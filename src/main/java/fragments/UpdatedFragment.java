package fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import adapters.UpdatedAppAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DatabaseHelper;
import ds.com.advanceappmanager.R;
import models.AppModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatedFragment extends Fragment {

    ArrayList<AppModel> mAppModelList;
    UpdatedAppAdapter updatedAppAdapter;

    public UpdatedFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int i) {
        UpdatedFragment fragment = new UpdatedFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void loadData(){
        mAppModelList = DatabaseHelper.getInstance(getActivity()).getAppDetails("0");
        Collections.sort(mAppModelList, new Comparator<AppModel>() {
            @Override
            public int compare(AppModel appName1, AppModel appName2) {
                String s1 = appName1.getName();
                String s2 = appName2.getName();
                Collator collator = Collator.getInstance();
                collator.setStrength(Collator.SECONDARY);
                return collator.compare(s1.toLowerCase(), s2.toLowerCase());
            }
        });
        updatedAppAdapter.setAppList(mAppModelList,getActivity());
        updatedAppAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_updated, container, false);
//        mAppModelList = DatabaseHelper.getInstance(getActivity()).getAppDetails("0");

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.updated_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        updatedAppAdapter = new UpdatedAppAdapter();
        updatedAppAdapter.setOnUpdatedAppItemClickListener(new UpdatedAppAdapter.OnUpdatedAppItemClickListener() {
            @Override
            public void onUpdatedAppClick(int position) {
                String packageName = updatedAppAdapter.getmAppList().get(position).getPname();
                Intent intent = new Intent();
                if(appInstalledOrNot(packageName)) {
                    if (Build.VERSION.SDK_INT >= 9) {
                        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + packageName));
                    } else {
                        final String appPkgName = (Build.VERSION.SDK_INT == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");

                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                        intent.putExtra(appPkgName, packageName);
                    }
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(updatedAppAdapter);
        return rootView;
    }
    public boolean appInstalledOrNot(String app){
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try
        {
            pm.getPackageInfo(app, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed ;
    }

}
