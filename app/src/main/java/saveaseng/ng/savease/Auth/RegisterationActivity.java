package saveaseng.ng.savease.Auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alahammad.otp_view.OtpView;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiCli;
import saveaseng.ng.savease.Utils.ApiInterface;

public class RegisterationActivity extends AppCompatActivity {
    private final static String SMS_API_KEY = "9Pc1XtdCYg43wdJ6AlbCSCyTlLqc2voEFpl9DvmUq0zcKJTDbdE4aOYOPtzz";
    private static final String METHOD_NAME = "Existphone";
    private static final String SOAP_ACTION = "http://savease.ng/Existphone";

    private static final String METHOD_NAME2 = "ExistUname";
    private static final String SOAP_ACTION2 = "http://savease.ng/ExistUname";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    Button btnContinue;
    ImageButton back;
    TextView txtSigninReg;
    android.app.AlertDialog alertDialog;
    android.app.AlertDialog alertDialog1;
    EditText edtEmail, edtPassword, edtConfirmPassword,edtPhoneNumber;
    String username,confirmPassword,password,phone;
    OtpView otpView;
    SmsVerifyCatcher smsVerifyCatcher;
    private String mVerificationId;
    Dialog dialog;
    FirebaseAuth mAuth;
    Pinview pinviewOtp;
    SweetAlertDialog pDialog;
    boolean connected = false;
    int otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        dialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mAuth = FirebaseAuth.getInstance();
        btnContinue = (Button) findViewById(R.id.btnContinueReg);
        back = (ImageButton) findViewById(R.id.imgBackRegister);
        edtEmail = (EditText) findViewById(R.id.edtRegisterUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        txtSigninReg = (TextView) findViewById(R.id.txtSigninReg);
        edtPhoneNumber = (EditText)findViewById(R.id.edtUserSignupPhone);
         pDialog = new SweetAlertDialog(RegisterationActivity.this, SweetAlertDialog.PROGRESS_TYPE);


//

        txtSigninReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterationActivity.this, Login.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterationActivity.this, AuthWelcome.class));
            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                confirmPassword = edtConfirmPassword.getText().toString().trim();
                phone = edtPhoneNumber.getText().toString().trim();

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    new checkPhone().execute(phone);

                } else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }



            }
        });


    }




        public void sendSms(String mobile) {
                Random random = new Random();
                otp = random.nextInt(999999);

                // Construct data

            ApiInterface apiInterface  = ApiCli.getClient().create(ApiInterface.class);

            Call<SmsRes> call = apiInterface.sendMessage(SMS_API_KEY,"Savease",mobile,"Your Savease ID verification code is .. "+otp ,"2");

            call.enqueue(new Callback<SmsRes>() {
                @Override
                public void onResponse(Call<SmsRes> call, Response<SmsRes> response) {
                    if (response.body() != null){

                        if (response.body().getData().getStatus().equalsIgnoreCase("success")){
                            SharedPreferences preferences = getSharedPreferences("OTP", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putInt("otp",otp);
                            edit.apply();
                        }else{
                            Toast.makeText(RegisterationActivity.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(RegisterationActivity.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SmsRes> call, Throwable t) {
                    Toast.makeText(RegisterationActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    private class checkUsername extends AsyncTask<String,String,String> {
        SweetAlertDialog pDialog = new SweetAlertDialog(RegisterationActivity.this, SweetAlertDialog.PROGRESS_TYPE);

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


            String Username =  strings[0];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("username",Username);



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

                new FancyAlertDialog.Builder(RegisterationActivity.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Username already in use.. try another")
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
                sendSms(phone);
                final Rect[] displayRectangle = {new Rect()};
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle[0]);
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterationActivity.this, R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_otp_layout, viewGroup, false);
                dialogView.setMinimumWidth((int) (displayRectangle[0].width() * 1f));
                dialogView.setMinimumHeight((int) (displayRectangle[0].height() * 1f));
                builder.setView(dialogView);
                alertDialog1 = builder.create();
                alertDialog1.show();
                final String[] pinCode = new String[1];

                ImageButton backPin = dialogView.findViewById(R.id.imgBackPinView);
                backPin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });



                pinviewOtp = dialogView.findViewById(R.id.otp);
                pinviewOtp.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinview, boolean fromUser) {
                        pinCode[0] = pinview.getValue();
                        SharedPreferences preferences = getSharedPreferences("OTP", Context.MODE_PRIVATE);
                        int otp = preferences.getInt("otp", 0);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Verifying otp,please wait....");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        if (pinCode[0].equalsIgnoreCase(String.valueOf(otp))){


                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterationActivity.this, "Phone number verified successfully", Toast.LENGTH_LONG).show();
                                            final Rect[] displayRectangle2 = {new Rect()};
                                            Window window2 = getWindow();
                                            window2.getDecorView().getWindowVisibleDisplayFrame(displayRectangle2[0]);
                                            final android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(RegisterationActivity.this, R.style.CustomAlertDialog);
                                            ViewGroup viewGroup2 = findViewById(android.R.id.content);
                                            View dialogView2 = LayoutInflater.from(viewGroup2.getContext()).inflate(R.layout.dialog_pin_layout, viewGroup2, false);
                                            dialogView2.setMinimumWidth((int) (displayRectangle2[0].width() * 1f));
                                            dialogView2.setMinimumHeight((int) (displayRectangle2[0].height() * 1f));
                                            builder2.setView(dialogView2);
                                            alertDialog = builder2.create();
                                            alertDialog.show();
                                            final String[] pinCode = new String[1];
                                            final String[] confirmPinCOde = new String[1];
                                            Button continued = dialogView2.findViewById(R.id.btnContinueRegPinVIew);
                                            ImageButton backPin = dialogView2.findViewById(R.id.imgBackPinView);
                                            backPin.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    alertDialog.dismiss();
                                                }
                                            });

                                            continued.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    if (!pinCode[0].equalsIgnoreCase(confirmPinCOde[0])) {
                                                        Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                                                        return;

                                                    }

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(RegisterationActivity.this, Signup.class);
                                                    intent.putExtra("username", username);
                                                    intent.putExtra("password", password);
                                                    intent.putExtra("confirm", confirmPassword);
                                                    intent.putExtra("phone",phone);
                                                    intent.putExtra("pinCode",pinCode[0]);
                                                    startActivity(intent);

                                                }
                                            });


                                            Pinview pinview1 = dialogView2.findViewById(R.id.setUpPinView);
                                            pinview1.setPinViewEventListener(new Pinview.PinViewEventListener() {
                                                @Override
                                                public void onDataEntered(Pinview pinview, boolean fromUser) {
                                                    pinCode[0] = pinview.getValue();

                                                }
                                            });


                                            Pinview pinview2 = dialogView2.findViewById(R.id.confirmPinVIew);
                                            pinview2.setPinViewEventListener(new Pinview.PinViewEventListener() {
                                                @Override
                                                public void onDataEntered(Pinview pinview, boolean fromUser) {
                                                    confirmPinCOde[0] = pinview.getValue();

                                                }
                                            });

                                        }
                                    });

                                    // this code will be executed after 2 seconds

                                }
                            }, 5000);
                        }else{
                            pDialog.dismiss();
                            Toast.makeText(RegisterationActivity.this, "Phone number verification unsuccessfully", Toast.LENGTH_LONG).show();
                            pinviewOtp.clearValue();
                        }

                    }
                });

            }



        }
    }

    private class checkPhone extends AsyncTask<String,String,String> {
        SweetAlertDialog pDialog = new SweetAlertDialog(RegisterationActivity.this, SweetAlertDialog.PROGRESS_TYPE);

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




            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("phone",Phone);


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

            if (s.equalsIgnoreCase("0")){
                this.pDialog.dismiss();
                new FancyAlertDialog.Builder(RegisterationActivity.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Phone number already in use..try another")
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
               new checkUsername().execute(username);
            }



        }
    }


}
