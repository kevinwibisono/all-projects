package my.istts.finalproject.models.rajaongkirapi;

public class PaketKurirHarga {
    private String value;
    private String etd;
    private String note;

    public PaketKurirHarga(String value, String etd, String note) {
        this.value = value;
        this.etd = etd;
        this.note = note;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
