package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.ArrayList;

import saveaseng.ng.savease.Adapter.TransactionAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.TransactionModel;
import saveaseng.ng.savease.R;

public class TransactionStatementActivity extends Home {

    private static final String METHOD_NAME = "getTransactionUsers";
    private static final String SOAP_ACTION = "http://savease.ng/getTransactionUsers";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    SharedPreferences preferences;
    private DrawerLayout drawer;
    TransactionAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private TextView accntName,accntNumber,balance,userType;
    private RecyclerView rvTRans;
    String username,accountType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_nav);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = preferences.getString("uname", "");
        accountType =preferences.getString("userType", "");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_transaction);
        setSupportActionBar(toolbar);

        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionStatementActivity.this,AccountOfficer.class));
            }
        });

        accntName = (TextView)findViewById(R.id.txtAccountName);
        accntNumber = (TextView)findViewById(R.id.txtAccountNumber);
        balance = (TextView)findViewById(R.id.txtAccountBalance);
        userType = (TextView)findViewById(R.id.txtAccountType);

        userType.setText(accountType);


        String fulln = preferences.getString("fname","") +" " + preferences.getString("lname","");
        balance.setText("N "+preferences.getString("balance",""));
        accntNumber.setText(preferences.getString("accountNumber",""));
        accntName.setText(fulln);


        rvTRans = (RecyclerView)findViewById(R.id.rvTransactions);
        mLayoutManager = new LinearLayoutManager(this);
        rvTRans.setLayoutManager(mLayoutManager);
        rvTRans.setHasFixedSize(true);

       new getTransaction().execute(username);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_transaction);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_transaction);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);




    }

    private class getTransaction extends AsyncTask<String,String, ArrayList<TransactionModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<TransactionModel> transactionModels) {
            super.onPostExecute(transactionModels);

            adapter = new TransactionAdapter(transactionModels);
            rvTRans.setAdapter(adapter);
        }

        @Override
        protected ArrayList<TransactionModel> doInBackground(String... strings) {
            ArrayList<TransactionModel> transactionModels = new ArrayList<>();

            String uname =  strings[0];
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("uname",uname);



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
            SoapObject response = (SoapObject) envelope.bodyIn;

            String re = response.getProperty(0).toString();

            if (re != null){
                try {
                    JSONArray jsonArray = new JSONArray(re);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        TransactionModel model = new TransactionModel();

                        model.setAccountNUmber(jsonObject1.optString("AccountNo"));
                        model.setCredit(jsonObject1.optString("credit"));
                        model.setDebit(jsonObject1.optString("debit"));
                        model.setRefNumber(jsonObject1.optString("RefNumber"));
                        model.setSenderName(jsonObject1.optString("SenderName"));
                        model.setTransDate(jsonObject1.optString("TransactionDate"));
                        model.setTransType(jsonObject1.optString("TransactionType"));


                        transactionModels.add(model);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                transactionModels = new ArrayList<>();
            }

            return transactionModels;
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (accountType.equalsIgnoreCase("user")){
                startActivity(new Intent(TransactionStatementActivity.this,Home.class));

            }else {
                startActivity(new Intent(TransactionStatementActivity.this,VendorHome.class));
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

        //noinspection homeBack
        if (accountType.equalsIgnoreCase("user")){
            if (id == R.id.homeBack) {
                startActivity(new Intent(TransactionStatementActivity.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(TransactionStatementActivity.this,VendorHome.class));
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
            startActivity(new Intent(TransactionStatementActivity.this,TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(TransactionStatementActivity.this,TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(TransactionStatementActivity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(TransactionStatementActivity.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(TransactionStatementActivity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(TransactionStatementActivity.this,aboutUs.class));
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
                        Intent intent = new Intent(TransactionStatementActivity.this, Login.class);
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
