package my.istts.finalproject.viewmodels.itemviewmodels;

import my.istts.finalproject.models.Pembayaran;

public class UnfinishedPaymentItemViewModel {
    private Pembayaran payment;

    public UnfinishedPaymentItemViewModel(Pembayaran payment) {
        this.payment = payment;
    }

    public String getId(){
        return payment.getId_payment();
    }

    public int getTotal(){
        return payment.getTotal_bayar();
    }

    public String getMethod(){
        return payment.getMetode();
    }

    public String getSelesai(){
        return payment.getSelesaiStr();
    }
}
