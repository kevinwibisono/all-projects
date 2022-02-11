package my.istts.finalproject.models.rajaongkirapi;

import java.util.List;

public class PaketKurir {
    private String service;
    private String description;
    private List<PaketKurirHarga> cost;

    public PaketKurir(String service, String description, List<PaketKurirHarga> cost) {
        this.service = service;
        this.description = description;
        this.cost = cost;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PaketKurirHarga> getCost() {
        return cost;
    }

    public void setCost(List<PaketKurirHarga> cost) {
        this.cost = cost;
    }
}
