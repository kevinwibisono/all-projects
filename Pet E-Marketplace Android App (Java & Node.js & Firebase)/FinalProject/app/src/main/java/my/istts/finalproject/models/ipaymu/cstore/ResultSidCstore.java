package my.istts.finalproject.models.ipaymu.cstore;

public class ResultSidCstore {
    private String sessionID;
    private String url;

    public ResultSidCstore(String sessionID, String url) {
        this.sessionID = sessionID;
        this.url = url;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ResultSidCstore{" +
                "sessionID='" + sessionID + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
