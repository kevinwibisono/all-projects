package my.istts.finalproject.models.ipaymu.va;

public class ResultVA {
    private int Status;
    private String Keterangan;
    private int id;
    private String va;
    private String displayName;
    private int referenceId;
    private String expiredAt;

    public ResultVA(int status, String keterangan, int id, String va, String displayName, int referenceId, String expiredAt) {
        Status = status;
        Keterangan = keterangan;
        this.id = id;
        this.va = va;
        this.displayName = displayName;
        this.referenceId = referenceId;
        this.expiredAt = expiredAt;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVa() {
        return va;
    }

    public void setVa(String va) {
        this.va = va;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override
    public String toString() {
        return "ResultVA{" +
                "Status=" + Status +
                ", Keterangan='" + Keterangan + '\'' +
                ", id=" + id +
                ", va='" + va + '\'' +
                ", displayName='" + displayName + '\'' +
                ", referenceId=" + referenceId +
                ", expiredAt='" + expiredAt + '\'' +
                '}';
    }
}
