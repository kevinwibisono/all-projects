package my.istts.finalproject.models.ipaymu.transfer;

public class ResultTransferDetail {
    private int SessionId;
    private int TransactionId;
    private int ReferenceId;
    private String Via;
    private String Channel;
    private String PaymentNo;
    private String PaymentName;
    private int Total;
    private String SubTotal;
    private int Fee;
    private String Expired;

    public ResultTransferDetail(int sessionId, int transactionId, int referenceId, String via, String channel, String paymentNo, String paymentName, int total, String subTotal, int fee, String expired) {
        SessionId = sessionId;
        TransactionId = transactionId;
        ReferenceId = referenceId;
        Via = via;
        Channel = channel;
        PaymentNo = paymentNo;
        PaymentName = paymentName;
        Total = total;
        SubTotal = subTotal;
        Fee = fee;
        Expired = expired;
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

    public String getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(String subTotal) {
        SubTotal = subTotal;
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
}
