package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.ArrayList;

import saveaseng.ng.savease.Adapter.NotificationAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.NotificationModel;
import saveaseng.ng.savease.R;

public class NotificationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String METHOD_NAME = "getTransMessageNotification";
    private static final String SOAP_ACTION = "http://savease.ng/getTransMessageNotification";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    SharedPreferences preferences;
    private DrawerLayout drawer;
    private NotificationAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView rvTRans;
    Toolbar toolbar;
    LinearLayout linearLayout;
    String username,accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_beneficiary_nav);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_notification);
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = preferences.getString("uname", "");
        accountType =preferences.getString("userType", "");


        rvTRans = (RecyclerView)findViewById(R.id.rvNotification);
        mLayoutManager = new LinearLayoutManager(this);
        rvTRans.setLayoutManager(mLayoutManager);
        rvTRans.setHasFixedSize(true);

        new getTransaction().execute(username);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notification);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_notification);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private class getTransaction extends AsyncTask<String,String, ArrayList<NotificationModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<NotificationModel> transactionModels) {
            super.onPostExecute(transactionModels);

            adapter = new NotificationAdapter(transactionModels);
            rvTRans.setAdapter(adapter);
        }

        @Override
        protected ArrayList<NotificationModel> doInBackground(String... strings) {
            ArrayList<NotificationModel> transactionModels = new ArrayList<>();

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
                        NotificationModel model = new NotificationModel();
                        model.setTransRef(jsonObject1.optString("TransRef"));
                        model.setMessage(jsonObject1.optString("Messages"));
                        model.setTransDate(jsonObject1.optString("tdate"));
                        model.setTransType(jsonObject1.optString("TransType"));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homee,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (accountType.equalsIgnoreCase("user")){
            if (id == R.id.homeBack) {
                startActivity(new Intent(NotificationActivity.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(NotificationActivity.this,VendorHome.class));
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
            startActivity(new Intent(NotificationActivity.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(NotificationActivity.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(NotificationActivity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(NotificationActivity.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(NotificationActivity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(NotificationActivity.this, aboutUs.class));
        } else if (id == R.id.logout) {
            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("accountType", "0");
            edit.apply();
            Intent intent = new Intent(NotificationActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
