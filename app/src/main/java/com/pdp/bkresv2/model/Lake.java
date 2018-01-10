package com.pdp.bkresv2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Phung Dinh Phuc on 29/07/2017.
 */

public class Lake implements Parcelable {
    public int LakeId;
    public String Name;
    public int HomeId;
    public String MapUrl;
    public String CreateTime;

    public Lake(int lakeId, String name, int homeId, String mapUrl, String createTime) {
        LakeId = lakeId;
        Name = name;
        HomeId = homeId;
        MapUrl = mapUrl;
        CreateTime = createTime;
    }

    protected Lake(Parcel in) {
        LakeId = in.readInt();
        Name = in.readString();
        HomeId = in.readInt();
        MapUrl = in.readString();
        CreateTime = in.readString();
    }

    public static final Creator<Lake> CREATOR = new Creator<Lake>() {
        @Override
        public Lake createFromParcel(Parcel in) {
            return new Lake(in);
        }

        @Override
        public Lake[] newArray(int size) {
            return new Lake[size];
        }
    };

    public int getLakeId() {
        return LakeId;
    }

    public void setLakeId(int lakeId) {
        LakeId = lakeId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getHomeId() {
        return HomeId;
    }

    public void setHomeId(int homeId) {
        HomeId = homeId;
    }

    public String getMapUrl() {
        return MapUrl;
    }

    public void setMapUrl(String mapUrl) {
        MapUrl = mapUrl;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(LakeId);
        dest.writeString(Name);
        dest.writeInt(HomeId);
        dest.writeString(MapUrl);
        dest.writeString(CreateTime);
    }
}
