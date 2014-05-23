package com.assettcu.placesapp.models;

import java.io.Serializable;

/**
 * file: Room
 * by: Derek Baumgartner
 * created: 5/19/2014.
 */
public class Room implements Serializable {

    private int roomId;
    private String roomName;
    private String buildingCode;
    private String roomImageURL;

    public Room() {}

    public Room(int roomId, String buildingCode, String roomName, String roomImageURL) {
        this.roomId = roomId;
        this.buildingCode = buildingCode;
        this.roomName = roomName;
        this.roomImageURL = roomImageURL;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getBuildingCode() {
        return this.buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomImageURL() {
        return this.roomImageURL;
    }

    public void setRoomImageURL(String roomImageURL) {
        this.roomImageURL = roomImageURL;
    }
}
