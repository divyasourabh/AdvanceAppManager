package models;

import android.graphics.Bitmap;

/**
 * Created by DS on 2/14/2016.
 */
public class AppModel {

    private String pname;
    private String name;
    private String appStatus;
    private String updateCount;
    private String lastModified;
    private Bitmap icon;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(String updateCount) {
        this.updateCount = updateCount;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

}
