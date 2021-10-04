package saveaseng.ng.savease.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import saveaseng.ng.savease.Activities.Settings;
import saveaseng.ng.savease.R;

public class ChangePassword extends AppCompatActivity {

    Button login ;
    ImageButton back;

    private EditText edtOldPassword,edtNewPassword,edtConfirmNewPassword;
    SharedPreferences preferences;

    private static final String METHOD_NAME = "changepass";
    private static final String SOAP_ACTION = "http://savease.ng/changepass";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private String oldPass,newPassword,username;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edtOldPassword = (EditText)findViewById(R.id.edtOldPassword);
        edtNewPassword = (EditText)findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = (EditText)findViewById(R.id.edtConfirmNewPassword);
        back = (ImageButton)findViewById(R.id.imgBack);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final SweetAlertDialog pDialog = new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE);
        login = (Button)findViewById(R.id.btnResetPassword);
        username = preferences.getString("uname", "");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePassword.this, Settings.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldPass = edtOldPassword.getText().toString().trim();
                newPassword = edtNewPassword.getText().toString().trim();
              String confirmnewPassword = edtConfirmNewPassword.getText().toString().trim();





                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;

                    if (oldPass.isEmpty()){
                        Toast.makeText(ChangePassword.this, "Field must be filled", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (newPassword.isEmpty()){
                        Toast.makeText(ChangePassword.this, "Field must be filled", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!newPassword.equalsIgnoreCase(confirmnewPassword)){
                        Toast.makeText(ChangePassword.this, "Password does not match", Toast.LENGTH_LONG).show();
                        return;
                    }

                    new resetPassword().execute(username,oldPass,newPassword);

                }
                else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }
            }
        });
    }

    private  class resetPassword extends AsyncTask<String,String,String> {
        SweetAlertDialog pDialog = new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Changing your password,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            this.pDialog.dismiss();
            String val = String.valueOf(s);
            if (val.equalsIgnoreCase("1")){
                Toast.makeText(ChangePassword.this, "Password successfully changed", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("accountType", "0");
                edit.apply();
                Intent intent = new Intent(ChangePassword.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
                finish();

            }else {
                Toast.makeText(ChangePassword.this, "Password changed was not successful", Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String uName =  strings[0];
            String oldpassword =  strings[1];
            String newpassword =  strings[2];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("in_username",uName);
            request.addProperty("in_oldpwd",oldpassword);
            request.addProperty("in_newpwd",newpassword);

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
            String re = "";
            try {
                if (envelope.bodyIn != null){
                    if (!envelope.getResponse().toString().equalsIgnoreCase("null")){
                        result = (Object)envelope.getResponse();
                        re = String.valueOf(result);
                    }else {
                        re = "Could not reset yor password at this time, please try again";
                    }
                }else {
                    re = "Could not reset yor password at this time, please try again";
                }



                Log.i("RESPONSE",String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }



            return re  ;
        }


    }
}
