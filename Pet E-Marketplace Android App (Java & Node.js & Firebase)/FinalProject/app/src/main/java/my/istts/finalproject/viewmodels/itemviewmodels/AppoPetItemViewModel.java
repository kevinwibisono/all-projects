package my.istts.finalproject.viewmodels.itemviewmodels;

public class AppoPetItemViewModel {
    private String petType;
    private String petName;
    private String petAge;

    public AppoPetItemViewModel(String petType, String petName, String petAge) {
        this.petType = petType;
        this.petName = petName;
        this.petAge = petAge;
    }

    public String getPetType() {
        return petType;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetAge() {
        return petAge;
    }
}
