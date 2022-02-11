package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.AppoPetItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentViewModel {
    private DetailPenjualDBAccess detailDB;
    private AkunDBAccess akunDB;
    private AlamatDBAccess addrDB;
    private PesananJanjitemuDBAccess orderDB;
    private Application app;

    public AppointmentViewModel(Application app){
        this.app = app;
        this.detailDB = new DetailPenjualDBAccess();
        this.akunDB = new AkunDBAccess(app);
        this.addrDB = new AlamatDBAccess();
        this.orderDB = new PesananJanjitemuDBAccess();
    }

    public MutableLiveData<String> ownerName = new MutableLiveData<>("");
    public MutableLiveData<String> ownerHP = new MutableLiveData<>("");
    public MutableLiveData<String> keluhan = new MutableLiveData<>("");
    private MutableLiveData<Boolean> addLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> addressNeeded = new MutableLiveData<>();
    private MutableLiveData<Boolean> timeEnabled = new MutableLiveData<>(false);
    private MutableLiveData<ArrayList<String>> clinicAppoTypes = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AppoPetItemViewModel>> pets = new MutableLiveData<>();
    private MutableLiveData<String> alamat = new MutableLiveData<>("");
    private MutableLiveData<String[]> errors = new MutableLiveData<>();
    private MutableLiveData<Integer> tlNumber = new MutableLiveData<>();
    private MutableLiveData<String> clinicSchedules = new MutableLiveData<>();
    private String[] jamBuka, jamTutup;
    private String idAlamat = "", appoType = "";
    private Date tanggal;
    private String jam;
    private String hp_klinik;

    public void getKlinikDetail(String hp_klinik){
        this.hp_klinik = hp_klinik;
        pets.setValue(new ArrayList<>());
        errors.setValue(new String[]{"", "", "", "", "", ""});

        detailDB.getDBAkunDetail(hp_klinik).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    DetailPenjual clinicDetail = new DetailPenjual(documentSnapshot);

                    jamBuka = clinicDetail.getJamBukaSplitted();
                    jamTutup = clinicDetail.getJamTutupSplitted();
                    setClinicSchedules();
                    String[] appoTypes = {"Datang ke klinik", "Home visit"};
                    ArrayList<String> appoTypesIncluded = new ArrayList<>();
                    String[] appoTypesState =  clinicDetail.getJanji_temu().split("\\|");

                    for (int i = 0; i < appoTypesState.length; i++) {
                        boolean typesIncluded = Boolean.parseBoolean(appoTypesState[i]);
                        if(typesIncluded){
                            appoTypesIncluded.add(appoTypes[i]);
                        }
                    }

                    clinicAppoTypes.setValue(appoTypesIncluded);
                    setAppointmentType(0);
                }
            }
        });
    }

    private void setClinicSchedules(){
        String schedule = "";
        String[] days = {"Senin: ", "Selasa: ", "Rabu: ", "Kamis: ", "Jumat: ", "Sabtu: ", "Minggu: "};
        for (int i = 0; i < jamBuka.length; i++) {
            if(schedule.equals("")){
                schedule += days[i]+jamBuka[i]+" - "+jamTutup[i];
            }
            else{
                schedule += "\n"+days[i]+jamBuka[i]+" - "+jamTutup[i];
            }
        }
        clinicSchedules.setValue(schedule);
    }

    public void addNewAppoPet(String type, String name, String age){
        ArrayList<AppoPetItemViewModel> currentPets = pets.getValue();
        currentPets.add(new AppoPetItemViewModel(type, name, age));
        pets.setValue(currentPets);
    }

    public void deleteAppoPet(int pos){
        ArrayList<AppoPetItemViewModel> currentPets = pets.getValue();
        currentPets.remove(pos);
        pets.setValue(currentPets);
    }

    public void setJam(String jam){
        String[] hourMin = jam.split(":");
        this.jam = jam;
        this.tanggal.setHours(Integer.parseInt(hourMin[0]));
        this.tanggal.setMinutes(Integer.parseInt(hourMin[1]));
    }

    public void setTanggal(Date tgl){
        this.tanggal = tgl;
        this.timeEnabled.setValue(true);
    }

    public void setAlamat(String id_alamat){
        idAlamat = id_alamat;
        addrDB.getAddressById(id_alamat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Alamat chosenAddr = new Alamat(documentSnapshot);
                    alamat.setValue(chosenAddr.getAlamat_lengkap());
                }
            }
        });
    }

    private boolean isAppoTimeDateValid(){
        if(tanggal != null && jam != null){
            int hariJT = tanggal.getDay()-1;
            if(tanggal.getDay() == 0){
                hariJT = 6; //0 -> minggu
            }
            if(jamBuka[hariJT].equals("")){
                return false;
            }
            else{
                int appoTime = getMinutes(jam.split(":"));
                int openTime = getMinutes(jamBuka[hariJT].split(":"));
                int closeTime = getMinutes(jamTutup[hariJT].split(":"));
                if(openTime > closeTime){
                    return timeValidPassMidnight(appoTime, openTime, closeTime);
                }
                else if(openTime < closeTime){
                    return timeValidNormal(appoTime, openTime, closeTime);
                }
                else{
                    return true;
                }
            }
        }
        return false;
    }

    private int getMinutes(String[] time){
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        return (hour*60)+minute;
    }

    //cth jam buka -> 18:00
    //cth jam tutup -> 00:00
    //range yang valid -> lebih kecil dari waktu tutup ATAU lebih besar dari waktu buka
    private boolean timeValidPassMidnight(int appoMinutes, int openMinutes, int closesMinutes){
        if(appoMinutes <= closesMinutes || appoMinutes >= openMinutes){
            return true;
        }
        else{
            return false;
        }
    }

    //cth jam buka -> 18:00
    //cth jam tutup -> 20:00
    //range yang valid -> lebih kecil dari waktu tutup DAN lebih besar dari waktu buka
    private boolean timeValidNormal(int appoMinutes, int openMinutes, int closesMinutes){
        if(appoMinutes >= openMinutes && appoMinutes <= closesMinutes){
            return true;
        }
        else{
            return false;
        }
    }

    public void setAppointmentType(int position){
        ArrayList<String> appoTypes = clinicAppoTypes.getValue();
        String typeSelected = appoTypes.get(position);
        appoType = typeSelected;
        if(typeSelected.equals("Home visit")){
            idAlamat = "";
            addressNeeded.setValue(true);
        }
        else{
            idAlamat = " ";
            addressNeeded.setValue(false);
        }
    }

    public void addAppointmentCheck(){
        if(ownerName.getValue().equals("")){
            errors.setValue(new String[]{"Nama Pemilik Tidak Dapat Kosong", "", "", "", "", ""});
            tlNumber.setValue(0);
        }
        else if(ownerHP.getValue().equals("")){
            errors.setValue(new String[]{"", "No HP Pemilik Tidak Dapat Kosong", "", "", "", ""});
            tlNumber.setValue(1);
        }
        else if(pets.getValue().size() < 1){
            errors.setValue(new String[]{"", "", "Mohon Sertakan Minimal 1 Hewan Peliharaan", "", "", ""});
            tlNumber.setValue(2);
        }
        else if(idAlamat.equals("")){
            errors.setValue(new String[]{"", "", "", "Alamat Tidak Dapat Kosong", "", ""});
            tlNumber.setValue(3);
        }
        else if(!isAppoTimeDateValid()){
            errors.setValue(new String[]{"", "", "", "", "Tanggal Dan Waktu Tidak Sesuai", ""});
            tlNumber.setValue(4);
        }
        else if(keluhan.getValue().equals("")){
            errors.setValue(new String[]{"", "", "", "", "", "Keluhan Tidak Dapat Kosong"});
            tlNumber.setValue(5);
        }
        else{
            errors.setValue(new String[]{"", "", "", "", "", ""});
            makeAppointment();
        }
    }

    private void makeAppointment(){
        addLoading.setValue(true);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    orderDB.addJanjiTemuKlinik(accountsGot.get(0).getEmail(), hp_klinik).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getData() != null){
                                        addDetailJanjiTemu(documentSnapshot.getId());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addDetailJanjiTemu(String id_pj){
        if(appoType.equals("Datang ke klinik")){
            detailDB.getDBAkunDetail(hp_klinik).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        DetailPenjual klinik = new DetailPenjual(documentSnapshot);

                        orderDB.addDetailJanjiTemu(id_pj, tanggal, klinik.getAlamat(), klinik.getKoordinat(), appoType, getDaftarHewan(0),
                                getDaftarHewan(1), getDaftarHewan(2), keluhan.getValue(), ownerName.getValue(), ownerHP.getValue())
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        addLoading.setValue(false);
                                    }
                                });
                    }
                }
            });
        }
        else{
            addrDB.getAddressById(idAlamat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        Alamat chosenAddr = new Alamat(documentSnapshot);

                        orderDB.addDetailJanjiTemu(id_pj, tanggal, chosenAddr.toString(), chosenAddr.getKoordinat(), appoType, getDaftarHewan(0),
                                getDaftarHewan(1), getDaftarHewan(2), keluhan.getValue(), ownerName.getValue(), ownerHP.getValue())
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        addLoading.setValue(false);
                                    }
                                });
                    }
                }
            });
        }
    }

    private String getDaftarHewan(int namaUsiaJenis){
        String[] daftarNUJ = {"", "", ""};
        ArrayList<AppoPetItemViewModel> currentPets = pets.getValue();
        for (AppoPetItemViewModel pet:
             currentPets) {
            if(daftarNUJ[0].equals("")){
                daftarNUJ[0] += pet.getPetName();
                daftarNUJ[1] += pet.getPetType();
                daftarNUJ[2] += pet.getPetAge();
            }
            else{
                daftarNUJ[0] += "|"+pet.getPetName();
                daftarNUJ[1] += "|"+pet.getPetType();
                daftarNUJ[2] += "|"+pet.getPetAge();
            }
        }
        return daftarNUJ[namaUsiaJenis];
    }

    public MutableLiveData<Boolean> isAddLoading() {
        return addLoading;
    }

    public LiveData<ArrayList<AppoPetItemViewModel>> getPets() {
        return pets;
    }

    public LiveData<ArrayList<String>> getClinicAppoTypes() {
        return clinicAppoTypes;
    }

    public LiveData<String> getAlamat() {
        return alamat;
    }

    public LiveData<String[]> getErrors() {
        return errors;
    }

    public LiveData<Integer> getTlNumber() {
        return tlNumber;
    }

    public LiveData<Boolean> isAddressNeeded() {
        return addressNeeded;
    }

    public LiveData<Boolean> isTimeEnabled() {
        return timeEnabled;
    }

    public LiveData<String> getClinicSchedules() {
        return clinicSchedules;
    }
}
