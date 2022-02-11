package com.example.sellerapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sellerapp.models.AlamatDBAccess;
import com.example.sellerapp.models.herejsonaddress.AlamatHere;
import com.example.sellerapp.models.herejsonaddress.AlamatList;
import com.example.sellerapp.models.herejsonaddress.KoordinatAlamatHere;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressPickerViewmodel {

    private AlamatDBAccess alamatDB;

    public AddressPickerViewmodel() {
        this.alamatDB = new AlamatDBAccess();
    }

    private MutableLiveData<Boolean> addressFound;
    private MutableLiveData<ArrayList<String>> hereAddress;
    private MutableLiveData<ArrayList<KoordinatAlamatHere>> hereCoordinates = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<KoordinatAlamatHere> koordinat;
    private String hereChosenAddress;
    private AlamatList list;

    public LiveData<Boolean> isAddressFound(){
        if(addressFound == null){
            addressFound = new MutableLiveData<>(false);
        }
        return addressFound;
    }

    public LiveData<ArrayList<String>> getHereAddresses(){
        if(hereAddress == null){
            hereAddress = new MutableLiveData<>(new ArrayList<>());
        }
        return hereAddress;
    }

    public LiveData<String> getErrorMsg(){
        if(errorMsg == null){
            errorMsg = new MutableLiveData<>();
        }
        return errorMsg;
    }

    public LiveData<KoordinatAlamatHere> getKoordinat(){
        if(koordinat == null){
            koordinat = new MutableLiveData<>();
        }
        return koordinat;
    }

    public String getHereChosenAddress(){
        return hereChosenAddress;
    }

    public void searchAddressHere(String address){
        //procedure utk mendapatkan daftar address dari HERE
        //1. reset kembali list address yang akan ditampilkan di listview
        //2. terima string dari inputan yang diberikan afterTextChanged
        //3. akses function repos untuk search alamat yang plg cocok di HERE Api
        //4. muat hasil di ArrayList
        addressFound.setValue(false);

        alamatDB.getHereAddresses(address, 10).enqueue(new Callback<AlamatList>() {
            @Override
            public void onResponse(Call<AlamatList> call, Response<AlamatList> response) {
                list = response.body();
                ArrayList<String> addresses = new ArrayList<>();
                ArrayList<KoordinatAlamatHere> coordinates = new ArrayList<>();
                if(list != null){
                    for (AlamatHere address:
                            list.getListAlamat()) {
                        addresses.add(address.getTitle());
                        coordinates.add(address.getPosition());
                    }
                }

                addressFound.setValue(true);
                hereAddress.setValue(addresses);
                hereCoordinates.setValue(coordinates);
            }

            @Override
            public void onFailure(Call<AlamatList> call, Throwable t) {
                addressFound.setValue(true);
                errorMsg.setValue("Terjadi masalah dalam mendapatkan daftar alamat. Mohon coba beberapa saat lagi");
            }
        });
    }

    public void searchAddressWithCoordinates(double latitude, double longitude){
        addressFound.setValue(false);

        alamatDB.getHereAddressByCoordinates(latitude, longitude).enqueue(new Callback<AlamatList>() {
            @Override
            public void onResponse(Call<AlamatList> call, Response<AlamatList> response) {
                list = response.body();
                ArrayList<String> addresses = new ArrayList<>();
                ArrayList<KoordinatAlamatHere> coordinates = new ArrayList<>();
                if(list != null){
                    for (AlamatHere address:
                            list.getListAlamat()) {
                        addresses.add(address.getTitle());
                        coordinates.add(address.getPosition());
                    }
                }

                addressFound.setValue(true);
                hereAddress.setValue(addresses);
                hereCoordinates.setValue(coordinates);
            }

            @Override
            public void onFailure(Call<AlamatList> call, Throwable t) {
                addressFound.setValue(true);
                errorMsg.setValue("Terjadi masalah dalam mendapatkan daftar alamat. Mohon coba beberapa saat lagi");
            }
        });
    }

    public void pickAddress(int index){
        koordinat.setValue(hereCoordinates.getValue().get(index));
        hereChosenAddress = "";
        if(list.getListAlamat().get(index).getAddress().getHouseNumber() != null){
            hereChosenAddress += list.getListAlamat().get(index).getAddress().getHouseNumber()+", ";
        }
        hereChosenAddress += list.getListAlamat().get(index).getAddress().getStreet();
        hereAddress.setValue(new ArrayList<>());
    }

    public String getStringCoordinate(){
        return koordinat.getValue().getLat()+","+koordinat.getValue().getLng();
    }

}
