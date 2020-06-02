package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import models.AppModel;
import utils.Util;

/**
 * Created by DS on 2/14/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "AppManager";
    // Table Names
    private static final String TABLE_APP_MANAGER = "app_manager";

    public interface TABLE_APP_MANAGER_COL {
        String PNAME = "pname";
        String NAME = "name";
        String APP_STATUS = "app_status";
        String UPDATE_COUNT = "update_count";
        String LAST_MODIFIED = "last_modified";
        String ICON = "app_icon";
    }

    // Table Create Statements
    private static final String CREATE_TABLE_APP_MANAGER = "CREATE TABLE "
            + TABLE_APP_MANAGER + "("
            + TABLE_APP_MANAGER_COL.PNAME + " TEXT PRIMARY KEY,"
            + TABLE_APP_MANAGER_COL.NAME + " TEXT,"
            + TABLE_APP_MANAGER_COL.APP_STATUS + " TEXT,"
            + TABLE_APP_MANAGER_COL.UPDATE_COUNT + " TEXT,"
            + TABLE_APP_MANAGER_COL.LAST_MODIFIED + " TEXT,"
            + TABLE_APP_MANAGER_COL.ICON + " BLOB"
            + ")";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private static DatabaseHelper databaseHelper;
    static Context context;

    public static DatabaseHelper getInstance(Context c) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(c);
            context = c;
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_MANAGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_MANAGER);
        onCreate(db);
    }

    public long insertAppData(String pname, String name, String appstatus, String updatecount, String lastmodified, byte[] icon) {
        try{
            ContentValues initialValues = new ContentValues();
            initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
            initialValues.put(TABLE_APP_MANAGER_COL.NAME, name.trim());
            initialValues.put(TABLE_APP_MANAGER_COL.APP_STATUS, appstatus.trim());
            initialValues.put(TABLE_APP_MANAGER_COL.UPDATE_COUNT, updatecount.trim());
            initialValues.put(TABLE_APP_MANAGER_COL.LAST_MODIFIED, lastmodified.trim());
            initialValues.put(TABLE_APP_MANAGER_COL.ICON, icon);
            return open().insert(TABLE_APP_MANAGER, null, initialValues);
        }catch ( Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public ArrayList<AppModel> getAppDetails(String status) {
        SQLiteDatabase db = open();
        ArrayList<AppModel> appModelArrayList = new ArrayList<AppModel>();

        Cursor cursor = db.query(TABLE_APP_MANAGER, null, TABLE_APP_MANAGER_COL.APP_STATUS + " = ? ", new String[]{status}, null, null, null);
        if (cursor == null && cursor.getColumnCount() == 0 && cursor.getCount() == 0) {
            return null;
        }
        Log.d("Test123", "cursor.size() " + cursor.getCount());
        while(cursor.moveToNext()){
            String pname = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.PNAME));
            String name = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.NAME));
            String appStatus = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.APP_STATUS));
            String updateCount = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.UPDATE_COUNT));
            String lastModified = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.LAST_MODIFIED));

            AppModel appModel = new AppModel();
            if( appStatus.trim().equalsIgnoreCase("1")){
                byte[] icon = cursor.getBlob(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.ICON));
                appModel.setIcon(Util.getPhoto(icon));
            }
            appModel.setPname(pname);
            appModel.setName(name);
            appModel.setAppStatus(appStatus);
            appModel.setUpdateCount(updateCount);
            appModel.setLastModified(lastModified);
//            if(Utils.getPhoto(icon) == null){
//                Drawable icon = R.id.ic_l;
//                Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
//                appModel.setIcon(R.id);
//            }else {
//                appModel.setIcon(iconBitmap /*Utils.getPhoto(icon)*/);
//            }
            appModelArrayList.add(appModel);
        }
//        Log.d("Test123", "appModelArrayList.size() = " + appModelArrayList.size());
        return appModelArrayList;
    }

    public boolean isPackageAvailable(String pname) {
        SQLiteDatabase db = open();
        ArrayList<AppModel> appModelArrayList = new ArrayList<AppModel>();

        Cursor cursor = db.query(TABLE_APP_MANAGER, null, TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname}, null, null, null);
