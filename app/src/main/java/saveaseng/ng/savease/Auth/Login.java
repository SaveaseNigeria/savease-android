package saveaseng.ng.savease.Auth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Activities.ForgotPassword;
import saveaseng.ng.savease.Activities.Home;
import saveaseng.ng.savease.Activities.VendorHome;
import saveaseng.ng.savease.Model.User;
import saveaseng.ng.savease.R;

public class Login extends AppCompatActivity {

    TextView signup,forgottenPass;
    Button login ;
    ImageButton back;

    private EditText edtUsername,edtPassword;
    SharedPreferences preferences;

    private static final String METHOD_NAME = "getlogin";
    private static final String SOAP_ACTION = "http://savease.ng/getlogin";
    private static final String METHOD_NAME2 = "getBalance";
    private static final String SOAP_ACTION2 = "http://savease.ng/getBalance";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private String username,password;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        signup = (TextView)findViewById(R.id.txtForgotPassword);
        forgottenPass= (TextView)findViewById(R.id.txtSign);


        forgottenPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog mDialog = new Dialog(Login.this, R.style.AppBaseTheme);
                mDialog.setContentView(R.layout.dialog_reg);

                Button btnRegisterDialog = (Button)mDialog.findViewById(R.id.btnRegisterDialog);

                btnRegisterDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Login.this,RegisterationActivity.class));
                    }
                });
                mDialog.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        edtUsername = (EditText)findViewById(R.id.edtLoginUsername);
        edtPassword = (EditText)findViewById(R.id.edtLoginPassword);
        back = (ImageButton)findViewById(R.id.imgBack);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE);
        login = (Button)findViewById(R.id.btnLogin);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,AuthWelcome.class));
                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtUsername.getText().toString().trim();
                password = edtPassword.getText().toString().trim();

                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;

                    if (username.isEmpty()){
                        Toast.makeText(Login.this, "Field must be filled", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (password.isEmpty()){
                        Toast.makeText(Login.this, "Field must be filled", Toast.LENGTH_LONG).show();
                        return;
                    }



                    new loginUser().execute(username,password);

                }
                else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }


            }
        });

    }


    private  class loginUser extends AsyncTask<String,String,String>{
        SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("login you in,please wait....");
            this.pDialog.setCancelable(false);
           this. pDialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            String val = String.valueOf(s);
            if (val.equalsIgnoreCase("1")){
                this.pDialog.dismiss();
               new getBalance().execute(username);

            }else if (val.equalsIgnoreCase("100")){
                this.pDialog.dismiss();
                Toast.makeText(Login.this, "User Account Not Found..Please Register", Toast.LENGTH_LONG).show();
                return;

            }else if (val.contains("Could not")){
                this.pDialog.dismiss();
                Toast.makeText(Login.this, val, Toast.LENGTH_LONG).show();
                return;
            }else {
                this.pDialog.dismiss();
                new getBalance().execute(username);

            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String uName =  strings[0];
            String password =  strings[1];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("uname",uName);
            request.addProperty("pword",password);

            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("uname", uName);
            edit.apply();

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
                        re = "Could not login at this time, please try again";
                    }
                }else {
                    re = "Could not login at this time, please try again";
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


    @Override
    public void onBackPressed() {
        confirmDialog(getApplicationContext());
    }

    private void confirmDialog(Context context){

        final AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Alert");
        alert.setMessage("Do you want to close the savease ?");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();
                        finishAffinity();
                        finish();

                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();

                    }
                });

        alert.show();
    }


    private class getBalance extends AsyncTask<String, String, String> {
        User user = new User();
        SweetAlertDialog pDialogLogin = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialogLogin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialogLogin.setTitleText("login you in,please wait....");
            this.pDialogLogin.setCancelable(false);
            this.pDialogLogin.show();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equalsIgnoreCase("")){

            }else {
                String[] separated = s.split(",");

                if (separated[6].equalsIgnoreCase("1")){
                    Toast.makeText(Login.this, "Login Done", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("accountType", "1");
                    edit.putString("userType", "User");
                    edit.apply();
                    Intent intent = new Intent(Login.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();


                }else {

                    Toast.makeText(Login.this, "Login Done", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("accountType", "1");
                    edit.putString("userType", "Vendor");
                    edit.apply();
                    Intent intent = new Intent(Login.this, VendorHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }


            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String re = "";

            String uname = strings[0];
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("straccountNo", uname);


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
                        user.setAccountType(jsonObject1.optString("accountType"));



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                re = user.getSaveaseID() + "," + user.getFname() + "," + user.getLname() + "," + user.getEmail() + "," + user.getPhone() + "," + String.valueOf(user.getBalance()+"0")+ ","+ user.getAccountType();

            }

//

            return re;

        }
    }
}
