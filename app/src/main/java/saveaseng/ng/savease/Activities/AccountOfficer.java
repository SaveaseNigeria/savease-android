package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URLEncoder;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.R;

public class AccountOfficer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditText edtMessage;
    private static final String METHOD_NAME = "getAcctOfficer";
    private static final String SOAP_ACTION = "http://savease.ng/getAcctOfficer";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    SweetAlertDialog pDialog;
    Button sendMessage;
    TextView fullNameT, acctNumber, balanceT,accountOfficerName,accountOfficerNumber,accountOfficerEmail,accntOfficerWhatsapp,userType;
    private String accountNumber, accountName,emailAdd,fulln;
    SharedPreferences preferences;
    boolean connected = false;
    Toolbar toolbar;
    String accountOffiicerNum,accountType;
    private DrawerLayout drawer;
    private LinearLayout linWhatsapp,linPhoneNumber,linAccountOfficerMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_office_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountName = preferences.getString("uname", "");
        accountNumber = preferences.getString("accountNumber", "");
        accountType =preferences.getString("userType", "");

        fullNameT = (TextView) findViewById(R.id.txtAccountName);
        userType = (TextView) findViewById(R.id.txtAccountType);
        userType.setText(accountType);
        acctNumber = (TextView) findViewById(R.id.txtAccountNumber);
        balanceT = (TextView) findViewById(R.id.txtAccountBalance);
        accountOfficerName = (TextView) findViewById(R.id.accountOfficerName);
        accountOfficerEmail = (TextView) findViewById(R.id.accntOfficerEmail);
        accountOfficerNumber = (TextView) findViewById(R.id.accntOfficerNumber);
        accntOfficerWhatsapp = (TextView) findViewById(R.id.accntOfficerWhatsapp);
        TextView saveaseUsername = (TextView) findViewById(R.id.saveaseUserName);
        linPhoneNumber = (LinearLayout)findViewById(R.id.linPhoneNumber);
        linWhatsapp = (LinearLayout)findViewById(R.id.linWhatsapp);
        linAccountOfficerMail = (LinearLayout)findViewById(R.id.linAccountOfficerMail);


        linAccountOfficerMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });


        linPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Call();
            }
        });


        linWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    WhatsApp();
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_account_office);
        setSupportActionBar(toolbar);

        fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        balanceT.setText("N "+preferences.getString("balance", ""));
        acctNumber.setText(preferences.getString("accountNumber", ""));
        pDialog = new SweetAlertDialog(AccountOfficer.this, SweetAlertDialog.ERROR_TYPE);

        edtMessage = (EditText)findViewById(R.id.edtMessageComplaintAccount);
        sendMessage = (Button)findViewById(R.id.btnSendAccountMessage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        saveaseUsername.setText(fulln);




        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            new getBalance().execute(accountNumber);

        } else {
            connected = false;
            pDialog.setTitleText("Error")
                    .setContentText("Seems you are not connected to the internet, please do so and try again").show();
        }
        fullNameT.setText(fulln);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_account_officer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_account_officer);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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
                startActivity(new Intent(AccountOfficer.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(AccountOfficer.this,VendorHome.class));
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
            startActivity(new Intent(AccountOfficer.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(AccountOfficer.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(AccountOfficer.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(AccountOfficer.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(AccountOfficer.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(AccountOfficer.this, aboutUs.class));
        } else if (id == R.id.logout) {

            confirmDialog(getApplicationContext());

        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  void Call()
    {

        // Use format with "tel:" and phoneNumber created is
        // stored in u.
        Uri u = Uri.parse("tel:" + accountOffiicerNum);

        // Create the intent and set the data for the
        // intent as the phone number.
        Intent i = new Intent(Intent.ACTION_DIAL, u);

        try
        {
            // Launch the Phone app's dialer with a phone
            // number to dial a call.
            startActivity(i);
        }
        catch (SecurityException s)
        {
            // show() method display the toast with
            // exception message.
            Toast.makeText(this, s.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void WhatsApp() {

        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone="+ "234"+accountOffiicerNum +"&text=" + URLEncoder.encode("Hello i am from savease and you are my account officer", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void sendMail() {

        String message = edtMessage.getText().toString().trim();

        String[] TO = {emailAdd};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Message from  "+ fulln);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AccountOfficer.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    private class getBalance extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("")){

            }else {
                String[] separated = s.split(",");
                accountOfficerName.setText(separated[0]+" "+separated[1]);
                accountOfficerEmail.setText(separated[3]);
                accountOfficerNumber.setText(separated[2]);
                accntOfficerWhatsapp.setText(separated[2]);
                accountOffiicerNum = separated[2];
                emailAdd = separated[3];
                SharedPreferences preferences = getSharedPreferences("accountOfficer", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("accountName", separated[0]+" "+separated[1]);
                edit.putString("accountOfficerEmail", separated[3]);
                edit.putString("accountOfficerNumber", separated[2]);
                edit.apply();
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String re = "";

            String uname = strings[0];
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("saveaseID", uname);


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
            SoapObject response = (SoapObject) envelope.bodyIn;


            if (response == null){
                re = "";
            }else {
                re = response.getProperty(0).toString();

                try {
                    JSONArray jsonArray = new JSONArray(re);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        re = jsonObject1.optString("firstName")+","+jsonObject1.optString("surName")+","+jsonObject1.optString("Phone")+","+jsonObject1.optString("emailID");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return re;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            new getBalance().execute(accountNumber);

        } else {
            connected = false;
            pDialog.setTitleText("Error")
                    .setContentText("Seems you are not connected to the internet, please do so and try again").show();
        }

    }

    private void confirmDialog(Context context){

        final AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Logout");
        alert.setMessage("Do you want to logout ?");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
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
                        Intent intent = new Intent(AccountOfficer.this, Login.class);
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
}
