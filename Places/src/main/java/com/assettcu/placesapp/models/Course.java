package com.assettcu.placesapp.models;

import java.io.Serializable;

/**
 * Created by Aaron on 5/6/2014.
 */


public class Course implements Serializable
{
    private Place place;
    private String name, className, classRoomName;
    private boolean isSelected;

    public Course(String b, String name)
    {
        this.name = b;
        this.className = name;
        isSelected = false;
    }

    public Course(String building, String className, String classRoom)
    {
        this.name = building;
        this.className = className;
        this.classRoomName = classRoom;
        isSelected = false;
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
        return name + "," + className + "," + classRoomName;
    }

    public String getClassName()
    {
        return className;
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

    public String getClassRoomName()
    {
        return classRoomName;
    }
}
