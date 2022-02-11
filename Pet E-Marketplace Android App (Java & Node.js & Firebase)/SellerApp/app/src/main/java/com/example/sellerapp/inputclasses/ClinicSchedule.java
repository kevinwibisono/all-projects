package com.example.sellerapp.inputclasses;

public class ClinicSchedule {
    private String day;
    private Boolean open;
    private String openHourMinute;
    private String closeHourMinute;

    public ClinicSchedule(String day, Boolean open, String openHourMinute, String closeHourMinute) {
        this.day = day;
        this.open = open;
        this.openHourMinute = openHourMinute;
        this.closeHourMinute = closeHourMinute;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Boolean isOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getOpenHourMinute() {
        return openHourMinute;
    }

    public void setOpenHourMinute(String openHourMinute) {
        this.openHourMinute = openHourMinute;
    }

    public String getCloseHourMinute() {
        return closeHourMinute;
    }

    public void setCloseHourMinute(String closeHourMinute) {
        this.closeHourMinute = closeHourMinute;
    }

}
