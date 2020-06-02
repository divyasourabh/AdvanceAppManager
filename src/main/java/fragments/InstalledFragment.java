package fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import adapters.InstalledAppAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DatabaseHelper;
import ds.com.advanceappmanager.R;
import models.AppModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstalledFragment extends Fragment {

    ArrayList<AppModel> mAppModelList;
    InstalledAppAdapter installedAppAdapter;

    public InstalledFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int i) {
        InstalledFragment fragment = new InstalledFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void loadData() {
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
        installedAppAdapter.setAppList(mAppModelList, getActivity());
        installedAppAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_installed, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.installed_recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 3);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        installedAppAdapter = new InstalledAppAdapter();
        installedAppAdapter.setOnAppItemClickListener(new InstalledAppAdapter.OnAppItemClickListener() {
            @Override
            public void onAppItemClick(int position) {
                Intent launchIntent;
                PackageManager manager = getActivity().getPackageManager();
                try {
                    launchIntent = manager.getLaunchIntentForPackage(installedAppAdapter.getmAppList().get(position).getPname());
                    if (launchIntent == null)
                        throw new PackageManager.NameNotFoundException();
                    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(launchIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "Application not install.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    //						Toast.makeText(getApplicationContext(), "in catch", Toast.LENGTH_LONG).show();
                }
            }
        });
        recyclerView.setAdapter(installedAppAdapter);
        return rootView;
    }


}
