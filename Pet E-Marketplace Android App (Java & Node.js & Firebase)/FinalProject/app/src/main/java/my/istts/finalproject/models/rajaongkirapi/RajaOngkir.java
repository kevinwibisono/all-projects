package my.istts.finalproject.models.rajaongkirapi;

import java.util.List;

public class RajaOngkir {
    private RajaOngkirQuery query;
    private RajaOngkirStatus status;
    private RajaOngkirPlace origin_details;
    private RajaOngkirPlace destination_details;
    private List<RajaOngkirKurir> results;

    public RajaOngkir(RajaOngkirQuery query, RajaOngkirStatus status, RajaOngkirPlace origin_details, RajaOngkirPlace destination_details, List<RajaOngkirKurir> results) {
        this.query = query;
        this.status = status;
        this.origin_details = origin_details;
        this.destination_details = destination_details;
        this.results = results;
    }

    public RajaOngkirQuery getQuery() {
        return query;
    }

    public void setQuery(RajaOngkirQuery query) {
        this.query = query;
    }

    public RajaOngkirStatus getStatus() {
        return status;
    }

    public void setStatus(RajaOngkirStatus status) {
        this.status = status;
    }

    public RajaOngkirPlace getOrigin_details() {
        return origin_details;
    }

    public void setOrigin_details(RajaOngkirPlace origin_details) {
        this.origin_details = origin_details;
    }

    public RajaOngkirPlace getDestination_details() {
        return destination_details;
    }

    public void setDestination_details(RajaOngkirPlace destination_details) {
        this.destination_details = destination_details;
    }

    public List<RajaOngkirKurir> getResults() {
        return results;
    }

    public void setResults(List<RajaOngkirKurir> results) {
        this.results = results;
    }
}
