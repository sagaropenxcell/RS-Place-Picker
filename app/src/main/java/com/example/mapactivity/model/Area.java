package com.example.mapactivity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Area implements Parcelable {

    @SerializedName("main_area_id")
    private int id;
    @SerializedName("area_name")
    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    protected Area(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Area> CREATOR = new Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel in) {
            return new Area(in);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }


    public static int getSelectedAreaPosition(Area area, ArrayList<Area> areas) {
        if (areas != null) {
            for (int i = 0; i < areas.size(); i++) {
                if (areas.get(i).getId() == area.getId()) {
                    return i;
                }
            }
        }
        return 0;
    }
}
