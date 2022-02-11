package my.istts.finalproject.models;

import my.istts.finalproject.models.ipaymu.CstoreInput;
import my.istts.finalproject.models.ipaymu.PaymentInput;
import my.istts.finalproject.models.ipaymu.cstore.ResultCstore;
import my.istts.finalproject.models.ipaymu.cstore.ResultSidCstore;
import my.istts.finalproject.models.ipaymu.qris.ResultQRIS;
import my.istts.finalproject.models.ipaymu.transfer.ResultTransferBank;
import my.istts.finalproject.models.ipaymu.va.ResultVA;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IpaymuService {

    @POST("bcatransfer")
    Call<ResultTransferBank> makePaymentTransferBCA(@Body PaymentInput input);

    @POST("getmandiriva")
    Call<ResultVA> makePaymentVAMandiri(@Body PaymentInput input);

    @POST("getbagva")
    Call<ResultVA> makePaymentVAAGI(@Body PaymentInput input);

    @POST("getbniva")
    Call<ResultVA> makePaymentVABNI(@Body PaymentInput input);

    @POST("getva")
    Call<ResultVA> makePaymentVANiaga(@Body PaymentInput input);

    @POST("payment/qris")
    Call<ResultQRIS> makePaymentQRIS(@Body PaymentInput input);

    @POST("payment/getsid")
    Call<ResultSidCstore> makeSidCstore(@Body CstoreInput input);

    @POST("payment/cstore")
    Call<ResultCstore> makePaymentCstore(@Body PaymentInput input);
}
