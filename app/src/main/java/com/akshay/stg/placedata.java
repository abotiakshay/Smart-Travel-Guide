package com.akshay.stg;

import java.io.Serializable;

public class placedata implements Serializable {
    String place_name,place_description,place_category,place_city,place_state,place_country,place_postalcode,place_img;

    public placedata(String place_name, String place_description, String place_category, String place_city, String place_state, String place_country, String place_postalcode, String place_img) {
        this.place_name = place_name;
        this.place_description = place_description;
        this.place_category = place_category;
        this.place_city = place_city;
        this.place_state = place_state;
        this.place_country = place_country;
        this.place_postalcode = place_postalcode;
        this.place_img = place_img;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_description() {
        return place_description;
    }

    public void setPlace_description(String place_description) {
        this.place_description = place_description;
    }

    public String getPlace_category() {
        return place_category;
    }

    public void setPlace_category(String place_category) {
        this.place_category = place_category;
    }

    public String getPlace_city() {
        return place_city;
    }

    public void setPlace_city(String place_city) {
        this.place_city = place_city;
    }

    public String getPlace_state() {
        return place_state;
    }

    public void setPlace_state(String place_state) {
        this.place_state = place_state;
    }

    public String getPlace_country() {
        return place_country;
    }

    public void setPlace_country(String place_country) {
        this.place_country = place_country;
    }

    public String getPlace_postalcode() {
        return place_postalcode;
    }

    public void setPlace_postalcode(String place_postalcode) {
        this.place_postalcode = place_postalcode;
    }

    public String getPlace_img() {
        return place_img;
    }

    public void setPlace_img(String place_img) {
        this.place_img = place_img;
    }
}
