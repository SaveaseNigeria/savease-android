package saveaseng.ng.savease.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

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
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.R;

public class VerifyHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView fullNameT,acctNumber,balanceT,userType;
    private String accountName,accountType;
    SharedPreferences preferences;
    private DrawerLayout drawer;
    private static final String METHOD_NAME = "VerifyPin";
    private static final String SOAP_ACTION = "http://savease.ng/VerifyPin";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private EditText edtVoucher;
    private Button btnCheckVoucher;
    private String voucher;
    Dialog dialog;

    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_home_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountName =preferences.getString("uname", "");
        accountType =preferences.getString("userType", "");
        dialog = new Dialog(this);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_verify_home);
        setSupportActionBar(toolbar);

        fullNameT =(TextView)findViewById(R.id.txtAccountName);
        acctNumber =(TextView)findViewById(R.id.txtAccountNumber);
        userType =(TextView)findViewById(R.id.txtAccountType);
        balanceT =(TextView)findViewById(R.id.txtAccountBalance);
        edtVoucher = (EditText)findViewById(R.id.edtVerifyPin);
        btnCheckVoucher = (Button)findViewById(R.id.btnVerify);

        String fulln = preferences.getString("fname","") +" " + preferences.getString("lname","");
        balanceT.setText("N "+preferences.getString("balance",""));
        acctNumber.setText(preferences.getString("accountNumber",""));
        fullNameT.setText(fulln);
        final SweetAlertDialog pDialog = new SweetAlertDialog(VerifyHome.this, SweetAlertDialog.ERROR_TYPE);

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
                        Toast.makeText(VerifyHome.this, "You need a input a voucher pin to actually verify", Toast.LENGTH_LONG).show();
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

        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyHome.this,AccountOfficer.class));
            }
        });



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_verify_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_verify_home);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private class verifyVoucher extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(VerifyHome.this, SweetAlertDialog.PROGRESS_TYPE);
        SweetAlertDialog sDialog = new SweetAlertDialog(VerifyHome.this, SweetAlertDialog.SUCCESS_TYPE);
        SweetAlertDialog fDialog = new SweetAlertDialog(VerifyHome.this, SweetAlertDialog.ERROR_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Loading");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected String doInBackground(String... voids) {

           // edtVoucher.setText("");

            String value =  voids[0];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("inputParame",value);


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

            if (s.contains("Invalid")){
                new FancyAlertDialog.Builder(VerifyHome.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("The voucher is invalid")
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


            }else if (s.contains("Voucher")){
                new FancyAlertDialog.Builder(VerifyHome.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("The voucher has already being used")
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


            }else {
                new FancyAlertDialog.Builder(VerifyHome.this)
                        .setTitle("Success")
                        .setBackgroundColor(Color.parseColor("#212435"))
                        .setMessage(s)
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

                            }
                        })
                        .build();


            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (accountType.equalsIgnoreCase("user")){
            if (id == R.id.homeBack) {
                startActivity(new Intent(VerifyHome.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(VerifyHome.this,VendorHome.class));
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.makeTransfer) {
            startActivity(new Intent(VerifyHome.this,TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(VerifyHome.this,TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(VerifyHome.this, VerifyPinActivity.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(VerifyHome.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(VerifyHome.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(VerifyHome.this,aboutUs.class));
        }else if (id == R.id.logout){
          confirmDialog(getApplicationContext());
        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void confirmDialog(Context context){

        final AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Logout");
        alert.setMessage("Do you want to logout ?");
        alert.setIcon(R.drawable.ic_problem);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();
                        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("accountType", "0");
                        edit.apply();
                        Intent intent = new Intent(VerifyHome.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{

            if (accountType.equalsIgnoreCase("user")){
                startActivity(new Intent(VerifyHome.this,Home.class));

            }else {
                startActivity(new Intent(VerifyHome.this,VendorHome.class));
            }

        }
    }
}
