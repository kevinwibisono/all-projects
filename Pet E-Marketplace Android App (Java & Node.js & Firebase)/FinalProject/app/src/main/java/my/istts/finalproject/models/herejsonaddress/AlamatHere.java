package my.istts.finalproject.models.herejsonaddress;

import java.util.List;

public class AlamatHere {
    private String title;
    private String id;
    private String resultType;
    private String houseNumberType;
    private DetailAlamatHere address;
    private KoordinatAlamatHere position;
    private List<KoordinatAlamatHere> access;
    private MapViewAlamatHere mapView;
    private ScoreAlamatHere scoring;

    public AlamatHere(String title, String id, String resultType, String houseNumberType, DetailAlamatHere address, KoordinatAlamatHere position, List<KoordinatAlamatHere> access, MapViewAlamatHere mapView, ScoreAlamatHere scoring) {
        this.title = title;
        this.id = id;
        this.resultType = resultType;
        this.houseNumberType = houseNumberType;
        this.address = address;
        this.position = position;
        this.access = access;
        this.mapView = mapView;
        this.scoring = scoring;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getHouseNumberType() {
        return houseNumberType;
    }

    public void setHouseNumberType(String houseNumberType) {
        this.houseNumberType = houseNumberType;
    }

    public DetailAlamatHere getAddress() {
        return address;
    }

    public void setAddress(DetailAlamatHere address) {
        this.address = address;
    }

    public KoordinatAlamatHere getPosition() {
        return position;
    }

    public void setPosition(KoordinatAlamatHere position) {
        this.position = position;
    }

    public List<KoordinatAlamatHere> getAccess() {
        return access;
    }

    public void setAccess(List<KoordinatAlamatHere> access) {
        this.access = access;
    }

    public MapViewAlamatHere getMapView() {
        return mapView;
    }

    public void setMapView(MapViewAlamatHere mapView) {
        this.mapView = mapView;
    }

    public ScoreAlamatHere getScoring() {
        return scoring;
    }

    public void setScoring(ScoreAlamatHere scoring) {
        this.scoring = scoring;
    }
}
