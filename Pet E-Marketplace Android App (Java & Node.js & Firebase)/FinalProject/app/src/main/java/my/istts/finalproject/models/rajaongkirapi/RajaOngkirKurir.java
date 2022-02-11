package my.istts.finalproject.models.rajaongkirapi;

import java.util.List;

public class RajaOngkirKurir {
    private String code;
    private String name;
    private List<PaketKurir> costs;

    public RajaOngkirKurir(String code, String name, List<PaketKurir> costs) {
        this.code = code;
        this.name = name;
        this.costs = costs;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PaketKurir> getCosts() {
        return costs;
    }

    public void setCosts(List<PaketKurir> costs) {
        this.costs = costs;
    }
}
