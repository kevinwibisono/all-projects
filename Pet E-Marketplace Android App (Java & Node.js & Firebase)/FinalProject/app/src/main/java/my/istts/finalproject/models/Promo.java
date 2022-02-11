package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;


public class Promo {
    private String email_penjual, id_promo, judul, id_produk;
    private int persentase, maximum_diskon, minimum_pembelian;
    private Timestamp valid_hingga;

    public Promo(DocumentSnapshot doc) {
        this.email_penjual = doc.getString("email_penjual");
        this.id_promo = doc.getId();
        this.judul = doc.getString("judul");
        this.id_produk = doc.getString("id_produk");
        this.persentase = doc.getLong("persentase").intValue();
        this.maximum_diskon = doc.getLong("maximum_diskon").intValue();
        this.minimum_pembelian = doc.getLong("minimum_pembelian").intValue();
        this.valid_hingga = doc.getTimestamp("valid_hingga");
    }

    public String getemail_penjual() {
        return email_penjual;
    }

    public String getId_promo() {
        return id_promo;
    }

    public String getJudul() {
        return judul;
    }

    public String getId_produk() {
        return id_produk;
    }

    public int getPersentase() {
        return persentase;
    }

    public int getMaximum_diskon() {
        return maximum_diskon;
    }

    public int getMinimum_pembelian() {
        return minimum_pembelian;
    }

    public void setValid_hingga(Timestamp valid_hingga) {
        this.valid_hingga = valid_hingga;
    }

    public String getValidString() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        Date validUntil = valid_hingga.toDate();
        String todayString = validUntil.getDate()+" "+months[validUntil.getMonth()]+" "+(validUntil.getYear()+1900);
        return todayString;
    }

    public Timestamp getValid_hingga() {
        return valid_hingga;
    }

    public String getTSMin(){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(minimum_pembelian);
    }
}
