package saveaseng.ng.savease.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Adapter.AccountAdapter;
import saveaseng.ng.savease.Adapter.AllBanksAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.AccountModel;
import saveaseng.ng.savease.Model.BankListData;
import saveaseng.ng.savease.Model.BankListResponse;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiClient;
import saveaseng.ng.savease.Utils.ApiInterface;

public class BankActivity extends Home implements  AllBanksAdapter.OnItemClicked {
    private static final String METHOD_NAME = "getBankDetails";
    private static final String SOAP_ACTION = "http://savease.ng/getBankDetails";


    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final static String API_KEY = "Bearer sk_live_0dc08582961f9c1d0785245c36bc4a65658ce187";
    TextView userType;
    String accountType,username,accountNumbers,bankCode,bankName;
    SharedPreferences preferences;
    private DrawerLayout drawer;
    private TextView accntName, accntNumber, balance;
    Button addAccount,btnAddAccountBank;
    private LinearLayout bottom_sheet;
    Dialog dialog;
    BottomSheetBehavior sheetBehavior;
    ArrayList<AccountModel> trans = new ArrayList<>();
    private RecyclerView rvTRans;
    private AccountAdapter adapter;
    ApiInterface apiService;
    private AllBanksAdapter bankAdapter;
    ArrayList<BankListData> bankListData = new ArrayList<>();
    android.app.AlertDialog alertDialog;
    private EditText edtBankName,edtAccountNameBank,edtAccountNumberBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_nav);
        dialog = new Dialog(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        bottom_sheet = (LinearLayout)findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = preferences.getString("uname", "");
        accountNumbers = preferences.getString("accountNumber", "");
        accountType =preferences.getString("userType", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bank);
        setSupportActionBar(toolbar);

        accntName = (TextView) findViewById(R.id.txtAccountName);
        accntNumber = (TextView) findViewById(R.id.txtAccountNumber);
        balance = (TextView) findViewById(R.id.txtAccountBalance);
        userType = (TextView) findViewById(R.id.txtAccountType);

        edtBankName = (EditText)findViewById(R.id.edtBankNameBank);
        edtAccountNameBank = (EditText)findViewById(R.id.edtAccountNameBank);
        edtAccountNumberBank = (EditText)findViewById(R.id.edtAccountNumberBank);

        edtBankName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllBanks();
            }
        });

        rvTRans = (RecyclerView)findViewById(R.id.rvBankAccounts);
        rvTRans.setLayoutManager(new LinearLayoutManager(this));

        userType.setText(accountType);

        String fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        accntName.setText(fulln);
        balance.setText("N "+preferences.getString("balance", ""));
        accntNumber.setText(preferences.getString("accountNumber", ""));

        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankActivity.this,AccountOfficer.class));
            }
        });


        addAccount = (Button) findViewById(R.id.btnAddAccount);
        btnAddAccountBank = (Button) findViewById(R.id.btnAddAccountBank);

        btnAddAccountBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acctName = edtAccountNameBank.getText().toString().trim();
                String acctNumber = edtAccountNumberBank.getText().toString().trim();

                if (acctName.isEmpty()){
                    Toast.makeText(BankActivity.this, "Account name must not be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (acctNumber.isEmpty()){
                    Toast.makeText(BankActivity.this, "Account number must not be empty", Toast.LENGTH_LONG).show();
                    return;
                }



            }
        });

        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(BankActivity.this,AddBank.class));
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_bank);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_bank);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            new getBanks().execute(accountNumbers);

        } else {
            connected = false;
            pDialog.setTitleText("Error")
                    .setContentText("Seems you are not connected to the internet, please do so and try again").show();
        }

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{

            if (accountType.equalsIgnoreCase("user")){
                startActivity(new Intent(BankActivity.this,Home.class));

            }else {
                startActivity(new Intent(BankActivity.this,VendorHome.class));
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (accountType.equalsIgnoreCase("user")){
            if (id == R.id.homeBack) {
                startActivity(new Intent(BankActivity.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(BankActivity.this,VendorHome.class));
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
            startActivity(new Intent(BankActivity.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(BankActivity.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(BankActivity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(BankActivity.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(BankActivity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(BankActivity.this,aboutUs.class));
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
                        Intent intent = new Intent(BankActivity.this, Login.class);
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

    private class getBanks extends AsyncTask<String,String, ArrayList<AccountModel>> {
        //   SweetAlertDialog pDialog = new SweetAlertDialog(VoucherTeable.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<AccountModel> transactionModels) {
            super.onPostExecute(transactionModels);
            trans = transactionModels;
            adapter = new AccountAdapter(transactionModels);
            rvTRans.setAdapter(adapter);
        }

        @Override
        protected ArrayList<AccountModel> doInBackground(String... strings) {
            ArrayList<AccountModel> transactionModels = new ArrayList<>();

            String uname =  strings[0];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("saveaseid",uname);



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

            if (envelope.bodyIn != null) {
                SoapObject response = (SoapObject) envelope.bodyIn;

                String re = response.getProperty(0).toString();


                if (re != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(re);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            AccountModel model = new AccountModel();
                            model.setAccountName(jsonObject1.optString("AccountName"));
                            model.setAccountNumber(jsonObject1.optString("AccountNumber"));
                            model.setBankName(jsonObject1.optString("BankName"));
                            model.setBankcode(jsonObject1.optString("bankCode"));
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



    @Override
    protected void onResume() {
        super.onResume();
        new getBanks().execute(accountNumbers);
    }

    private void showAllBanks() {
        pDialog = new SweetAlertDialog(BankActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Fetching all banks..please wait");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<BankListResponse> call = apiService.getAllBanks(API_KEY);

        call.enqueue(new Callback<BankListResponse>() {
            @Override
            public void onResponse(Call<BankListResponse> call, Response<BankListResponse> response) {
                if (response.body() != null){

                    bankListData = new ArrayList<>(response.body().getData());
                    Rect displayRectangle = new Rect();
                    Window window = getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BankActivity.this,R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_all_banks, viewGroup, false);
                    dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
                    dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();
                    bankAdapter = new AllBanksAdapter(bankListData,BankActivity.this);
                    RecyclerView bankRv = dialogView.findViewById(R.id.rvAllBanks);
                    bankRv.setLayoutManager(new LinearLayoutManager(BankActivity.this));
                    bankRv.setAdapter(bankAdapter);







                }else{
                    pDialog.dismiss();
                    Toast.makeText(BankActivity.this, "An issue occurred while fetching the list of banks, please try again later ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<BankListResponse> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(BankActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });




    }

    @Override
    public void userItemClick(int pos) {
        BankListData data = bankListData.get(pos);
        edtBankName.setText(data.getName());
        bankCode = data.getCode();
        bankName = data.getName();
        Toast.makeText(BankActivity.this, data.getName() + " " + data.getCode(), Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
        pDialog.dismiss();

    }
}
