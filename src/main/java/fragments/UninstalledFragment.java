package fragments;


import android.content.Intent;
import android.net.Uri;
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

import adapters.UninstalledAppAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DatabaseHelper;
import ds.com.advanceappmanager.R;
import models.AppModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class UninstalledFragment extends Fragment {

    ArrayList<AppModel> mAppModelList;
    UninstalledAppAdapter unInstalledAppAdapter;

    public UninstalledFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int i){
        UninstalledFragment fragment = new UninstalledFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,new IntentFilter(UninstalledFragment.class.getName()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    public void loadData(){
        mAppModelList = DatabaseHelper.getInstance(getActivity()).getAppDetails("1");
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
        unInstalledAppAdapter.setAppList(mAppModelList);
        unInstalledAppAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_uninstalled, container, false);
//        mAppModelList = DatabaseHelper.getInstance(getActivity()).getAppDetails("1");

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.uninstalled_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        unInstalledAppAdapter = new UninstalledAppAdapter();
        unInstalledAppAdapter.setOnAppItemClickListener(new UninstalledAppAdapter.OnAppItemClickListener() {
            @Override
            public void onAppItemClick(int position, String appPackageName) {
//                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                if (appPackageName == null || appPackageName.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),"Package not found.",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        recyclerView.setAdapter(unInstalledAppAdapter);
        return rootView;
    }


}
