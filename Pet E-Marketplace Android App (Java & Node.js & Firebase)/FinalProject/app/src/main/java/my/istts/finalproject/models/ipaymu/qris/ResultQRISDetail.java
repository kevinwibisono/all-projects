package my.istts.finalproject.models.ipaymu.qris;

public class ResultQRISDetail {
    private int SessionId;
    private int TransactionId;
    private int ReferenceId;
    private String Via;
    private String Channel;
    private String PaymentNo;
    private String QrString;
    private String PaymentName;
    private int Total;
    private int Fee;
    private String Expired;
    private String QrImage;
    private String QrTemplate;

    public ResultQRISDetail(int sessionId, int transactionId, int referenceId, String via, String channel, String paymentNo, String qrString, String paymentName, int total, int fee, String expired, String qrImage, String qrTemplate) {
        SessionId = sessionId;
        TransactionId = transactionId;
        ReferenceId = referenceId;
        Via = via;
        Channel = channel;
        PaymentNo = paymentNo;
        QrString = qrString;
        PaymentName = paymentName;
        Total = total;
        Fee = fee;
        Expired = expired;
        QrImage = qrImage;
        QrTemplate = qrTemplate;
    }

    public int getSessionId() {
        return SessionId;
    }

    public void setSessionId(int sessionId) {
        SessionId = sessionId;
    }

    public int getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(int transactionId) {
        TransactionId = transactionId;
    }

    public int getReferenceId() {
        return ReferenceId;
    }

    public void setReferenceId(int referenceId) {
        ReferenceId = referenceId;
    }

    public String getVia() {
        return Via;
    }

    public void setVia(String via) {
        Via = via;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }

    public String getPaymentNo() {
        return PaymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        PaymentNo = paymentNo;
    }

    public String getQrString() {
        return QrString;
    }

    public void setQrString(String qrString) {
        QrString = qrString;
    }

    public String getPaymentName() {
        return PaymentName;
    }

    public void setPaymentName(String paymentName) {
        PaymentName = paymentName;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public int getFee() {
        return Fee;
    }

    public void setFee(int fee) {
        Fee = fee;
    }

    public String getExpired() {
        return Expired;
    }

    public void setExpired(String expired) {
        Expired = expired;
    }

    public String getQrImage() {
        return QrImage;
    }

    public void setQrImage(String qrImage) {
        QrImage = qrImage;
    }

    public String getQrTemplate() {
        return QrTemplate;
    }

    public void setQrTemplate(String qrTemplate) {
        QrTemplate = qrTemplate;
    }
}
