package com.example.sellerapp.models.herejsonaddress;

public class DetailAlamatHere {
    private String label;
    private String countryCode;
    private String countryName;
    private String country;
    private String city;
    private String district;
    private String subdistrict;
    private String street;
    private String postalCode;
    private String houseNumber;

    public DetailAlamatHere(String label, String countryCode, String countryName, String country, String city, String district, String subdistrict, String street, String postalCode, String houseNumber) {
        this.label = label;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.country = country;
        this.city = city;
        this.district = district;
        this.subdistrict = subdistrict;
        this.street = street;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(String subdistrict) {
        this.subdistrict = subdistrict;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
