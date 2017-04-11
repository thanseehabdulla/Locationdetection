package com.app.ats.com.myloco.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


public class LocatorModel implements Parcelable {
    int numid;
    String id;
    String name;
    String address_l1;
    String city;
    String country;
    String zip;
    String latitude;
    String longitude;
    String user_id;
    String status;
    String category_id;
    String phone;
    String mobile;
    String date;
    private int read;
    private String locality;
    String senderName;
    String editor_id;
    String email;
    String website;
    String description;
    String short_description;
    String working_hours;
    String show_dash;
    String image_id;
    String image;
    String first_name;


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }

    public String getShow_dash() {
        return show_dash;
    }

    public void setShow_dash(String show_dash) {
        this.show_dash = show_dash;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;

    }

    public String getEditor_id() {
        return editor_id;
    }

    public void setEditor_id(String editor_id) {
        this.editor_id = editor_id;
    }

    public int getNumid() {
        return numid;
    }

    public void setNumid(int numid) {
        this.numid = numid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress_l1() {
        return address_l1;
    }

    public void setAddress_l1(String address_l1) {
        this.address_l1 = address_l1;
    }

    public String getCity() {
        return city;
    }

    public String setCity(String city) {
        this.city = city;
        return city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        super.toString();
        // String jsonString = "{";
        // JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("city", city);
            obj.put("country", country);
            obj.put("locality", locality);
            obj.put("senderName", senderName);
            obj.put("id", id);
            obj.put("name", name);
            obj.put("address_l1", address_l1);
            obj.put("zip", zip);
            obj.put("user_id", user_id);
            obj.put("status", status);
            obj.put("category_id", category_id);
            obj.put("editor_id", editor_id);
            obj.put("website", website);
            obj.put("description", description);
            obj.put("short_description", short_description);
            obj.put("working_hours", working_hours);
            obj.put("show_dash", show_dash);
            obj.put("image_id", image_id);
            obj.put("image", image);
            obj.put("phone", phone);
            obj.put("mobile", mobile);
            obj.put("email", email);

        } catch (JSONException e) {
            //e.printStackTrace();
        }
        //jsonString += "}";
        return obj.toString();
    }

    public LocatorModel() {
    }

    public LocatorModel(Parcel in) {
        String[] data = new String[23];

        in.readStringArray(data);

        this.id = data[0];
        this.name = data[1];
        this.address_l1 = data[2];
        this.city = data[3];
        this.country = data[4];
        this.zip = data[5];
        this.latitude = data[6];
        this.longitude = data[7];
        this.user_id = data[8];
        this.status = data[9];
        this.category_id = data[10];
        this.editor_id = data[11];
        this.website = data[12];
        this.description = data[13];
        this.short_description = data[14];
        this.working_hours = data[15];
        this.show_dash = data[16];
        this.phone = data[17];
        this.mobile= data[18];
        this.image_id = data[19];
        this.image = data[20];
        this.email = data[21];
        this.senderName = data[22];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.id,
                this.name,
                this.address_l1,
                this.city,
                this.country,
                this.zip,
                this.latitude,
                this.longitude,
                this.user_id,
                this.status,
                this.category_id,
                this.editor_id,
                this.website,
                this.description,
                this.short_description,
                this.working_hours,
                this.show_dash,
                this.phone,
                this.mobile,
                this.image_id,
                this.image,
                this.email,
                this.senderName
        });
    }

    public static final Creator CREATOR = new Creator() {
        public LocatorModel createFromParcel(Parcel in) {
            return new LocatorModel(in);
        }

        public LocatorModel[] newArray(int size) {
            return new LocatorModel[size];
        }
    };


    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }


    public void getLocality(String locality) {


    }

    public void setName(String city, String country) {

    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String firstname) {
        this.first_name = firstname;
    }
}