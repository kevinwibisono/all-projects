package my.istts.finalproject.models.ipaymu.transfer;

public class ResultTransferBank {
    private int Status;
    private String Message;
    private ResultTransferDetail Data;

    public ResultTransferBank(int status, String message, ResultTransferDetail data) {
        Status = status;
        Message = message;
        Data = data;
    }

    public int getStatus() {
        return Status;
    }

    public String getMessage() {
        return Message;
    }

    public ResultTransferDetail getData() {
        return Data;
    }
}
