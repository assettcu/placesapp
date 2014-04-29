package com.assettcu.placesapp.models;

import java.io.Serializable;

/**
 * file: Place
 * by: Derek Baumgartner
 * created: 4/18/2014.
 */
public class Place implements Serializable
{
    private int placeID;
    private String placeName;
    private String buildingCode;
    private String imageURL;
    private String latitude;
    private String longitude;

    public Place() {}

    public Place(int placeId, String placeName, String buildingCode, String imageURL){
        this.placeID = placeId;
        this.placeName = placeName;
        this.buildingCode = buildingCode;
        this.imageURL = imageURL;
    }

    //Latitude
    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    //Longitude
    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    // Place ID
    public int getPlaceID(){
        return this.placeID;
    }

    public void setPlaceID(int placeID){
        this.placeID = placeID;
    }

    // Place Name
    public String getPlaceName(){
        return this.placeName;
    }

    public void setPlaceName(String placeName){
        this.placeName = placeName;
    }

    // Building Code
    public String getBuildingCode(){
        return this.buildingCode;
    }

    public void setBuildingCode(String buildingCode){
        this.buildingCode = buildingCode;
    }

    // Image URL
    public String getImageURL(){
        return this.imageURL;
    }

    public void setImageURL(String image_url){
        this.imageURL = image_url;
    }
}
