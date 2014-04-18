package com.assettcu.placesapp.models;

/**
 * file: Place
 * by: Derek Baumgartner
 * created: 4/18/2014.
 */
public class Place {
    private int placeid;
    private String placename;
    private String building_code;
    private String image_url;

    public Place() {}

    public Place(int placeid, String placename, String building_code, String image_url){
        this.placeid = placeid;
        this.placename = placename;
        this.building_code = building_code;
        this.image_url = image_url;
    }

    // Place ID
    public int getPlaceid(){
        return this.placeid;
    }

    public void setPlaceid(int placeid){
        this.placeid = placeid;
    }

    // Place Name
    public String getPlacename(){
        return this.placename;
    }

    public void setPlacename(String placename){
        this.placename = placename;
    }

    // Building Code
    public String getBuildingCode(){
        return this.building_code;
    }

    public void setBuilding_code(String building_code){
        this.building_code = building_code;
    }

    // Image URL
    public String getImage_url(){
        return this.image_url;
    }

    public void setImage_url(String image_url){
        this.image_url = image_url;
    }
}
