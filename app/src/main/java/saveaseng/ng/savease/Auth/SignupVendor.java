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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import saveaseng.ng.savease.R;

public class SignupVendor extends AppCompatActivity {
    TextView login;
    private EditText edtFirstName,edtLastName,edtPhoneNumber,edtEmail,edtUsername,edtPassword,edtCOnfirmPassword,edtCompanyName,edtCompanyAddress,edtBusinessType,edtCacReg,edtBvn;
    private static final String METHOD_NAME = "RegisterVendor";
    private static final String SOAP_ACTION = "http://savease.ng/RegisterVendor";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button signup ;
    private String fname,lname,phone,email,username,password,confirmpassword,companyName,companyAddress,businessType,cacReg,bvn;
    SharedPreferences preferences;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_vendor);

        edtFirstName = (EditText)findViewById(R.id.edtVendorSignupFirstName);
        edtLastName = (EditText)findViewById(R.id.edtVendorSignupLastName);
        edtPhoneNumber = (EditText)findViewById(R.id.edtVendorSignupPhone);
        edtEmail = (EditText)findViewById(R.id.edtVendorSignupEmail);
        edtUsername = (EditText)findViewById(R.id.edtVendorSignupUsername);
        edtPassword = (EditText)findViewById(R.id.edtVendorSignupPassword);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        edtCOnfirmPassword = (EditText)findViewById(R.id.edtVendorSignupConfirmPassword);
        edtBusinessType = (EditText)findViewById(R.id.edtVendorBusinessType);
        edtBvn = (EditText)findViewById(R.id.edtVendorSignupBvnNumber);
        edtCacReg = (EditText)findViewById(R.id.edtVendorSignupCacReg);
        edtCompanyAddress = (EditText)findViewById(R.id.edtVendorSignupAddress);
        edtCompanyName = (EditText)findViewById(R.id.edtVendorSignupCompanyName);
        login = (TextView)findViewById(R.id.txtVendorLogin);

        signup = (Button)findViewById(R.id.btnVendorSignup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupVendor.this,Login.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = edtFirstName.getText().toString().trim();
                lname = edtLastName.getText().toString().trim();
                username = edtUsername.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                phone = edtPhoneNumber.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                confirmpassword = edtCOnfirmPassword.getText().toString().trim();
                companyAddress = edtCompanyAddress.getText().toString().trim();
                companyName = edtCompanyName.getText().toString().trim();
                businessType = edtBusinessType.getText().toString().trim();
                bvn = edtBvn.getText().toString().trim();
                cacReg = edtCacReg.getText().toString().trim();
                final SweetAlertDialog pDialog = new SweetAlertDialog(SignupVendor.this, SweetAlertDialog.ERROR_TYPE);


                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    if (fname.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (lname.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (email.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (username.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (phone.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (confirmpassword.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (companyName.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (companyAddress.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (bvn.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (businessType.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (cacReg.isEmpty()){
                        Toast.makeText(SignupVendor.this, "You need a fill out this field", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!password.equalsIgnoreCase(confirmpassword)){
                        Toast.makeText(SignupVendor.this, "Password does not match ,please check it again", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!email.matches(emailPattern)){
                        Toast.makeText(SignupVendor.this, "Please enter a valid email to continue", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new signupVendor().execute(fname,lname,email,username,phone,password,companyName,companyAddress,businessType,bvn,cacReg);

                }
                else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }
            }
        });
    }

    private class signupVendor extends AsyncTask<String,String,String> {
        SweetAlertDialog pDialog = new SweetAlertDialog(SignupVendor.this, SweetAlertDialog.PROGRESS_TYPE);
        SweetAlertDialog sDialog = new SweetAlertDialog(SignupVendor.this, SweetAlertDialog.SUCCESS_TYPE);
        SweetAlertDialog fDialog = new SweetAlertDialog(SignupVendor.this, SweetAlertDialog.ERROR_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Signing you up,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String fName =  strings[0];
            String lName =  strings[1];
            String email =  strings[2];
            String Username =  strings[3];
            String Phone =  strings[4];
            String Password =  strings[5];
            String comName =  strings[6];
            String comAdd =  strings[7];
            String bType =  strings[8];
            String Bvn =  strings[9];
            String CACReg =  strings[10];

            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("uname", Username);
            edit.apply();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("fname",fName);
            request.addProperty("lname",lName);
            request.addProperty("phone",Phone);
            request.addProperty("email",email);
            request.addProperty("username",Username);
            request.addProperty("password",Password);
            request.addProperty("businessType",bType);
            request.addProperty("companyname",comName);
            request.addProperty("address",comAdd);
            request.addProperty("cacregNo",CACReg);
            request.addProperty("Bvn",Bvn);


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
