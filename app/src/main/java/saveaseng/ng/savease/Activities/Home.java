package saveaseng.ng.savease.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.text.DecimalFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.User;
import saveaseng.ng.savease.R;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static long back_pressed;
    private static final String METHOD_NAME = "getBalance";
    private static final String SOAP_ACTION = "http://savease.ng/getBalance";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final int REQUEST_STORAGE_PERMISSION = 1;
    private final int REQUEST_LOCATION_PERMISSION = 2;
    DatabaseReference promotionRef;
    private RecyclerView rvPromotionHome;


    private DrawerLayout drawer;
    SharedPreferences preferences,pref;
    String username;
    CardView card1, card2, card3, card4, card5, card6,card7;
    SweetAlertDialog pDialog;
    boolean connected = false;
    TextView accountNumber, accountBalance, userType, fullName;
    private ImageView imgMakeDeposit, imgTransferFunds, imgAddBeneficiary, imgStatement, imgVendorNearU, imgVoucherTable, verifyVoucher, logout,settings, about,faq,complain,userGuiid,imgWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestLocationPermission();
        requestStoragePermission();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        pref = getSharedPreferences("bvnStatus", Context.MODE_PRIVATE);
        username = preferences.getString("uname", "");
        promotionRef = FirebaseDatabase.getInstance().getReference().child("promotion");
        pDialog = new SweetAlertDialog(Home.this, SweetAlertDialog.ERROR_TYPE);

        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,AccountOfficer.class));
            }
        });


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            new getBalance().execute(username);

        } else {
            connected = false;
            pDialog.setTitleText("Error")
                    .setContentText("Seems you are not connected to the internet, please do so and try again").show();
        }


        accountBalance = (TextView) findViewById(R.id.txtAccountBalance);
        accountNumber = (TextView) findViewById(R.id.txtAccountNumber);
        fullName = (TextView) findViewById(R.id.txtAccountName);
        userType = (TextView) findViewById(R.id.txtAccountType);
        card1 = findViewById(R.id.card_view1);
        card2 = findViewById(R.id.card_view2);
        card3 = findViewById(R.id.card_view3);
        card4 = findViewById(R.id.card_view4);
        card5 = findViewById(R.id.card_view5);
        card6 = findViewById(R.id.card_view6);
        card7 = findViewById(R.id.card_view7);
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("accountType", "0");
                edit.apply();
                Intent intent = new Intent(Home.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Complaint.class));

            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(Home.this,UserGuide.class));
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, FaqActivity.class));
            }
        });
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), aboutUs.class));
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, LatestNews.class));
            }
        });

        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Profile.class));
            }
        });


        imgMakeDeposit = (ImageView) findViewById(R.id.imgMakeDeposit);

        imgMakeDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, DepositActivity.class));
            }
        });



        imgVendorNearU = (ImageView) findViewById(R.id.imgVendorNearU);

        imgVendorNearU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, VendorsAround.class));
            }
        });

        verifyVoucher = (ImageView) findViewById(R.id.imgVerifyVoucher);
        verifyVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, VerifyHome.class));
            }
        });

        imgAddBeneficiary = (ImageView) findViewById(R.id.imgAddBeneficiary);
        imgAddBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, FundAccount.class));
            }
        });

        imgStatement = (ImageView) findViewById(R.id.imgStatment);
        imgStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, TransactionStatementActivity.class));
            }
        });



        imgWithdraw = (ImageView) findViewById(R.id.imgWithdraw);
        imgWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, WithdrawActivity.class));
            }
        });

        imgTransferFunds = (ImageView) findViewById(R.id.imgTransferFunds);
        imgTransferFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, TransferActicvity.class));
            }
        });



        logout = (ImageView) findViewById(R.id.logoutBottom);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(Home.this, Test.class));
               confirmDialog(getApplicationContext());

            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
          //  startActivity(new Intent(Home.this, NotificationActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.deposits) {
            startActivity(new Intent(Home.this, DepositActivity.class));
            // Handle the camera action
        } else if (id == R.id.makeTransfer) {
            startActivity(new Intent(Home.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(Home.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(Home.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            //  startActivity(new Intent(Home.this,BuyVoucherActivity.class));
        } else if (id == R.id.complain) {
              startActivity(new Intent(Home.this, Complaint.class));
        } else if (id == R.id.about) {
              startActivity(new Intent(Home.this,aboutUs.class));
        }else if (id == R.id.logout){
          confirmDialog(getApplicationContext());
        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class getBalance extends AsyncTask<String, String, String> {
        User user = new User();


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
                accountNumber.setText(separated[0]);
                accountBalance.setText("N "+separated[5]);
                fullName.setText(separated[1] + " " + separated[2]);
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("accountNumber", separated[0]);
                edit.putString("fname", separated[1]);
                edit.putString("lname", separated[2]);
                edit.putString("balance", separated[5]);
                edit.putString("email", separated[3]);
                edit.putString("phone", separated[4]);

                if (separated[6].equalsIgnoreCase("1")){
                    edit.putString("userType", "user");
                    userType.setText("User");
                }else{
                    edit.putString("userType", "vendor");
                    userType.setText("Vendor");
                }

                edit.apply();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Bvn").child(separated[0]);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String bvnStat = dataSnapshot.child("bvn").getValue().toString();

                            if (bvnStat.equalsIgnoreCase("true")){
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("bvn","true");
                                editor.apply();
                            }else{
                               // Toast.makeText(Home.this, "Dont know what happened here", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                           // Toast.makeText(Home.this, "nothing here", Toast.LENGTH_SHORT).show();
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String re = "";

            String uname = strings[0];
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("straccountNo", uname);


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

                        user.setFname(jsonObject1.optString("fname"));
                        user.setLname(jsonObject1.optString("lname"));
                        user.setSaveaseID(jsonObject1.optString("saveaseID"));
                        user.setEmail(jsonObject1.optString("email"));
                        user.setPhone(jsonObject1.optString("phone"));
                        float bel = Float.parseFloat(jsonObject1.optString("balance"));
                        String  newBel = new DecimalFormat("##.##").format(bel);
                        user.setBalance(Double.parseDouble(newBel));
                        user.setAccountType(jsonObject1.optString("accountType"));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                re = user.getSaveaseID() + "," + user.getFname() + "," + user.getLname() + "," + user.getEmail() + "," + user.getPhone() + "," + String.valueOf(user.getBalance()+"0")+ "," + String.valueOf(user.getAccountType());

            }

//

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
            new getBalance().execute(username);

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
                        Intent intent = new Intent(Home.this, Login.class);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the Location permission", REQUEST_STORAGE_PERMISSION, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_STORAGE_PERMISSION)
    public void requestStoragePermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this, perms)) {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the storage permission", REQUEST_STORAGE_PERMISSION, perms);
        }
    }


}
