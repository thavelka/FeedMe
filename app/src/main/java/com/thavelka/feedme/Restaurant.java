package com.thavelka.feedme;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    public String getName() {
        return getString("name");
    }

    public String getAddress() {
        return getString("address");
    }

    public String getImage() {
        return getString("imageUrl");
    }

}