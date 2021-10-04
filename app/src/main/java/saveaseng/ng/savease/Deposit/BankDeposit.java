package saveaseng.ng.savease.Deposit;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankDeposit extends Fragment {


    private static final String METHOD_NAME = "saveDeposit";
    private static final String SOAP_ACTION = "http://savease.ng/saveDeposit";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    SharedPreferences preferences;
    boolean connected = false;

    private MaterialSpinner spinnerBank;
    private EditText edtVoucher,edtVoucherSn,edtAccountNumber,edtAccountName,edtAmount;
    private String bank,voucherPin,vouncherSn,accountNumber,accountName,amount,username;
    private Button btnDeposit;

    private static final String[] bankType = {
            "Select Bank ",
            "Diamond Bank",
            "Eco Bank",
            "First Bank",
            "GT Bank",
            "UBA",
            "Wema Bank",
            "Zenith Bank"

    };


    public BankDeposit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bank_deposit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        spinnerBank = (MaterialSpinner)view.findViewById(R.id.spinBankType);
        spinnerBank.setItems(bankType);
        spinnerBank.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                bank = item;


            }
        });
        spinnerBank.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });

        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        username = preferences.getString("uname", "");


        edtAccountName = (EditText)view.findViewById(R.id.edtSaveaseAccountNumber);
        edtAccountNumber = (EditText)view.findViewById(R.id.edtSaveaseAccountName);
        edtVoucher = (EditText)view.findViewById(R.id.edtVouchPin);
        edtVoucherSn = (EditText)view.findViewById(R.id.edtVouchPinSN);
        edtAmount = (EditText)view.findViewById(R.id.edtVoucherAmount);
        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
        btnDeposit = (Button)view.findViewById(R.id.btnMakeBankDeposit);

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNumber = edtAccountNumber.getText().toString().trim();
                accountName = edtAccountName.getText().toString().trim();
                amount = edtAmount.getText().toString().trim();
                voucherPin = edtVoucher.getText().toString().trim();
                vouncherSn = edtVoucherSn.getText().toString().trim();



                ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    if (amount.isEmpty()){
                        Toast.makeText(getContext(), "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (accountName.isEmpty()){
                        Toast.makeText(getContext(), "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (accountNumber.isEmpty()){
                        Toast.makeText(getContext(), "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (vouncherSn.isEmpty()){
                        Toast.makeText(getContext(), "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (voucherPin.isEmpty()){
                        Toast.makeText(getContext(), "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    new makeBankDeposit().execute(accountNumber,voucherPin,vouncherSn,bank,accountName,amount,username);

                }
                else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }
            }
        });

    }

    private class makeBankDeposit extends AsyncTask<String,String,String> {

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
            pDialog.dismissWithAnimation();
            Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String acctNum =  strings[0];
            String voucherP =  strings[1];
            String voucherS =  strings[2];
            String bnk =  strings[3];
            String acctName =  strings[4];
            double amt =  Double.parseDouble(strings[5]);
            String user =  strings[6];
            String ip =  strings[7];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("in_acctNo",acctNum);
            request.addProperty("in_cardpin",voucherP);
            request.addProperty("in_cardsn",voucherS);
            request.addProperty("in_bankName",bnk);
            request.addProperty("in_acctName",acctName);
            request.addProperty("in_amount",amt);
            request.addProperty("in_depositor",user);


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
}
