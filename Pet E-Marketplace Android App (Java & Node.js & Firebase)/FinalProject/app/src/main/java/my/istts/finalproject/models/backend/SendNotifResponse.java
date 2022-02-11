package my.istts.finalproject.models.backend;

public class SendNotifResponse {
    private String messageId;
    private String error;

    public SendNotifResponse(String messageId, String error) {
        this.messageId = messageId;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getMessageId() {
        return messageId;
    }
}
