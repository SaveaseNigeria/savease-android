package saveaseng.ng.savease.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

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
import im.delight.android.location.SimpleLocation;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Auth.ChangePassword;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.Model.VendorModel;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiCli;
import saveaseng.ng.savease.Utils.ApiInterface;

public class Settings extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    RelativeLayout linChangePassword;
    DatabaseReference vendorRef;
    double latitude,longitude;
    String address,phone,name,state;
    private DrawerLayout drawer;
    Switch simpleSwitch1;
    private SimpleLocation mLocation;
    private static final String METHOD_NAME = "updateUlevel";
    private static final String SOAP_ACTION = "http://savease.ng/updateUlevel";

    private static final String METHOD_NAME2 = "updatesaveaseBusiness";
    private static final String SOAP_ACTION2 = "http://savease.ng/updatesaveaseBusiness";


    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final static String SMS_API_KEY = "9Pc1XtdCYg43wdJ6AlbCSCyTlLqc2voEFpl9DvmUq0zcKJTDbdE4aOYOPtzz";
    private String accountNum,relation,relationType;
    SharedPreferences preferences;
    Dialog mDialog,cDialog;
    private static final String[] transferBankType = {
            "Relationship with Kin",
            "Father",
            "Mother",
            "Sister",
            "Brother",
            "Wife",
            "Husband",
            "Son",
            "Daughter",
            "Other"

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_settings);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountNum =preferences.getString("accountNumber", "");
        phone =preferences.getString("phone", "");
        vendorRef = FirebaseDatabase.getInstance().getReference().child("Vendors");
        simpleSwitch1 = (Switch) findViewById(R.id.simpleSwitch);

        Context context = this;
        boolean requireFineGranularity = false;
        boolean passiveMode = false;
        long updateIntervalInMilliseconds = 10 * 60 * 1000;
        mLocation = new SimpleLocation(this, requireFineGranularity, passiveMode, updateIntervalInMilliseconds);

        // reduce the precision to 5,000m for privacy reasons
        mLocation.setBlurRadius(100);

        // if we can't access the location yet
        if (!mLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

      simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (simpleSwitch1.isChecked()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                    builder.setMessage("Are you sure you want to upgrade your account to become a vendor?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mDialog = new Dialog(Settings.this, R.style.AppBaseTheme);
                                    mDialog.setContentView(R.layout.upgrade);
                                    TextView userNameUpgrade = (TextView)mDialog.findViewById(R.id.userNameUpgrade);
                                    name  = preferences.getString("fname","");
                                    userNameUpgrade.setText("Dear " +name);


                                    Button btnRegisterDialog = (Button)mDialog.findViewById(R.id.btnVerifyBvnDialog);
                                    ImageButton imgBack = (ImageButton)mDialog.findViewById(R.id.imgBack);
                                    final CheckBox agree = mDialog.findViewById(R.id.chkAgree);
                                    imgBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            simpleSwitch1.setChecked(false);
                                            mDialog.dismiss();

                                        }
                                    });

                                    btnRegisterDialog.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (agree.isChecked()){


                                                cDialog = new Dialog(Settings.this, R.style.AppBaseTheme);
                                                cDialog.setContentView(R.layout.upgrade_continue);

                                                Button btnRegisterDialog = (Button)cDialog.findViewById(R.id.btnUpgrade);
                                                EditText officeAddress = (EditText)cDialog.findViewById(R.id.edtBusinessAddress);
                                                EditText officeState = (EditText)cDialog.findViewById(R.id.edtBusinessState);
                                                EditText officeTown = (EditText)cDialog.findViewById(R.id.edtBusinessTown);
                                                EditText edtHomeAddress = (EditText)cDialog.findViewById(R.id.edtHomeAddress);
                                                EditText edtHomeState = (EditText)cDialog.findViewById(R.id.edtHomeState);
                                                EditText edtHomeTown = (EditText)cDialog.findViewById(R.id.edtHomeTown);
                                                EditText edtNextOfKin = (EditText)cDialog.findViewById(R.id.edtNextKin);
                                                EditText edtNextOfKinOther = (EditText)cDialog.findViewById(R.id.edtNextKinOther);
                                                EditText edtNextOfKinPhone = (EditText)cDialog.findViewById(R.id.edtNextKinPhone);
                                                ImageButton imgBack = (ImageButton)cDialog.findViewById(R.id.imgBack);
                                                 MaterialSpinner spinnerRelationship = (MaterialSpinner)cDialog.findViewById(R.id.spinRelationship);
                                                imgBack.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        simpleSwitch1.setChecked(false);
                                                        cDialog.dismiss();

                                                    }
                                                });


                                                spinnerRelationship.setItems(transferBankType);
                                                spinnerRelationship.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                                                    @Override
                                                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                                                        relationType = item;

                                                        if (relationType.equalsIgnoreCase("Relationship with Kin")){

                                                            relation = "";

                                                        }else if (relationType.equalsIgnoreCase("Other")){
                                                            edtNextOfKinOther.setVisibility(View.VISIBLE);


                                                        }else {
                                                            relation = item;

                                                        }
                                                    }
                                                });
                                                spinnerRelationship.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
                                                    @Override
                                                    public void onNothingSelected(MaterialSpinner spinner) {
                                                        Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
                                                    }
                                                });

                                                btnRegisterDialog.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        address = officeAddress.getText().toString().trim();
                                                        state = officeState.getText().toString().trim();
                                                        String officeTw = officeTown.getText().toString().trim();
                                                        String homeAdd = edtHomeAddress.getText().toString().trim();
                                                        String homeSt = edtHomeState.getText().toString().trim();
                                                        String homeTw = edtHomeTown.getText().toString().trim();
                                                        String nxtOfKin = edtNextOfKin.getText().toString().trim();
                                                        String nxtOfKinPhone = edtNextOfKinPhone.getText().toString().trim();

                                                        if (relationType.equalsIgnoreCase("other")){
                                                            relation = edtNextOfKinOther.getText().toString().trim();
                                                        }


                                                        if (address.isEmpty() || state.isEmpty()|| officeTw.isEmpty()|| homeAdd.isEmpty()|| homeSt.isEmpty()|| homeTw.isEmpty()|| nxtOfKin.isEmpty()|| nxtOfKinPhone.isEmpty()){
                                                            Toast.makeText(Settings.this, "Please fill out all the fields before proceeding", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }

                                                        if (relationType.equalsIgnoreCase("Relationship with Kin")){
                                                            Toast.makeText(Settings.this, "Please select your relationship with next of kin", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }


                                                        new saveDetails().execute(accountNum,address,officeTw,state,homeAdd,homeTw,homeSt,nxtOfKin,nxtOfKinPhone,relation);



                                                    }
                                                });
                                                cDialog.show();



                                            }else {
                                                Toast.makeText(Settings.this, "Please agree to the Terms and Condition to continue", Toast.LENGTH_LONG).show();
                                                return;
                                            }


                                        }
                                    });
                                    mDialog.show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    simpleSwitch1.setChecked(false);
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }else{

                }

            }
        });

        linChangePassword = (RelativeLayout)findViewById(R.id.linChangePassword);
        linChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, ChangePassword.class));
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homee, menu);
        return true;
    }

    @Override
    protected void onPause() {
        mLocation.endUpdates();
        super.onPause();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.homeBack) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.deposits) {
            startActivity(new Intent(getApplicationContext(),DepositActivity.class));
            // Handle the camera action
        } else if (id == R.id.makeTransfer) {
            startActivity(new Intent(getApplicationContext(),TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(getApplicationContext(),TransactionStatementActivity.class));

        } else if (id == R.id.settings) {

            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("accountType","0");
            edit.apply();
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }  else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocation.beginUpdates();

    }


    private class upgradeAccount extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(Settings.this, SweetAlertDialog.PROGRESS_TYPE);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Upgrading..please wait");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected String doInBackground(String... voids) {

            // edtVoucher.setText("");

            String value =  voids[0];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("saveaseid",value);


            SoapSerializationEnvelope envelope = new      SoapSerializationEnvelope(SoapEnvelope.VER11);
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

            SmartLocation.with(Settings.this).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });

            VendorModel vendorModel = new VendorModel();
            vendorModel.setLatitude(latitude);
            vendorModel.setLongitude(longitude);
            vendorModel.setVendorAddress(address);
            vendorModel.setVendorName(name);
            vendorModel.setVendorNumber(phone);
            vendorModel.setStatus("Not Available");


            vendorRef.child(accountNum).setValue(vendorModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (s.equalsIgnoreCase("1")){
                                    sendMessage();
                                    final Dialog dialog = new Dialog(Settings.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.success_dialog);
                                    TextView message = dialog.findViewById(R.id.txtMessage);
                                    message.setText("Welcome to Savease Business. You are now a \nSavease Agent/Vendor, start selling and start getting commissions.");
                                    Button discard = dialog.findViewById(R.id.btnDismiss);
                                    discard.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor edit = preferences.edit();
                                            edit.putString("accountType", "0");
                                            edit.apply();
                                            Intent intent = new Intent(Settings.this, Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finishAffinity();
                                            finish();


                                        }
                                    });
                                    dialog.show();

                                }else {
                                    final Dialog dialog = new Dialog(Settings.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.failure_dialog);
                                    TextView message = dialog.findViewById(R.id.txtMessage);
                                    message.setText("Account Upgrade was unsuccessful was unsuccessful. Please try again ");
                                    Button discard = dialog.findViewById(R.id.btnDismiss);
                                    discard.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            simpleSwitch1.setChecked(false);
                                            dialog.dismiss();
                                        }
                                    });


                                    dialog.show();

                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Settings.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }
            });


        }
    }

    private class saveDetails extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(Settings.this, SweetAlertDialog.PROGRESS_TYPE);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Upgrading..please wait");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected String doInBackground(String... voids) {

            // edtVoucher.setText("");

            String value =  voids[0];
            String businessAdd =  voids[1];
            String businessTown =  voids[2];
            String businessState =  voids[3];
            String homeAdd =  voids[4];
            String homeTown =  voids[5];
            String homeState =  voids[6];
            String nextOFK =  voids[7];
            String nextOfKPhone =  voids[8];
            String relationship =  voids[9];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("saveaseId",value);
            request.addProperty("BusinessAddress",businessAdd);
            request.addProperty("BusinessAddress_Town",businessTown);
            request.addProperty("BusinessAddress_State",businessState);
            request.addProperty("HomeAddress",homeAdd);
            request.addProperty("HomeAddress_town",homeTown);
            request.addProperty("HomeAddress_State",homeState);
            request.addProperty("NextOfKin",nextOFK);
            request.addProperty("NextOfkin_Phone",nextOfKPhone);
            request.addProperty("Relationship_With_Kin",relationship);


            SoapSerializationEnvelope envelope = new      SoapSerializationEnvelope(SoapEnvelope.VER11);
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
            return String.valueOf(result);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.pDialog.dismiss();

            if (s.equalsIgnoreCase("1")){
                new upgradeAccount().execute(accountNum);
            }else {
                Toast.makeText(Settings.this, "Details was not saved, try again later", Toast.LENGTH_LONG).show();
                simpleSwitch1.setChecked(false);
            }

        }



    }

    private void sendMessage() {

        String acct = "0"+preferences.getString("accountNumber","");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ApiInterface apiInterface  = ApiCli.getClient().create(ApiInterface.class);

        Call<SmsRes> call = apiInterface.sendMessage(SMS_API_KEY,"Savease",acct,"Welcome to Savease Business. You are now a Savease Agent/Vendor which was in effect stating from  "+formattedDate+",  start selling and start getting commissions.","2");

        call.enqueue(new Callback<SmsRes>() {
            @Override
            public void onResponse(Call<SmsRes> call, Response<SmsRes> response) {
                if (response.body() != null){

                    if (response.body().getData().getStatus().equalsIgnoreCase("success")){


                    }else{
                        Toast.makeText(Settings.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(Settings.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SmsRes> call, Throwable t) {
                Toast.makeText(Settings.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
