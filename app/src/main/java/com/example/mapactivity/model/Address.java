package com.example.mapactivity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mapactivity.helpers.Geocode;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Address implements Parcelable {

    public static final String TYPE_HOUSE = "house";
    public static final String TYPE_BUILDING = "other";

    @SerializedName(value = "main_address_id", alternate = {"address_id"})
    private int address_id;
    private String address_name;
    private Area area;
    private String address_type;
    private boolean is_default;
    private String street;
    private String jadda;
    private String block_no;

    @SerializedName("building_no")
    private String building;
    @SerializedName("house_number")
    private String house;

    private String floor_no;
    @SerializedName("landmark")
    private String extra_direction;
    private String formatted_address;
    private String lat;
    private String lng;

    private HashMap<String, String> addressMap;

    public Address() {
    }


    protected Address(Parcel in) {
        address_id = in.readInt();
        address_name = in.readString();
        area = in.readParcelable(Area.class.getClassLoader());
        address_type = in.readString();
        is_default = in.readByte() != 0;
        street = in.readString();
        jadda = in.readString();
        block_no = in.readString();
        building = in.readString();
        house = in.readString();
        floor_no = in.readString();
        extra_direction = in.readString();
        formatted_address = in.readString();
        lat = in.readString();
        lng = in.readString();

        int size = in.readInt();
        if (size > 0) {
            addressMap = new HashMap<>();
        }
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            String value = in.readString();
            addressMap.put(key, value);
        }
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }


    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getAddress_type() {
        if (address_type == null) {
            address_type = TYPE_HOUSE;
        }
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getJadda() {
        return jadda;
    }

    public void setJadda(String jadda) {
        this.jadda = jadda;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBlock_no() {
        return block_no;
    }

    public void setBlock_no(String block_no) {
        this.block_no = block_no;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public boolean is_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public String getFloor_no() {
        return floor_no;
    }

    public void setFloor_no(String floor_no) {
        this.floor_no = floor_no;
    }

    public String getExtra_direction() {
        return extra_direction;
    }

    public void setExtra_direction(String extra_direction) {
        this.extra_direction = extra_direction;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public double getLat() {
        if (lat == null || lat.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(lat);
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public double getLng() {
        if (lng == null || lng.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(lng);
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    public String getFormatted_address() {
        return appendComma(area == null ? "" : area.getName()) +
                appendComma(block_no) +
                appendComma(street) +
                appendComma(jadda) +
                appendComma(building) +
                appendComma(floor_no);
    }

    private static String appendComma(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        return data + ", ";
    }

    private static String appendNewLine(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        return "\n" + data;
    }


    public static int getSelectedAreaPosition(String area, ArrayList<Area> areas) {
        if (areas != null) {
            for (int i = 0; i < areas.size(); i++) {
                if (areas.get(i).getName().equalsIgnoreCase(area)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public void setAddressMap(HashMap<String, String> addressMap) {
        this.addressMap = addressMap;
        this.street = addressMap.get(Geocode.street_number);
        String route = addressMap.get(Geocode.route);

        if (isValidLine(route)) {
            if (isValidLine(street)) {
                street = street + ", ";
            }
            street = route;
        }

        this.house = addressMap.get(Geocode.premise);
        this.block_no = addressMap.get(Geocode.locality);
        //this.country = addressMap.get(Geocode.country);

        String latitude = addressMap.get(Geocode.lat);
        String longitude = addressMap.get(Geocode.lng);
        if (latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()) {
            this.lat = latitude;
            this.lng = longitude;
        }

        String area2 = addressMap.get(Geocode.sublocality_level_2);
        if (isValidLine(area2)) {
            block_no = area2;
            String area3 = addressMap.get(Geocode.sublocality_level_3);
            if (isValidLine(area3)) {
                block_no = area3 + ", " + block_no;
            }
        }

        String admin_area_level_1 = addressMap.get(Geocode.administrative_area_level_1);
        if (isValidLine(admin_area_level_1)) {
            jadda = admin_area_level_1;
            String admin_area_level_2 = addressMap.get(Geocode.administrative_area_level_2);
            if (isValidLine(admin_area_level_2)) {
                jadda = admin_area_level_2 + ", " + jadda;
                String admin_area_level_3 = addressMap.get(Geocode.administrative_area_level_3);
                if (isValidLine(admin_area_level_3)) {
                    jadda = admin_area_level_3 + ", " + jadda;
                }
            }
        }

        String sub_area = addressMap.get(Geocode.sublocality_level_1);
        if (isValidLine(sub_area)) {
            jadda = sub_area + ", " + jadda;

        }

        String postal_code = addressMap.get(Geocode.postal_code);
        if (isValidLine(postal_code)) {
            jadda = jadda + " - " + postal_code;
        }
    }

    private boolean isValidLine(String line) {
        return line != null && !line.isEmpty();
    }

    public HashMap<String, String> getAddressMap() {

        return addressMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(address_id);
        dest.writeString(address_name);
        dest.writeParcelable(area, flags);
        dest.writeString(address_type);
        dest.writeByte((byte) (is_default ? 1 : 0));
        dest.writeString(street);
        dest.writeString(jadda);
        dest.writeString(block_no);
        dest.writeString(building);
        dest.writeString(house);
        dest.writeString(floor_no);
        dest.writeString(extra_direction);
        dest.writeString(formatted_address);
        dest.writeString(lat);
        dest.writeString(lng);

        if (addressMap == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(addressMap.size());
            for (Map.Entry<String, String> entry : addressMap.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }
    }


    public boolean isHouse() {
        return getAddress_type().equalsIgnoreCase(TYPE_HOUSE);
    }

    public static int getSelectedAddressPosition(ArrayList<Address> addresses) {
        int index = 0;
        for (Address address : addresses) {
            if (address.is_default) {
                return index;
            }
            index++;
        }
        return index;
    }
}
