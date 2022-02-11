package my.istts.finalproject.viewmodels.itemviewmodels;

import my.istts.finalproject.models.rajaongkirapi.PaketKurir;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class OngkirItemViewModel {
    private PaketKurir paketKurir;
    private String kurir;

    public OngkirItemViewModel(PaketKurir paketKurir, String kurir) {
        this.paketKurir = paketKurir;
        this.kurir = kurir;
    }

    public String getKurir(){
        return this.kurir;
    }

    public String getDetail(){
        if(paketKurir != null){
            int minimumEstimate = 1;
            String etd = paketKurir.getCost().get(0).getEtd().toLowerCase();
            String[] estimateTime = paketKurir.getCost().get(0).getEtd().split("-");
            //untuk kurir JNE, terkadang ada yg est nya 2-2 hari atau 1-1 hari
            if(estimateTime.length > 1){
                if(estimateTime[0].equals(estimateTime[1])){
                    etd = estimateTime[0];
                    minimumEstimate = Integer.parseInt(etd);
                }
                else{
                    minimumEstimate = Integer.parseInt(estimateTime[0]);
                }
            }
            else if(etd.contains(" hari")){
                etd = etd.substring(0, etd.indexOf(" hari"));
                minimumEstimate = Integer.parseInt(etd);
            }

            if(minimumEstimate > 0){
                return paketKurir.getDescription()+" ("+etd+" hari)";
            }
            else{
                return paketKurir.getDescription()+" (< 24 Jam)";
            }
        }
        else{
            return "Self Pickup (Ambil Sendiri)";
        }
    }

    public int getHargaInt(){
        if(paketKurir != null){
            return Integer.parseInt(paketKurir.getCost().get(0).getValue());
        }
        else{
            return 0;
        }
    }

    public String getHarga(){
        if(paketKurir != null){
            int hargaNum = Integer.parseInt(paketKurir.getCost().get(0).getValue());
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);
            return formatter.format(hargaNum);
        }
        else{
            return "0";
        }
    }
}
