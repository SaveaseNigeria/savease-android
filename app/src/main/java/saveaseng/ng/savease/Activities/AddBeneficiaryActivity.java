package saveaseng.ng.savease.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Timer;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.R;

public class AddBeneficiaryActivity extends Home {

    private EditText edtAccntNumber;
    private static final String METHOD_NAME = "addBeneficiary";
    private static final String SOAP_ACTION = "http://savease.ng/addBeneficiary";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button addBeneficiary;
    TextView fullNameT, acctNumber, balanceT;
    private String accountNumber, accountName, bankName;
    SharedPreferences preferences;
    boolean connected = false;
    Toolbar toolbar;
    LinearLayout linearLayout;
    private DrawerLayout drawer;
    private MaterialSpinner spinnerBeneficiaryBank;
    private String bank = "";
    private Timer timer;


    private static final String[] bankType = {
            "Account Type ",
            "Savease Wallet",
            "Diamond Bank",
            "Eco Bank",
            "First Bank",
            "GT Bank",
            "UBA",
            "Wema Bank",
            "Zenith Bank"

    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountName = preferences.getString("uname", "");

        fullNameT = (TextView) findViewById(R.id.txtAccountName);
        acctNumber = (TextView) findViewById(R.id.txtAccountNumber);
        balanceT = (TextView) findViewById(R.id.txtAccountBalance);
        linearLayout = findViewById(R.id.linear);

        edtAccntNumber = (EditText) findViewById(R.id.edtSaveaseAccountNumberben);

        Button accontOfficer = (Button)findViewById(R.id.btnAccountOfficerBen);
        accontOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddBeneficiaryActivity.this,AccountOfficer.class));
            }
        });


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_beneficiary);
        setSupportActionBar(toolbar);

        String fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        balanceT.setText(preferences.getString("balance", ""));
        acctNumber.setText(preferences.getString("accountNumber", ""));
        fullNameT.setText(fulln);

        spinnerBeneficiaryBank = (MaterialSpinner) findViewById(R.id.spinBeneficiaryBankType);
        spinnerBeneficiaryBank.setItems(bankType);
        spinnerBeneficiaryBank.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                bank = item;
                linearLayout.setVisibility(View.VISIBLE);
                if (bank.equalsIgnoreCase("Savease Wallet")) {


                }
                if (bank.equalsIgnoreCase("Account Type ")) {
                    linearLayout.setVisibility(View.GONE);

                }

            }
        });
        spinnerBeneficiaryBank.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();

            }
        });


        addBeneficiary = (Button) findViewById(R.id.btnSaveBeneficiary);

        final SweetAlertDialog pDialog = new SweetAlertDialog(AddBeneficiaryActivity.this, SweetAlertDialog.ERROR_TYPE);

        addBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accountNumber = edtAccntNumber.getText().toString().trim();


                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    if (accountNumber.isEmpty()) {
                        Toast.makeText(AddBeneficiaryActivity.this, "You need a fill out this field", Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (bank.isEmpty()) {
                        Toast.makeText(AddBeneficiaryActivity.this, "You need select a bank", Toast.LENGTH_LONG).show();
                        return;
                    }


                    new addBenficiary().execute(accountName, accountNumber, bank);

                } else {
                    connected = false;
                    pDialog.setTitleText("Error")
                            .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_beneficiary);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_beneficiary);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private class addBenficiary extends AsyncTask<String, String, String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(AddBeneficiaryActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Adding Beneficiary ,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.pDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {
                final Dialog dialog = new Dialog(AddBeneficiaryActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.success_dialog);
                TextView message = dialog.findViewById(R.id.txtMessage);
                message.setText("You have successfully added a beneficiary.");
                Button discard = dialog.findViewById(R.id.btnDismiss);
                discard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();

            } else {
                final Dialog dialog = new Dialog(AddBeneficiaryActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.failure_dialog);
                TextView message = dialog.findViewById(R.id.txtMessage);
                message.setText("Adding beneficiary was unsuccessful");
                Button discard = dialog.findViewById(R.id.btnDismiss);
                discard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String bankname = strings[2];
            String accnt = strings[1];
            String depositor = strings[0];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("accountNumber", accnt);
            request.addProperty("savedFor", depositor);
            request.addProperty("bankname", bankname);


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
            try {
                result = (Object) envelope.getResponse();
                Log.i("RESPONSE", String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return String.valueOf(result);

        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        if (id == R.id.homeBack) {
            startActivity(new Intent(AddBeneficiaryActivity.this, Home.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.deposits) {
            startActivity(new Intent(AddBeneficiaryActivity.this, DepositActivity.class));
            // Handle the camera action
        } else if (id == R.id.makeTransfer) {
            startActivity(new Intent(AddBeneficiaryActivity.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(AddBeneficiaryActivity.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(AddBeneficiaryActivity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(AddBeneficiaryActivity.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(AddBeneficiaryActivity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(AddBeneficiaryActivity.this, aboutUs.class));
        } else if (id == R.id.logout) {
            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("accountType", "0");
            edit.apply();
            Intent intent = new Intent(AddBeneficiaryActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
