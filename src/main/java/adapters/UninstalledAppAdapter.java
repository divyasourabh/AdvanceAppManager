package adapters;

import android.content.Context;
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
public class UninstalledAppAdapter extends RecyclerView.Adapter<UninstalledAppAdapter.UninstalledViewHolder> {
    private Context mContext;
    public void setAppList(List<AppModel> mAppList) {
        this.mAppList = mAppList;
    }

    List<AppModel> mAppList = new ArrayList<AppModel>() ;

    OnAppItemClickListener onAppItemClickListener;

    public interface OnAppItemClickListener {
        public void onAppItemClick(int position,String pname);
    }


    public void setOnAppItemClickListener(final OnAppItemClickListener mOnAppItemClickListener) {
        this.onAppItemClickListener = mOnAppItemClickListener;
    }

    @Override
    public UninstalledViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.uninstalled_app_card, viewGroup, false);
        return new UninstalledViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UninstalledViewHolder installedAppViewHolder, final int position) {
        final AppModel appModel = mAppList.get(position);
        installedAppViewHolder.icon.setImageBitmap(appModel.getIcon());
        installedAppViewHolder.appName.setText(appModel.getName());
        installedAppViewHolder.uninstallDate.setText(appModel.getLastModified());
        installedAppViewHolder.icon.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAppItemClickListener.onAppItemClick(position, appModel.getPname());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }

    class UninstalledViewHolder extends RecyclerView.ViewHolder {

        protected ImageView icon;
        protected TextView appName;
        protected TextView uninstallDate;

        UninstalledViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.app_icon);
            appName = (TextView) v.findViewById(R.id.app_name);
            uninstallDate = (TextView) v.findViewById(R.id.uninstalled_date);
        }
    }
}
