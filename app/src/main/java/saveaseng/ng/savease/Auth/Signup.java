package saveaseng.ng.savease.Auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiCli;
import saveaseng.ng.savease.Utils.ApiInterface;

public class Signup extends AppCompatActivity {
    TextView login;
    ImageButton back;
      private EditText edtFirstName,edtLastName,edtEmail,edtUsername;
    private static final String METHOD_NAME = "RegisterUser";
    private static final String SOAP_ACTION = "http://savease.ng/RegisterUser";

    private static final String METHOD_NAME2 = "ExistEmail";
    private static final String SOAP_ACTION2 = "http://savease.ng/ExistEmail";

    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final static String API_KEY = "Bearer sk_live_0dc08582961f9c1d0785245c36bc4a65658ce187";
    private final static String SMS_API_KEY = "9Pc1XtdCYg43wdJ6AlbCSCyTlLqc2voEFpl9DvmUq0zcKJTDbdE4aOYOPtzz";
    Button signup ;
    private String fname,lname,email,username,password,pin,phone;
    SharedPreferences preferences;
    SharedPreferences pref;
    boolean connected = false;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        dialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        pin = getIntent().getStringExtra("pinCode");
        phone = getIntent().getStringExtra("phone");


        back = (ImageButton)findViewById(R.id.imgBackSignup);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(Signup.this,RegisterationActivity.class);
             startActivity(intent);
            }
        });

        edtFirstName = (EditText)findViewById(R.id.edtUserSignupFirstName);
        edtLastName = (EditText)findViewById(R.id.edtUserSignupLastName);

        edtUsername = (EditText)findViewById(R.id.edtUserSignupEmail);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        pref = getSharedPreferences("bvnStatus", Context.MODE_PRIVATE);

        signup = (Button)findViewById(R.id.btnUserSignup);
        final SweetAlertDialog pDialog = new SweetAlertDialog(Signup.this, SweetAlertDialog.ERROR_TYPE);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;

                    fname = edtFirstName.getText().toString().trim();
                    lname = edtLastName.getText().toString().trim();
                    email = edtUsername.getText().toString().trim();


                    if (fname.isEmpty()){
                        Toast.makeText(Signup.this, "First Name not filed ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (lname.isEmpty()){
                        Toast.makeText(Signup.this, "Last Name not filed", Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (email.isEmpty()){
                        Toast.makeText(Signup.this, "Email not filed", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (phone.isEmpty()){
                        Toast.makeText(Signup.this, "Phone Number not filed", Toast.LENGTH_LONG).show();
                        return;
                    }


                    new checkPhone().execute(email);



                }
                else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }
            }
        });
    }



    private class signupUser extends AsyncTask<String,String,String> {
        SweetAlertDialog pDialog = new SweetAlertDialog(Signup.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Signing you up,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String fName =  strings[0];
            String lName =  strings[1];
            String email =  strings[2];
            String Username =  strings[3];
            String Phone =  strings[4];
            String Password =  strings[5];
            String bvn =  strings[6];

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
            request.addProperty("transPIN",bvn);


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
                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                    re = String.valueOf(response);
                    Log.i("RESPONSE",String.valueOf(response));

              // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return re ;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equalsIgnoreCase("1")){
                this.pDialog.dismiss();
                sendMessage();

            }else if (s.equalsIgnoreCase("2")){
                this.pDialog.dismiss();
                Toast.makeText(Signup.this, "Phone number already in use..", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Signup.this,Login.class));
            }else{
                this.pDialog.dismiss();
                Toast.makeText(Signup.this, "Their was an error signing up..please try again", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Signup.this,Login.class));
            }



        }
    }

    private class checkPhone extends AsyncTask<String,String,String> {
        SweetAlertDialog pDialog = new SweetAlertDialog(Signup.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Verifying,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String Phone =  strings[0];




            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);

            request.addProperty("email",Phone);


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
            String re = "";

            try {
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                re = String.valueOf(response);
                Log.i("RESPONSE",String.valueOf(response));

                // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return re ;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equalsIgnoreCase("0")){
                this.pDialog.dismiss();
                new FancyAlertDialog.Builder(Signup.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Email already in use..try another")
                        .setPositiveBtnText("Done")
                        .setPositiveBtnBackground(Color.parseColor("#DF5C4E"))
                        .setNegativeBtnBackground(Color.parseColor("#ffffff"))
                        .setAnimation(Animation.SLIDE)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_cancel, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                dialog.dismiss();

                            }
                        })
                        .build();


            }else if (s.equalsIgnoreCase("1")){
                this.pDialog.dismiss();
                new signupUser().execute(fname,lname,email,username,phone,password,pin);
            }



        }
    }

    @Override
    public void onBackPressed() {

    }

    private void sendMessage() {

        ApiInterface apiInterface  = ApiCli.getClient().create(ApiInterface.class);

        Call<SmsRes> call = apiInterface.sendMessage(SMS_API_KEY,"Savease",phone,"Welcome to Africa's Deposit Gateway. Your account has been activated and you can now make financial transactions with ease. Please do not disclose your password and PIN to anyone as Savease will at no time request for this information. ","2");

        call.enqueue(new Callback<SmsRes>() {
            @Override
            public void onResponse(Call<SmsRes> call, Response<SmsRes> response) {
                if (response.body() != null){

                    if (response.body().getData().getStatus().equalsIgnoreCase("success")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("bvn","false");
                        editor.apply();
                        Toast.makeText(Signup.this, "We have sent a confirmation email and login again..", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Signup.this,Login.class));


                    }else{
                        Toast.makeText(Signup.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(Signup.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SmsRes> call, Throwable t) {
                Toast.makeText(Signup.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
