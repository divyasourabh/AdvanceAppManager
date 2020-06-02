package ds.com.advanceappmanager;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import database.DatabaseHelper;
import fragments.InstalledFragment;
import fragments.UninstalledFragment;
import fragments.UpdatedFragment;
import services.MyService;
import utils.Util;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        if (DatabaseHelper.getInstance(this).isDBAvailable()) {
            new LoadApplications(false).execute("");
        } else {
            new LoadApplications(true).execute("");
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        intentFilter.addAction(UpdatedFragment.class.getName());
        intentFilter.addAction(InstalledFragment.class.getName());
        intentFilter.addAction(UninstalledFragment.class.getName());
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);


//        Intent intent = new Intent(this, MyService.class);
//        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return InstalledFragment.newInstance(position + 1);
                case 1:
                    return UpdatedFragment.newInstance(position + 1);
                case 2:
                    return UninstalledFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
    private static PackageManager packageManager = null;
    private List<ApplicationInfo> installedAppsList = null;
    ArrayList<ApplicationInfo> applist;

    private class LoadApplications extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress = null;
        boolean isProgressNeedtoShow;

        private LoadApplications(boolean b) {
            isProgressNeedtoShow = b;
        }


        @Override
        protected Void doInBackground(String... params) {
            String queryText = params[0];
            packageManager = getApplication().getPackageManager();
            installedAppsList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            try {
                for (ApplicationInfo info : installedAppsList) {
                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                        Drawable icon = info.loadIcon(packageManager);
                        Bitmap iconBitmap = ((BitmapDrawable) icon).getBitmap();
                        long temp = db.insertAppData(info.packageName.toString(), info.loadLabel(packageManager).toString(), "0", "0", Util.currentSystemDate(), Util.getBytes(iconBitmap));
                        Log.d("Test123", "info.packageName.toString() = " + info.packageName.toString() +
                                " info.loadLabel(packageManager).toString() = " + info.loadLabel(packageManager).toString());
                        Log.d("Test123", "temp = " + temp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            updateAll();
            try {
                if (progress != null) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);


        }

        @Override
        protected void onPreExecute() {
            if (isProgressNeedtoShow)
                progress = ProgressDialog.show(MainActivity.this, null,
                        "updating database, it will take few seconds... ");

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Onreceive " + intent.getAction(), Toast.LENGTH_LONG).show();
            Log.d("Test123", "in Mainactivity : intent.getAction() : " + intent.getAction());
            if (intent.getAction().equalsIgnoreCase(UninstalledFragment.class.getName())) {
                updateUninstalledFragment();
            } else if (intent.getAction().equalsIgnoreCase(InstalledFragment.class.getName())) {
                updateInstalledFragment();
            } else if (intent.getAction().equalsIgnoreCase(UpdatedFragment.class.getName())) {
                updateUpdatedFragment();
            }
        }
    };

    private void updateAll() {
        updateUninstalledFragment();
        updateInstalledFragment();
        updateUpdatedFragment();
    }

    private void updateUninstalledFragment() {
        List<Fragment> a = getSupportFragmentManager().getFragments();
        for (Fragment f : a) {
            if (f instanceof UninstalledFragment) {
                UninstalledFragment uninstalledFragment = (UninstalledFragment) f;
                uninstalledFragment.loadData();
            }
        }
    }

    private void updateInstalledFragment() {
        List<Fragment> a = getSupportFragmentManager().getFragments();
        for (Fragment f : a) {
            if (f instanceof InstalledFragment) {
                InstalledFragment installedFragment = (InstalledFragment) f;
                installedFragment.loadData();
            }
        }
    }

    private void updateUpdatedFragment() {
        List<Fragment> a = getSupportFragmentManager().getFragments();
        for (Fragment f : a) {
            if (f instanceof UpdatedFragment) {
                UpdatedFragment updatedFragment = (UpdatedFragment) f;
                updatedFragment.loadData();
            }
        }

    }
}