package my.istts.finalproject.models.rajaongkirapi;

public class RajaOngkirInput {
    private String origin;
    private String destination;
    private long weight;
    private String courier;

    public RajaOngkirInput(String origin, String destination, long weight, String courier) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.courier = courier;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }
}
