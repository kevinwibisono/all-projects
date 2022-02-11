package my.istts.finalproject.models.ipaymu.cstore;

public class ResultCstore {
    private String status;
    private String sessionID;
    private int trx_id;
    private String channel;
    private String kode_pembayaran;
    private String expired;
    private int reference_id;
    private String keterangan;

    public ResultCstore(String status, String sessionID, int trx_id, String channel, String kode_pembayaran, String expired, int reference_id, String keterangan) {
        this.status = status;
        this.sessionID = sessionID;
        this.trx_id = trx_id;
        this.channel = channel;
        this.kode_pembayaran = kode_pembayaran;
        this.expired = expired;
        this.reference_id = reference_id;
        this.keterangan = keterangan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public int getTrx_id() {
        return trx_id;
    }

    public void setTrx_id(int trx_id) {
        this.trx_id = trx_id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getKode_pembayaran() {
        return kode_pembayaran;
    }

    public void setKode_pembayaran(String kode_pembayaran) {
        this.kode_pembayaran = kode_pembayaran;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public int getReference_id() {
        return reference_id;
    }

    public void setReference_id(int reference_id) {
        this.reference_id = reference_id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
