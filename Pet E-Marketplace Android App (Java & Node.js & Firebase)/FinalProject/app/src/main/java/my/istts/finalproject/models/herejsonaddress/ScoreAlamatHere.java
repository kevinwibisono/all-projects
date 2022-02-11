package my.istts.finalproject.models.herejsonaddress;

public class ScoreAlamatHere {
    private double queryScore;
    private FieldScoreAlamatHere fieldScore;

    public ScoreAlamatHere(double queryScore, FieldScoreAlamatHere fieldScore) {
        this.queryScore = queryScore;
        this.fieldScore = fieldScore;
    }

    public double getQueryScore() {
        return queryScore;
    }

    public void setQueryScore(double queryScore) {
        this.queryScore = queryScore;
    }

    public FieldScoreAlamatHere getFieldScore() {
        return fieldScore;
    }

    public void setFieldScore(FieldScoreAlamatHere fieldScore) {
        this.fieldScore = fieldScore;
    }
}
