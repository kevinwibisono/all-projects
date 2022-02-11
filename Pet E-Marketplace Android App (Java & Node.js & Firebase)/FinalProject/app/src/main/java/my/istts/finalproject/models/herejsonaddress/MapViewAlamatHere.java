package my.istts.finalproject.models.herejsonaddress;

public class MapViewAlamatHere {
    private double west;
    private double south;
    private double east;
    private double north;

    public MapViewAlamatHere(double west, double south, double east, double north) {
        this.west = west;
        this.south = south;
        this.east = east;
        this.north = north;
    }

    public double getWest() {
        return west;
    }

    public void setWest(double west) {
        this.west = west;
    }

    public double getSouth() {
        return south;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public double getEast() {
        return east;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public double getNorth() {
        return north;
    }

    public void setNorth(double north) {
        this.north = north;
    }
}
