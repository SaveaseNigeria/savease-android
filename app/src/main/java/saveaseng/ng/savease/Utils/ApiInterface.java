package saveaseng.ng.savease.Utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import saveaseng.ng.savease.Model.AccountNameResponse;
import saveaseng.ng.savease.Model.BankListResponse;
import saveaseng.ng.savease.Model.BankTransfer;
import saveaseng.ng.savease.Model.Example;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.Model.TransferCreationResponse;
import saveaseng.ng.savease.Model.TransferRep;
import saveaseng.ng.savease.Model.TransferResponse;

public interface ApiInterface {

    @GET("bank/resolve_bvn/{id}")
    Call<Example> getBvn(@Path("id") String id, @Header("Authorization") String token);

    @GET("bank/resolve")
    Call<AccountNameResponse> getAccountName(@Query("account_number") String accountNumber, @Query("bank_code") String code, @Header("Authorization") String token);


    @GET("sms/create")
    Call<SmsRes> sendMessage(@Query("api_token") String api, @Query("from") String from, @Query("to") String to, @Query("body") String body,@Query("dnd") String dnd);


    @GET("bank")
    Call<BankListResponse> getAllBanks(@Header("Authorization") String token);


    @POST("transferrecipient")
    Call<TransferCreationResponse> createTransferRep(@Body TransferRep transferRep, @Header("Authorization") String token);

    @POST("transfer")
    Call<TransferResponse> transferFund(@Body BankTransfer transfer, @Header("Authorization") String token);




}


