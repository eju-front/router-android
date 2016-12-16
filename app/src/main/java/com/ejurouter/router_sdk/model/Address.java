package com.ejurouter.router_sdk.model;

import java.io.Serializable;

/**
 * Created by SidneyXu on 2016/12/01.
 */

public class Address implements Serializable {

    private String detail;
    private int roomNumber;

    public Address() {
    }

    public Address(String detail, int roomNumber) {
        this.detail = detail;
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Address{");
        sb.append("detail='").append(detail).append('\'');
        sb.append(", roomNumber=").append(roomNumber);
        sb.append('}');
        return sb.toString();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
