package com.example.sellerapp.models.herejsonaddress;

import java.util.List;

public class FieldScoreAlamatHere {
    private List<Double> streets;
    private double houseNumber;

    public FieldScoreAlamatHere(List<Double> streets, double houseNumber) {
        this.streets = streets;
        this.houseNumber = houseNumber;
    }

    public List<Double> getStreets() {
        return streets;
    }

    public void setStreets(List<Double> streets) {
        this.streets = streets;
    }

    public double getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(double houseNumber) {
        this.houseNumber = houseNumber;
    }
}
