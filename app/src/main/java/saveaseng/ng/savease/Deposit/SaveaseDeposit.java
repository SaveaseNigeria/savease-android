package saveaseng.ng.savease.Deposit;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Model.Data;
import saveaseng.ng.savease.Model.Example;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.Model.User;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiCli;
import saveaseng.ng.savease.Utils.ApiClient;
import saveaseng.ng.savease.Utils.ApiInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveaseDeposit extends Fragment {

    private static final String METHOD_NAME = "saveDeposit";
    private static final String SOAP_ACTION = "http://savease.ng/saveDeposit";

    private static final String METHOD_NAME2 = "getCardAmount";
    private static final String SOAP_ACTION2= "http://savease.ng/getCardAmount";

    private static final String METHOD_NAME5 = "getBalance";
    private static final String SOAP_ACTION5 = "http://savease.ng/getBalance";


    private static final String METHOD_NAME3 = "getCardSerial";
    private static final String SOAP_ACTION3= "http://savease.ng/getCardSerial";


    private static final String METHOD_NAME4 = "getNameOnDeposit";
    private static final String SOAP_ACTION4= "http://savease.ng/getNameOnDeposit";

    private static final String METHOD_NAME6 = "updateBalance";
    private static final String SOAP_ACTION6 = "http://savease.ng/updateBalance";


    private static final String METHOD_NAME7 = "updateBVNStatus";
    private static final String SOAP_ACTION7 = "http://savease.ng/updateBVNStatus";



    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final static String API_KEY = "Bearer sk_live_0dc08582961f9c1d0785245c36bc4a65658ce187";
    private final static String SMS_API_KEY = "9Pc1XtdCYg43wdJ6AlbCSCyTlLqc2voEFpl9DvmUq0zcKJTDbdE4aOYOPtzz";
    SharedPreferences preferences;
    SharedPreferences pref,prefs;
    boolean connected = false;

    private EditText edtVoucher,edtAccountNumber,edtVoucherNarrationSavease;
    private TextView edtAmount;
    private TextView edtAccountName;
    Dialog dialog;
    Dialog mDialog;
    String balance,saveaseId,bvn,transRef;
    private String bank,voucherPin,vouncherSn,accountNumber,accountName,amount,fAmount,username,acctname,bvnStatus,bvnFirstName,bvnLastName,bvnPhoneNumber;
    private Button btnDeposit;
    private SweetAlertDialog pDialog;



    public SaveaseDeposit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_savease_deposit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        edtAmount = (TextView)view.findViewById(R.id.edtVoucherAmountSavease);
        edtVoucher = (EditText)view.findViewById(R.id.edtVouchPinSavease);
        edtVoucherNarrationSavease = (EditText)view.findViewById(R.id.edtVoucherNarrationSavease);
        dialog = new Dialog(getContext());
        edtAccountName = (TextView)view.findViewById(R.id.edtAccountNameSavease);
        edtAccountNumber = (EditText)view.findViewById(R.id.edtAccountNumberSavease);
        balance = preferences.getString("balance", "");;
        pref = getContext().getSharedPreferences("bvnStatus", Context.MODE_PRIVATE);
        saveaseId = preferences.getString("accountNumber", "");


        bvnStatus = pref.getString("bvn","");

        edtVoucher.setText("");
        edtAccountNumber.setText("");
        username = preferences.getString("uname", "");
        bank = "savease";
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);

        edtAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                accountNumber = s.toString();
                new getAccountName().execute(s.toString());


            }
        });

        edtVoucher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                voucherPin =s.toString();
                new getCardAmount().execute(s.toString());
                new getCardSerial().execute(s.toString());
            }
        });

        btnDeposit = (Button)view.findViewById(R.id.btnMakeSaveaseDeposit);
        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bvnStatus.isEmpty() || bvnStatus.equalsIgnoreCase("true")){
                    startDeposit();
                }else{
                    double a = Double.parseDouble("10");
                    double b = Double.parseDouble(balance);


                    if (b < a){
                        Toast.makeText(getContext(), "Oops, insufficient balance to verify your bvn ensure you have not less than N10 in your account ", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }else{
                        showVerifyBvnDialog();
                    }
                }


            }
        });

    }

    private void showVerifyBvnDialog() {
        mDialog = new Dialog(getContext(), R.style.AppBaseTheme);
        mDialog.setContentView(R.layout.verify_bvn);

        final EditText edtBVNNumber = (EditText)mDialog.findViewById(R.id.edtBVNNumber);


        Button btnRegisterDialog = (Button)mDialog.findViewById(R.id.btnVerifyBvnDialog);

        btnRegisterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getContext(), RegisterationActivity.class));
                 bvn = edtBVNNumber.getText().toString().trim();
                if (bvn.isEmpty() && bvn.length() != 11){
                    Toast.makeText(getContext(), "Your BVN number is not correct, correct it and try agaiin", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    verifyBvn(bvn);
                }
            }
        });
        mDialog.show();
    }

    private void verifyBvn(String toString) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Verifying your bvn,please wait....");
        pDialog.setCancelable(false);
        pDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Example> call = apiService.getBvn(toString, API_KEY);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example>call, Response<Example> response) {
                pDialog.dismiss();
                if (response.body() != null){
                    Data data = response.body().getData();
                    bvnFirstName = data.getFirstName();
                    bvnLastName = data.getLastName();
                    bvnPhoneNumber = data.getMobile();



                    String fname = preferences.getString("fname","");
                    String lname = preferences.getString("lname","");
                    String phone = "0"+preferences.getString("accountNumber", "");


                    if (fname.equalsIgnoreCase(bvnFirstName) || lname.equalsIgnoreCase(bvnLastName)){
                        if (phone.equalsIgnoreCase(bvnPhoneNumber)){
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("bvn","true");
                            editor.apply();
                            pDialog.dismiss();
                            new updateBvnStatus().execute(username);
                            // Toast.makeText(getContext(), "Your bvn successfully verified your bvn and details, click the signup button to complete registration", Toast.LENGTH_LONG).show();


                        }else {
                            new updateBalance().execute(username,"10");
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Your phone number does not match with the phone number gotten from your bvn, please rectify the issue and try again", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else {
                        new updateBalance().execute(username,"10");
                        pDialog.dismiss();
                        Toast.makeText(getContext(), "Your name does not match with the name gotten from your bvn, please rectify the issue and try again", Toast.LENGTH_LONG).show();

                        return;
                    }


                }else{
                    new updateBalance().execute(username,"10");
                    pDialog.dismiss();
                    Toast.makeText(getContext(), "Your Bvn number is not correct, please rectify the issue and try again", Toast.LENGTH_LONG).show();

                    return;
                }




            }

            @Override
            public void onFailure(Call<Example>call, Throwable t) {
                // Log error here since request failed
                //  Log.e(TAG, t.toString());
                pDialog.dismiss();
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private class updateBvnStatus extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Verifying your bvn,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == "null"){
                pDialog.dismiss();

            }else {
                if (s.equalsIgnoreCase("1"))
                {
                    pDialog.dismiss();
                    new updateBalance().execute(username,"10");
                    startDeposit();
                }else {
                    pDialog.dismiss();
                    Toast.makeText(getContext(), "An error occurred while updating your verifying your BVN status, please contact your account officer", Toast.LENGTH_LONG).show();

                }


            }


        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];



            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME7);

            request.addProperty("in_username",amt);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION7, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object  result = null;
            try {
                result = (Object )envelope.getResponse();
                Log.i("RESPONSE",String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return String.valueOf(result) ;

        }
    }

    private void startDeposit() {
        btnDeposit.setEnabled(false);
        voucherPin = edtVoucher.getText().toString();
        amount = edtAmount.getText().toString();
        accountNumber = edtAccountNumber.getText().toString();
        accountName = edtAccountName.getText().toString();

        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            if (voucherPin.isEmpty() && amount.equalsIgnoreCase("null")) {
                Toast.makeText(getContext(), "Incorrect pin", Toast.LENGTH_LONG).show();
                return;
                 }
            if (accountNumber.isEmpty() && accountName.equalsIgnoreCase("null")) {
                Toast.makeText(getContext(), "Incorrect pin", Toast.LENGTH_LONG).show();
                return;
            }

            new makeSaveaseDeposit().execute(accountNumber,voucherPin,vouncherSn,bank,acctname,fAmount,username,edtVoucherNarrationSavease.getText().toString().trim());
        }

        else {
            btnDeposit.setEnabled(true);
            connected = false;
            pDialog.setTitleText("Error")
                    .setContentText("Seems you are not connected to the internet, please do so and try again").show();
        }
    }


    private class getCardAmount extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == "null"){
                edtAmount.setText("Amount: ");
            }else {
                fAmount = s;
                edtAmount.setText("Amount: " + s);
            }
        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);

            request.addProperty("in_card_pin",amt);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;


            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION2, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object  result = null;
            try {
                result = (Object )envelope.getResponse();
                Log.i("RESPONSE",String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return String.valueOf(result) ;

        }
    }


    private class getCardSerial extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
            {
                vouncherSn  = s.substring(1, s.length() - 1);
            }


        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);

            request.addProperty("in_card_pin",amt);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION3, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object  result = null;
            try {
                result = (Object )envelope.getResponse();
                Log.i("RESPONSE",String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return String.valueOf(result) ;

        }
    }

    private class getAccountName extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == "null"){
                edtAccountName.setText("Account Name: ");
            }else {
                String[] separated = s.split(",");

                if (separated[0].length() >= 2 && separated[0].charAt(0) == '"' && separated[0].charAt(separated[0].length() - 1) == '"')
                {
                    acctname = separated[0].substring(1, separated[0].length() - 1);
                    edtAccountName.setText("Account Name: " + separated[0]);
                }else {

                    edtAccountName.setText("Account Name: " + separated[0]);
                }


            }

        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);

            request.addProperty("in_saveaseid",amt);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION4, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object  result = null;
            try {
                result = (Object )envelope.getResponse();
                Log.i("RESPONSE",String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return String.valueOf(result) ;

        }
    }

    private class makeSaveaseDeposit extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        SweetAlertDialog sDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
        SweetAlertDialog fDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Making deposit,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            if (s.equalsIgnoreCase("")) {

                new FancyAlertDialog.Builder(getActivity())
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Your Deposit Transaction was unsuccessful")
                        .setPositiveBtnText("Done")
                        .setPositiveBtnBackground(Color.parseColor("#DF5C4E"))
                        .setAnimation(Animation.SLIDE)
                        .setNegativeBtnBackground(Color.parseColor("#ffffff"))
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_cancel, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                dialog.dismiss();


                            }
                        })
                        .build();

            } else {
                String[] separated = s.split(",");

                if (separated[0].equalsIgnoreCase("1")){
                    transRef = separated[1];
                    new getBalance().execute(username);

                }else{

                    new FancyAlertDialog.Builder(getActivity())
                            .setTitle("Failed")
                            .setBackgroundColor(Color.parseColor("#DF5C4E"))
                            .setMessage("Your Deposit Transaction was unsuccessful")
                            .setPositiveBtnText("Done")
                            .setPositiveBtnBackground(Color.parseColor("#DF5C4E"))
                            .setAnimation(Animation.SLIDE)
                            .setNegativeBtnBackground(Color.parseColor("#ffffff"))
                            .isCancellable(true)
                            .setIcon(R.drawable.ic_cancel, Icon.Visible)
                            .OnPositiveClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {
                                    dialog.dismiss();

                                }
                            })
                            .build();

                }



            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String re = "";
            String acctNum =  strings[0];
            String voucherP =  strings[1];
            String voucherS =  strings[2];
            String bnk =  strings[3];
            String acctName =  strings[4];
            String amt =  strings[5];
            String user =  strings[6];
            String narration = strings[7];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("in_acctNo",acctNum);
            request.addProperty("in_cardpin",voucherP);
            request.addProperty("in_cardsn",voucherS);
            request.addProperty("in_bankName",bnk);
            request.addProperty("in_acctName",acctName);
            request.addProperty("in_amount",amt);
            request.addProperty("in_depositor",user);
            request.addProperty("in_naration",narration);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object result = null;
            SoapObject response = (SoapObject) envelope.bodyIn;


            if (response == null){
                re = "";
            }else {
                re = response.getProperty(0).toString();
                try {
                    JSONArray jsonArray = new JSONArray(re);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        re = jsonObject1.optString("TransStatus")+","+jsonObject1.optString("TansRef") ;


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            return re;

        }
    }
    private class getBalance extends AsyncTask<String, String, String> {
        User user = new User();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equalsIgnoreCase("")){

            }else {
                btnDeposit.setEnabled(true);
                String[] separated = s.split(",");
                SharedPreferences preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("accountNumber", separated[0]);
                edit.putString("fname", separated[1]);
                edit.putString("lname", separated[2]);
                edit.putString("balance", separated[5]);
                edit.putString("email", separated[3]);
                edit.putString("phone", separated[4]);
                edit.apply();
                sendMessage(separated[1]);

                new FancyAlertDialog.Builder(getActivity())
                        .setTitle("Success")
                        .setBackgroundColor(Color.parseColor("#212435"))
                        .setMessage("Your Deposit was successful")
                        .setPositiveBtnText("Done")
                        .setPositiveBtnBackground(Color.parseColor("#212435"))
                        .setNegativeBtnBackground(Color.parseColor("#ffffff"))
                        .setAnimation(Animation.SLIDE)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_checked, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                dialog.dismiss();
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
                            }
                        })
                        .build();



            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String re = "";

            String uname = strings[0];
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);
            request.addProperty("straccountNo", uname);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION5, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object result = null;
            SoapObject response = (SoapObject) envelope.bodyIn;


            if (response == null){
                re = "";
            }else {
                re = response.getProperty(0).toString();
                try {
                    JSONArray jsonArray = new JSONArray(re);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        user.setFname(jsonObject1.optString("fname"));
                        user.setLname(jsonObject1.optString("lname"));
                        user.setSaveaseID(jsonObject1.optString("saveaseID"));
                        user.setEmail(jsonObject1.optString("email"));
                        user.setPhone(jsonObject1.optString("phone"));
                        user.setBalance(Double.parseDouble(jsonObject1.optString("balance")));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                re = user.getSaveaseID() + "," + user.getFname() + "," + user.getLname() + "," + user.getEmail() + "," + user.getPhone() + "," + String.valueOf(user.getBalance()+"0");

            }

//

            return re;

        }
    }

    private class updateBalance extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Bvn");
            userRef.child(saveaseId).child("bvn").setValue("true")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
                            userRef.child(saveaseId).child("bvnNumber").setValue(bvn)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //  Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];
            String pin = strings[1];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME6);

            request.addProperty("inuser",amt);
            request.addProperty("App_amt",pin);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION6, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            } //send request
            Object  result = null;
            try {
                result = (Object )envelope.getResponse();
                Log.i("RESPONSE",String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return "" ;

        }
    }

    private void sendMessage(String name) {

        String newString = accountNumber.substring(0, 3) + "XXXX" + accountNumber.substring(3+4);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ApiInterface apiInterface  = ApiCli.getClient().create(ApiInterface.class);

        Call<SmsRes> call = apiInterface.sendMessage(SMS_API_KEY,"Savease","0"+accountNumber,"Your Acct "+newString+ " Has Been Credited with NGN"+ fAmount+ " On "+ formattedDate +" By THIRD PARTY SAVEASE DEPOSIT / "+name+" - (Transaction Ref) Bal: +NGN"+fAmount+"CR, Kindly dial *384*3358# to use our USSD platform","2");

        call.enqueue(new Callback<SmsRes>() {
            @Override
            public void onResponse(Call<SmsRes> call, Response<SmsRes> response) {
                if (response.body() != null){

                    if (response.body().getData().getStatus().equalsIgnoreCase("success")){

                    }else{
                        Toast.makeText(getContext(), "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getContext(), "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SmsRes> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }



}
