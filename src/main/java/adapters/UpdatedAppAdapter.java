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
public class UpdatedAppAdapter extends RecyclerView.Adapter<UpdatedAppAdapter.UpdatedAppViewHolder> {

    private Context mContext;
    List<AppModel> mAppList = new ArrayList<AppModel>() ;

    public void setAppList(List<AppModel> mAppList,Context context) {
        this.mAppList = mAppList;
        mContext = context;
    }

    public List<AppModel> getmAppList() {
        return mAppList;
    }

    OnUpdatedAppItemClickListener onUpdatedAppItemClickListener;

    public interface OnUpdatedAppItemClickListener{
        public void onUpdatedAppClick(int position);
    }

    public void setOnUpdatedAppItemClickListener(OnUpdatedAppItemClickListener onUpdatedAppItemClickListener) {
        this.onUpdatedAppItemClickListener = onUpdatedAppItemClickListener;
    }


    @Override
    public UpdatedAppViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.updated_app_card, viewGroup, false);
        return new UpdatedAppViewHolder(itemView, position);
    }

    @Override
    public void onBindViewHolder(UpdatedAppViewHolder updatedAppViewHolder, final int position) {
        AppModel appModel = mAppList.get(position);
        updatedAppViewHolder.appName.setText(appModel.getName());
        updatedAppViewHolder.updateCount.setText("Updated " + appModel.getUpdateCount() + " time");
        updatedAppViewHolder.lastModified.setText(appModel.getLastModified());

        final PackageManager pm = mContext.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo( appModel.getPname(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            appInfo = null;
        }
        Bitmap iconBitmap = null;
        if( appInfo != null ){
            final Drawable icon = appInfo.loadIcon(pm);
            iconBitmap = ((BitmapDrawable)icon).getBitmap();
        }
        updatedAppViewHolder.icon.setImageBitmap(iconBitmap);
//        installedAppViewHolder.icon.setImageBitmap(appModel.getIcon());
        updatedAppViewHolder.icon.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdatedAppItemClickListener.onUpdatedAppClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }

    class UpdatedAppViewHolder extends RecyclerView.ViewHolder {

        protected ImageView icon;
        protected TextView appName;
        protected TextView updateCount;
        protected TextView lastModified;

        UpdatedAppViewHolder(View v, final int position) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.app_icon);
            appName = (TextView) v.findViewById(R.id.app_name);
            updateCount = (TextView) v.findViewById(R.id.update_count);
            lastModified = (TextView) v.findViewById(R.id.update_date);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    onUpdatedAppItemClickListener.onUpdatedAppClick(itemView, position);
                }
            });
        }
    }
}
