package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.R;

public class ForgotPassword extends AppCompatActivity {
    private static final String METHOD_NAME = "resetPassword";
    private static final String SOAP_ACTION = "http://savease.ng/resetPassword";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private EditText edtVoucher;
    private Button btnCheckVoucher;
    ImageButton back;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edtVoucher = (EditText)findViewById(R.id.edtResetEmail);
        btnCheckVoucher = (Button)findViewById(R.id.btnReset);
        back = (ImageButton)findViewById(R.id.imgBackReset);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, Login.class));
                finish();
            }
        });
        final SweetAlertDialog pDialog = new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.ERROR_TYPE);

        btnCheckVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String voucher = edtVoucher.getText().toString().trim();
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    if (voucher.isEmpty()){
                        Toast.makeText(ForgotPassword.this, "You need an input an email to actually reset your password", Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        new verifyVoucher().execute(voucher);
                    }
                }
                else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();

                }
            }
        });
    }

    private class verifyVoucher extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.PROGRESS_TYPE);
        SweetAlertDialog sDialog = new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.SUCCESS_TYPE);
        SweetAlertDialog fDialog = new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.ERROR_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Resetting password");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected String doInBackground(String... voids) {

            String value =  voids[0];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("in_email",value);


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
            return String.valueOf(result);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.pDialog.dismiss();


        }
    }
}
