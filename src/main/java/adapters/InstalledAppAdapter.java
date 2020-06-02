package adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ds.com.advanceappmanager.R;
import models.AppModel;

/**
 * Created by DS on 2/14/2016.
 */
public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.InstalledAppViewHolder> {

    private Context mContext;

    public void setAppList(List<AppModel> mAppList,Context context) {
        mContext = context;
        this.mAppList = mAppList;
    }

    public List<AppModel> getmAppList() {
        return mAppList;
    }

    List<AppModel> mAppList = new ArrayList<AppModel>() ;

    @Override
    public InstalledAppViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.installed_app_card, viewGroup, false);
        return new  InstalledAppViewHolder(itemView,position);
    }

    @Override
    public void onBindViewHolder(InstalledAppViewHolder installedAppViewHolder, final int position) {

        AppModel appModel = mAppList.get(position);
        installedAppViewHolder.appName.setText(appModel.getName());
        final PackageManager pm = mContext.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo( appModel.getPname(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            appInfo = null;
        }
        Bitmap iconBitmap = null;
        if(appInfo != null ){
            final Drawable icon = appInfo.loadIcon(pm);
            iconBitmap = ((BitmapDrawable)icon).getBitmap();
        }
//        installedAppViewHolder.icon.setImageBitmap(appModel.getIcon());
        installedAppViewHolder.icon.setImageBitmap(iconBitmap);
        installedAppViewHolder.icon.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAppItemClickListener.onAppItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {

        return mAppList.size();
    }

    OnAppItemClickListener onAppItemClickListener;

    public interface OnAppItemClickListener {
        public void onAppItemClick(int position);
    }


    public void setOnAppItemClickListener(final OnAppItemClickListener mOnAppItemClickListener) {
        this.onAppItemClickListener = mOnAppItemClickListener;
    }

    class InstalledAppViewHolder extends RecyclerView.ViewHolder {
        protected ImageView icon;
        protected TextView appName;

        public InstalledAppViewHolder( View v ,final int position ){
            super(v);
            icon = (ImageView)v.findViewById(R.id.app_icon);
            appName = (TextView)v.findViewById(R.id.app_name);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                if( onAppItemClickListener != null){
//                    onAppItemClickListener.onAppItemClick(view,position);
//                }
                }
            });
        }

    }
}
