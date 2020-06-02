package ds.com.advanceappmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import database.DatabaseHelper;
import fragments.InstalledFragment;
import fragments.UninstalledFragment;
import fragments.UpdatedFragment;
import utils.Util;

/**
 * Created by DS on 2/14/2016.
 */
public class AppReceiver extends BroadcastReceiver {
    public AppReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        Uri uri = intent.getData();
        String pkg = uri != null ? uri.getSchemeSpecificPart() : null;

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED)) {

            boolean checkPname = DatabaseHelper.getInstance(context).isPackageAvailable(pkg);

            if( checkPname ) {
                DatabaseHelper.getInstance(context).updateAppStatus(pkg,"0");
                DatabaseHelper.getInstance(context).updateAppModificationDate(pkg,Util.currentSystemDate());
            } else {
                final PackageManager pm = context.getPackageManager();
                ApplicationInfo appInfo;
                try {
                    appInfo = pm.getApplicationInfo( pkg, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    appInfo = null;
                }
                Bitmap iconBitmap = null;
                String applicationName = "(unknown)";
                if( appInfo != null ){
                    applicationName = (String) pm.getApplicationLabel(appInfo);
                    iconBitmap = getAppIcon(context,pkg);
                }
                long temp =DatabaseHelper.getInstance(context).insertAppData(appInfo.packageName.toString(), applicationName, "0", "0", Util.currentSystemDate(), Util.getBytes(iconBitmap));
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(InstalledFragment.class.getName()));//custom action
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(UninstalledFragment.class.getName()));//custom action

//            Toast.makeText(context,pkg + " - " +"AppManger OnReceive Called ACTION_PACKAGE_ADDED",Toast.LENGTH_LONG).show();

        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED)) {

            DatabaseHelper.getInstance(context).updateAppStatus(pkg,"1"/*,Utils.getBytes(getAppIcon(context,pkg))*/);
            DatabaseHelper.getInstance(context).updateAppModificationDate(pkg,Util.currentSystemDate());

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(UninstalledFragment.class.getName()));
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(InstalledFragment.class.getName()));
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(UpdatedFragment.class.getName()));

//            Toast.makeText(context,pkg + " - " +"AppManger OnReceive Called ACTION_UNINSTALL_PACKAGE",Toast.LENGTH_LONG).show();
        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED)) {

            String oldCount =  DatabaseHelper.getInstance(context).getAppUpdateCount(pkg);
            if( oldCount == null){
                DatabaseHelper.getInstance(context).getAppUpdateCount("0");
            }else {
                DatabaseHelper.getInstance(context).updateAppUpdateCount(pkg,(Integer.parseInt(oldCount)+ 1)+"");
//                DatabaseHelper.getInstance(context).getAppUpdateCount(Integer.parseInt(oldCount));
            }
            DatabaseHelper.getInstance(context).updateAppModificationDate(pkg, Util.currentSystemDate());
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(UpdatedFragment.class.getName()));

//            Toast.makeText(context,pkg + " - " +"AppManger OnReceive Called ACTION_PACKAGE_REPLACED",Toast.LENGTH_LONG).show();
        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
            Toast.makeText(context, "App manager updating database...", Toast.LENGTH_LONG).show();



        }else{
//            Toast.makeText(context,pkg + " - " +"receiver : in else part" ,Toast.LENGTH_LONG).show();
        }
    }


    public Bitmap getAppIcon(Context context,String pname){
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo( pname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            appInfo = null;
        }
        Bitmap iconBitmap = null;
        if( appInfo != null ){
            final Drawable icon = appInfo.loadIcon(pm);
            iconBitmap = ((BitmapDrawable)icon).getBitmap();
        }
        return iconBitmap;
    }
}
