package saveaseng.ng.savease.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Adapter.VoucherAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.VoucherTableModel;
import saveaseng.ng.savease.R;

public class VoucherTeable extends Home implements VoucherAdapter.OnItemClicked{
    EditText startDate, endDate;
    private String acctType;
    private MaterialSpinner spinnerAccountType;
    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    LinearLayout linearLayout;
    SharedPreferences preferences;
    int mYear, mMonth, mDay, mHour, mMinute;
    File file;
    Dialog dialog;
    private static final String METHOD_NAME = "displayTblDaterange";
    private static final String SOAP_ACTION = "http://savease.ng/displayTblDaterange";

    private static final String METHOD_NAME2 = "displayTblUnused";
    private static final String SOAP_ACTION2 = "http://savease.ng/displayTblUnused";

    private static final String METHOD_NAME3 = "displayTblUsed";
    private static final String SOAP_ACTION3 = "http://savease.ng/displayTblUsed";


    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    Button getVoucher;
    boolean connected = false;
    String username,start,end,accountType;
    private DrawerLayout drawer;
    private VoucherAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private TextView accntName, accntNumber, balance,userType;
    String fulln,acctNUm;
    ArrayList<VoucherTableModel> trans = new ArrayList<>();
    private RecyclerView rvTRans;
    private static final String[] voucher = {
            "Select Criteria",
            "Used Voucher",
            "Unused Voucher",
            "Date range"


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_table_nav);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = preferences.getString("uname", "");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_voucher_table);
        setSupportActionBar(toolbar);

        accntName = (TextView) findViewById(R.id.txtAccountName);
        accntNumber = (TextView) findViewById(R.id.txtAccountNumber);
        userType = (TextView) findViewById(R.id.txtAccountType);
        balance = (TextView) findViewById(R.id.txtAccountBalance);
        acctNUm = preferences.getString("accountNumber", "");
        accountType =preferences.getString("userType", "");

        fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        accntName.setText(fulln);
        userType.setText(accountType);
        balance.setText("N "+preferences.getString("balance", ""));
        accntNumber.setText(preferences.getString("accountNumber", ""));

        startDate = findViewById(R.id.startdate);
        endDate = findViewById(R.id.enddate);
        linearLayout = findViewById(R.id.dateLayout);
        rvTRans = (RecyclerView)findViewById(R.id.rvVoucherTable) ;
        mLayoutManager = new LinearLayoutManager(this);
        rvTRans.setLayoutManager(mLayoutManager);
        rvTRans.setHasFixedSize(true);

        final SweetAlertDialog pDialog = new SweetAlertDialog(VoucherTeable.this, SweetAlertDialog.ERROR_TYPE);
        getVoucher = findViewById(R.id.btnGetVoucher);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datete();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datet();
            }
        });
        getVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    new getVoucherTableCards().execute(username,start,end);
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
                startActivity(new Intent(VoucherTeable.this,AccountOfficer.class));
            }
        });


        spinnerAccountType = (MaterialSpinner) findViewById(R.id.spinVouchers);
        spinnerAccountType.setItems(voucher);
        spinnerAccountType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                acctType = item;

                if (acctType.equalsIgnoreCase("Select Criteria")) {
                    linearLayout.setVisibility(View.GONE);

                } else if (acctType.equalsIgnoreCase("Used Voucher")) {
                    linearLayout.setVisibility(View.GONE);
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;

                        new getVoucherDate().execute(username,METHOD_NAME3,SOAP_ACTION3);
                    }
                    else {
                        connected = false;
                        pDialog.setTitleText("Error")
                                .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                    }

                } else if (acctType.equalsIgnoreCase("Unused Voucher")) {
                    linearLayout.setVisibility(View.GONE);
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                        new getVoucherDate().execute(username,METHOD_NAME2,SOAP_ACTION2);
                    }
                    else {
                        connected = false;
                        pDialog.setTitleText("Error")
                                .setContentText("Seems you are not connected to the internet, please do so and try again").show();
                    }
                } else if (acctType.equalsIgnoreCase("Date Range")) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        spinnerAccountType.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_voucherTable);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_voucherTable);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (accountType.equalsIgnoreCase("user")){
                startActivity(new Intent(VoucherTeable.this,Home.class));

            }else {
                startActivity(new Intent(VoucherTeable.this,VendorHome.class));
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
                startActivity(new Intent(VoucherTeable.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(VoucherTeable.this,VendorHome.class));
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
            startActivity(new Intent(VoucherTeable.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(VoucherTeable.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(VoucherTeable.this, VerifyHome.class));
        }  else if (id == R.id.userGuide) {
            startActivity(new Intent(VoucherTeable.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(VoucherTeable.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(VoucherTeable.this,aboutUs.class));
        }else if (id == R.id.logout){
           confirmDialog(getApplicationContext());
        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void datete() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {



                        String len = String.valueOf(monthOfYear);
                        String da = String.valueOf(dayOfMonth);
                        if (len.length() <2){
                            if (da.length()<2) {
                                start = "0" +dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                                startDate.setText(start);
                            }else {
                                start = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                                startDate.setText(start);
                            }
                        }else {
                            if (da.length()<2){
                                start = "0" +dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                startDate.setText(start);
                            }else {
                                start = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                startDate.setText(start);
                            }

                        }


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    public void datet() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {



                        String len = String.valueOf(monthOfYear);
                        String da = String.valueOf(dayOfMonth);
                        if (len.length() <2){
                            if (da.length()<2) {
                                end = "0" +dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                                endDate.setText(end);
                            }else {
                                end = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                                endDate.setText(end);
                            }
                        }else {
                            if (da.length()<2){
                                end = "0" +dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                endDate.setText(start);
                            }else {
                                end = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                endDate.setText(start);
                            }
                        }


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    public void userItemClick(int pos) {
        VoucherTableModel mod = trans.get(pos);
        final PowerMenu powerMenu = new PowerMenu.Builder(VoucherTeable.this)
                .addItem(new PowerMenuItem("Share", false))
                .addItem(new PowerMenuItem("Print", false))
                .setAnimation(MenuAnimation.SHOW_UP_CENTER) // Animation start point (TOP | LEFT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setTextColor(getResources().getColor(R.color.colorAccent))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        Toast.makeText(VoucherTeable.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (item.getTitle().equalsIgnoreCase("Share")){
                            showAnotherPopUp(mod);
                        }else{
                            showPrintDialog(mod);
                        }
                    }
                })
                .setSelectedMenuColor(getResources().getColor(R.color.darkgold))
                .build();
        powerMenu.showAsDropDown(spinnerAccountType);


    }

    private class getVoucherTableCards extends AsyncTask<String,String, ArrayList<VoucherTableModel>> {
    //    SweetAlertDialog pDialog = new SweetAlertDialog(VoucherTeable.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<VoucherTableModel> transactionModels) {
            super.onPostExecute(transactionModels);
            trans = transactionModels;
            linearLayout.setVisibility(View.GONE);
            adapter = new VoucherAdapter(transactionModels,VoucherTeable.this);
            rvTRans.setAdapter(adapter);
        }

        @Override
        protected ArrayList<VoucherTableModel> doInBackground(String... strings) {
            ArrayList<VoucherTableModel> transactionModels = new ArrayList<>();

            String uname =  strings[0];
            String startDate = strings[1];
            String endDate = strings[2];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("uname",uname);
            request.addProperty("startDate",startDate);
            request.addProperty("endDate",endDate);



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

            if (envelope.bodyIn.toString().contains("SoapFault")){
                transactionModels = new ArrayList<>();

            }else {
                if (envelope.bodyIn != null) {
                    SoapObject response = (SoapObject) envelope.bodyIn;

                    String re = response.getProperty(0).toString();


                    if (re != null) {
                        try {
                            JSONArray jsonArray = new JSONArray(re);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                VoucherTableModel model = new VoucherTableModel();
                                model.setAmount(jsonObject1.optString("Amount"));
                                model.setVoucherStatus(jsonObject1.optString("VoucherStatus"));
                                model.setVoucherPin(jsonObject1.optString("VoucherPin"));
                                model.setBatchNo(jsonObject1.optString("BatchNo"));
                                model.setSerialNumber(jsonObject1.optString("SerialNumber"));
                                model.setUsedBy(jsonObject1.optString("UsedBy"));
                                model.setUsedDate(jsonObject1.optString("UsedDate"));
                                transactionModels.add(model);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        transactionModels = new ArrayList<>();
                    }

                }else {
                    transactionModels = new ArrayList<>();
                }



            }



            return transactionModels;
        }
    }


    private class getVoucherDate extends AsyncTask<String,String, ArrayList<VoucherTableModel>> {
     //   SweetAlertDialog pDialog = new SweetAlertDialog(VoucherTeable.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<VoucherTableModel> transactionModels) {
            super.onPostExecute(transactionModels);
            trans = transactionModels;
            adapter = new VoucherAdapter(transactionModels,VoucherTeable.this);
            rvTRans.setAdapter(adapter);
        }

        @Override
        protected ArrayList<VoucherTableModel> doInBackground(String... strings) {
            ArrayList<VoucherTableModel> transactionModels = new ArrayList<>();

            String uname =  strings[0];
            String methodname =  strings[1];
            String soapaction =  strings[2];

            SoapObject request = new SoapObject(NAMESPACE, methodname);
            request.addProperty("uname",uname);



            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(soapaction, envelope);
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

            if (envelope.bodyIn != null) {
                SoapObject response = (SoapObject) envelope.bodyIn;

                String re = response.getProperty(0).toString();


                if (re != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(re);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            VoucherTableModel model = new VoucherTableModel();
                            model.setAmount(jsonObject1.optString("Amount"));
                            model.setVoucherStatus(jsonObject1.optString("VoucherStatus"));
                            model.setVoucherPin(jsonObject1.optString("VoucherPin"));
                            model.setBatchNo(jsonObject1.optString("BatchNo"));
                            model.setSerialNumber(jsonObject1.optString("SerialNumber"));
                            model.setUsedBy(jsonObject1.optString("UsedBy"));
                            model.setUsedDate(jsonObject1.optString("UsedDate"));
                            transactionModels.add(model);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    transactionModels = new ArrayList<>();
                }

            }else {
                transactionModels = new ArrayList<>();
            }


            return transactionModels;
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
                        Intent intent = new Intent(VoucherTeable.this, Login.class);
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



    protected void printDemo(VoucherTableModel tableModel) {
        if(btsocket == null){
            Toast.makeText(VoucherTeable.this, "Please select a bluetooth printer", Toast.LENGTH_SHORT).show();
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else{
            Toast.makeText(VoucherTeable.this, "Printing Voucher... please wait", Toast.LENGTH_SHORT).show();
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();
                byte[] printformat = new byte[]{0x1B,0x21,0x03};
                outputStream.write(printformat);

                //outputStream.write(printformat);

               printPhoto(R.drawable.logowhite);

                printCustom("Deposit Voucher",1,0);
                printNewLine();
                String dateTime[] = getDateTime();
                printCustom("Date : "+ dateTime[0],0,0);
                printNewLine();

                printCustom("N"+tableModel.getAmount(),3,1);
                printNewLine();
                printCustom("Serial Number : "+ tableModel.getSerialNumber(),1,0);
                printNewLine();

                String newString = tableModel.getVoucherPin().substring(0, 4) + "  -  " + tableModel.getVoucherPin().substring(3+1,8)+ "  -  "+tableModel.getVoucherPin().substring(7+1) ;
                printCustom("PIN :  "+newString,3,1);
                printNewLine();
                String exp[] = getDateTimeExpiry();
                printCustom("Expiry Date : "+ exp[0]+" "+exp[1],0,0);
                printNewLine();
                printCustom("Merchant Name : "+ fulln ,0,0);
                printNewLine();
                printCustom("Terminal ID : "+ acctNUm ,0,0);
                printNewLine();
                printNewLine();
                printCustom("Thank you for using Africa's ",1,1);
                printCustom("deposit gateway",1,1);
                printNewLine();
                printNewLine();

                printCustom("www.savease.ng",1,1);
                printCustom("info@savease.ng",1,1);
                printCustom("09090692222, 08027226627",1,1);

                printNewLine();
                printNewLine();
                printNewLine();
                outputStream.flush();

                dialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x15}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;

                case 4:
                    outputStream.write(bb4);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //print photo
    public void printPhoto(int img) {

            try {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                        img);
                if(bmp!=null){
                    byte[] command = Utils.decodeBitmap(bmp);
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText(command);
                }else{
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }



    }
    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

     //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String leftRightAlign(String str1, String str2) {
        String ans = str1 +str2;
        if(ans.length() <31){
            int n = (31 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[1];

        String pattern = "dd-M-yyyy hh:mm:ss a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        dateTime[0] = date;

        return dateTime;
    }

    private String[] getDateTimeExpiry() {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = DeviceList.getSocket();
            if(btsocket != null){
                printText("printing the text");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAnotherPopUp(final VoucherTableModel tableModel) {
        Dialog dialog = new Dialog(VoucherTeable.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.card_layout_share);
        TextView voucherPin = (TextView)dialog.findViewById(R.id.txtVoucherPinShare);
        TextView voucherAmount = (TextView)dialog.findViewById(R.id.txtVoucherAmountShare);
        TextView voucherSerial = (TextView)dialog.findViewById(R.id.txtVoucherPinSerialNumber);
        TextView voucherDateCreated = (TextView)dialog.findViewById(R.id.txtVoucherDatePrinted);
        TextView voucherDateExpire = (TextView)dialog.findViewById(R.id.txtVoucherDateExpiry);
        TextView voucherMerchant = (TextView)dialog.findViewById(R.id.txtVoucherMerchant);
        TextView voucherTerminal = (TextView)dialog.findViewById(R.id.txtVoucherTerminalID);
        final LinearLayout linCard = (LinearLayout)dialog.findViewById(R.id.linCard);
        Button confirm = dialog.findViewById(R.id.btnShare);

        String dateTime[] = getDateTime();
        voucherDateCreated.setText("Date :" +dateTime[0]);
        voucherMerchant.setText("Merchant Name : "+ fulln);
        voucherTerminal.setText("Terminal ID : "+acctNUm);
        String exp[] = getDateTimeExpiry();
        voucherDateExpire.setText("Expiry Date : " +exp[0]+ " "+ exp[1]);
        voucherAmount.setText("N"+tableModel.getAmount());
        voucherPin.setText("PIN : "+tableModel.getVoucherPin());
        voucherSerial.setText("Serial Number : "+tableModel.getSerialNumber());

        dialog.show();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = saveBitMap(VoucherTeable.this, linCard);    //which view you want to pass that view as parameter
                if (file != null) {
                    //Log.i("TAG", "Drawing saved to the gallery!");
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Toast.makeText(VoucherTeable.this, "Drawing saved to the gallery!", Toast.LENGTH_SHORT).show();
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Voucher Purchase...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Pin : "+tableModel.getVoucherPin());

                    startActivity(Intent.createChooser(intentShareFile, "Voucher Card"));
                } else {
                    Toast.makeText(VoucherTeable.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                //  Log.i("ATG", "Can't create directory to save the image");
                return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(final Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Toast.makeText(cntx, path, Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPrintDialog(VoucherTableModel tableModel) {

        dialog = new Dialog(VoucherTeable.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.card_layout);
        TextView voucherPin = (TextView)dialog.findViewById(R.id.txtVoucherPinShare);
        TextView voucherAmount = (TextView)dialog.findViewById(R.id.txtVoucherAmountShare);
        TextView voucherSerial = (TextView)dialog.findViewById(R.id.txtVoucherPinSerialNumber);
        TextView voucherDateCreated = (TextView)dialog.findViewById(R.id.txtVoucherDatePrinted);
        TextView voucherDateExpire = (TextView)dialog.findViewById(R.id.txtVoucherDateExpiry);
        TextView voucherMerchant = (TextView)dialog.findViewById(R.id.txtVoucherMerchant);
        TextView voucherTerminal = (TextView)dialog.findViewById(R.id.txtVoucherTerminalID);
        final LinearLayout linCard = (LinearLayout)dialog.findViewById(R.id.linCard);
        Button confirm = dialog.findViewById(R.id.btnShare);
        confirm.setText("Print");
        String dateTime[] = getDateTime();
        voucherDateCreated.setText("Date :" +dateTime[0]);
        voucherMerchant.setText("Merchant Name : "+ fulln);
        voucherTerminal.setText("Terminal ID : "+acctNUm);
        String exp[] = getDateTimeExpiry();
        voucherDateExpire.setText("Expiry Date : " +exp[0]+ " "+ exp[1]);
        voucherAmount.setText("N"+tableModel.getAmount());
        voucherPin.setText("PIN : "+tableModel.getVoucherPin());
        voucherSerial.setText("Serial Number : "+tableModel.getSerialNumber());


        dialog.show();
        confirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                file = saveBitMap(VoucherTeable.this, linCard);    //which view you want to pass that view as parameter
                if (file != null) {
                    printDemo(tableModel);

                }
            }
        });


    }

}

