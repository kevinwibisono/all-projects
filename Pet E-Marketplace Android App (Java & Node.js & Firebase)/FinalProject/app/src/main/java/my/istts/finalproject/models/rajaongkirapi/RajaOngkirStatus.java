package my.istts.finalproject.models.rajaongkirapi;

public class RajaOngkirStatus {
    private int code;
    private String description;

    public RajaOngkirStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