//        Log.d("Test123","cursor.size() " + cursor.getCount());
        if (cursor == null && cursor.getCount() == 0) {
            return false;
        }
        if(cursor.getCount() > 0){
            return  true;
        } else {
            return false;
        }
    }

    public boolean isDBAvailable() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query(TABLE_APP_MANAGER, null,null, null, null, null, null);
//        Log.d("Test123","cursor.size() " + cursor.getCount());
        if (cursor == null && cursor.getCount() == 0) {
            return false;
        }
        if(cursor.getCount() > 0){
            return  true;
        } else {
            return false;
        }
    }


    public void updateAppUpdateCount(String pname,String count){
        ContentValues initialValues = new ContentValues();
        initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
        initialValues.put(TABLE_APP_MANAGER_COL.UPDATE_COUNT, count);
        open().update(TABLE_APP_MANAGER, initialValues, TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname});
    }

    public String getAppUpdateCount(String pname){

        ContentValues initialValues = new ContentValues();
        initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
        Cursor cursor = open().query(TABLE_APP_MANAGER,null,TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname},null,null,null);
        if (cursor == null && cursor.getColumnCount() == 0 && cursor.getCount() == 0) {
            return null;
        }
        String updateCount = null;
        if(cursor.moveToFirst()) {
            updateCount = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.UPDATE_COUNT));
            AppModel appModel = new AppModel();
            appModel.setUpdateCount(updateCount);
        }
        return updateCount;
    }

    public void updateAppModificationDate(String pname,String date){
        ContentValues initialValues = new ContentValues();
        initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
        initialValues.put(TABLE_APP_MANAGER_COL.LAST_MODIFIED, date);
        open().update(TABLE_APP_MANAGER, initialValues, TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname});
    }

    public void getAppModificationDate(String pname,String date){
        ContentValues initialValues = new ContentValues();
        initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
        initialValues.put(TABLE_APP_MANAGER_COL.LAST_MODIFIED, date);
        Cursor cursor = open().query(TABLE_APP_MANAGER,null,TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname},null,null,null);

        if (cursor == null && cursor.getColumnCount() == 0 && cursor.getCount() == 0) {
            return;
        }
        if(cursor.moveToFirst()) {
            String lastModified = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.LAST_MODIFIED));
            AppModel appModel = new AppModel();
            appModel.setLastModified(lastModified);
        }
    }

    public void updateAppStatus(String pname, String status/*, byte[] icon*/){
//       Log.d("Test123" ,pname +" - "+ status );

        ContentValues initialValues = new ContentValues();
        initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
        initialValues.put(TABLE_APP_MANAGER_COL.APP_STATUS, status);
//        initialValues.put(TABLE_APP_MANAGER_COL.ICON, icon);
        open().update(TABLE_APP_MANAGER, initialValues, TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname});
    }

    public void getAppStatus(String pname, String status){
        ContentValues initialValues = new ContentValues();
        initialValues.put(TABLE_APP_MANAGER_COL.PNAME, pname.trim());
        initialValues.put(TABLE_APP_MANAGER_COL.APP_STATUS, status);
        Cursor cursor = open().query(TABLE_APP_MANAGER,null,TABLE_APP_MANAGER_COL.PNAME + " = ? ", new String[]{pname},null,null,null);

        if (cursor == null && cursor.getColumnCount() == 0 && cursor.getCount() == 0) {
            return;
        }
        if(cursor.moveToFirst()) {
            String appStatus = cursor.getString(cursor.getColumnIndex(TABLE_APP_MANAGER_COL.APP_STATUS));
            AppModel appModel = new AppModel();
            appModel.setAppStatus(appStatus);
        }
    }

    public void close() {

        this.close();
    }

    public SQLiteDatabase open() {

        return getWritableDatabase();
    }
}
