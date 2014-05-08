package com.assettcu.placesapp.models;

import java.io.Serializable;

/**
 * Created by Aaron on 5/6/2014.
 */


public class Course implements Serializable
{
    private Place place;
    private String name, cname;
    private boolean isSelected;

    public Course(Place place, String name)
    {
        this.name = name;
        this.place = place;
        isSelected = false;
    }

    public Course(String building, String className)
    {
        this.name = building;
        this.cname = className;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }

    public String toString()
    {
        return name + "," + cname;
    }

    public String getCname()
    {
        return cname;
    }

    public Place getPlace()
    {
        return place;
    }

    public void setPlace(Place place)
    {
        this.place = place;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
