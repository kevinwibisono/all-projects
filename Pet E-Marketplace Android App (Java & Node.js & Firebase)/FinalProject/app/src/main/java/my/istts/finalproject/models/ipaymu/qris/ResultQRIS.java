package my.istts.finalproject.models.ipaymu.qris;

public class ResultQRIS {
    private int Status;
    private String Message;
    private ResultQRISDetail Data;

    public ResultQRIS(int status, String message, ResultQRISDetail data) {
        Status = status;
        Message = message;
        Data = data;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public ResultQRISDetail getData() {
        return Data;
    }

    public void setData(ResultQRISDetail data) {
        Data = data;
    }
}
