package saveaseng.ng.savease.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
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
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Adapter.AllBanksAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.AccountData;
import saveaseng.ng.savease.Model.AccountNameResponse;
import saveaseng.ng.savease.Model.BankListData;
import saveaseng.ng.savease.Model.BankListResponse;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiClient;
import saveaseng.ng.savease.Utils.ApiInterface;

public class AddBank extends Home implements AllBanksAdapter.OnItemClicked {
    private Button btnContinue;
    ApiInterface apiService;
    private String bankCode,amount,narration,transRef,acctNum,pincode,acctName,bankName,accountType,ti;
    private AllBanksAdapter adapter;
    ArrayList<BankListData> bankListData = new ArrayList<>();
    android.app.AlertDialog alertDialog;
    SweetAlertDialog pDialog;
    private DrawerLayout drawer;
    private EditText edtAmountFunded;
    private MaterialSpinner spinnerTranferBankType;
    TextView fullNameT, acctNumber, balanceT,userType;
    private String accountNumber, accountName, email, phone;
    SharedPreferences preferences;
    Dialog dialog;
    private EditText edtBankName,edtAccountNumber,edtAccountName,edtAmount,edtNarration;
    private final static String API_KEY = "Bearer sk_live_0dc08582961f9c1d0785245c36bc4a65658ce187";
    private static final String METHOD_NAME2 = "InsertBankDetails";
    private static final String SOAP_ACTION2 = "http://savease.ng/InsertBankDetails";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbank_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountName = preferences.getString("uname", "");
        accountNumber = preferences.getString("accountNumber","");
        email = preferences.getString("email", "");
        phone = preferences.getString("phone", "");
        accountType =preferences.getString("userType", "");

        fullNameT = (TextView) findViewById(R.id.txtAccountName);
        acctNumber = (TextView) findViewById(R.id.txtAccountNumber);
        balanceT = (TextView) findViewById(R.id.txtAccountBalance);
        userType = (TextView) findViewById(R.id.txtAccountType);
        userType.setText(accountType);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        edtAccountName = (EditText)findViewById(R.id.edtAccountName);
        edtBankName = (EditText)findViewById(R.id.edtBankName);
        edtAccountNumber = (EditText)findViewById(R.id.edtAccountNumber);

        btnContinue = (Button)findViewById(R.id.btnAddAcc);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new updateAccount().execute(acctName,acctNum,bankName,accountNumber,bankCode);

            }
        });

        edtAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                acctNum = s.toString();
                getAcountName(acctNum);

            }
        });

        edtBankName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllBanks();
            }
        });


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_addbank);
        setSupportActionBar(toolbar);

        String fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        balanceT.setText("N "+preferences.getString("balance", ""));
        acctNumber.setText(preferences.getString("accountNumber", ""));
        fullNameT.setText(fulln);





        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_addbank);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_addbank);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private class updateAccount extends AsyncTask<String, String, String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(AddBank.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Adding Details ,please wait....");
            this.pDialog.setCancelable(true);
            this.pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.pDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {


                new FancyAlertDialog.Builder(AddBank.this)
                        .setTitle("Success")
                        .setBackgroundColor(Color.parseColor("#212435"))
                        .setMessage("Successfully added account details")
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
                                startActivity(new Intent(AddBank.this,BankActivity.class));
                                finish();



                            }
                        })
                        .build();
            } else {

                new FancyAlertDialog.Builder(AddBank.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Adding account details failed")
                        .setPositiveBtnText("Done")
                        .setPositiveBtnBackground(Color.parseColor("#DF5C4E"))
                        .setAnimation(Animation.SLIDE)
                        .setNegativeBtnBackground(Color.parseColor("#ffffff"))
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_cancel, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                dialog.dismiss();

                            }
                        })
                        .build();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String name = strings[0];
            String number = strings[1];
            String bank = strings[2];
            String id = strings[3];
            String code = strings[4];



            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("accountname", name);
            request.addProperty("accountno", number);
            request.addProperty("bankname", bank);
            request.addProperty("saveaseid", id);
            request.addProperty("bankcode", code);


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


    private void getAcountName(String acctNum) {

        if (acctNum.length() < 10){

        }else {

            pDialog = new SweetAlertDialog(AddBank.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Verifying Account number..please wait");
            pDialog.setCancelable(false);
            pDialog.show();

            if (bankCode.isEmpty() || bankCode == null) {
                pDialog.dismiss();
                Toast.makeText(this, "Please first select a bank and then verify the account number", Toast.LENGTH_SHORT).show();
                return;
            } else {

                Call<AccountNameResponse> call = apiService.getAccountName(acctNum, bankCode, API_KEY);
                call.enqueue(new Callback<AccountNameResponse>() {
                    @Override
                    public void onResponse(Call<AccountNameResponse> call, Response<AccountNameResponse> response) {
                        if (response != null) {
                            pDialog.dismiss();
                            AccountData accountData = response.body().getData();
                            edtAccountName.setText(accountData.getAccountName());
                            acctName = accountData.getAccountName();



                        } else {
                            Toast.makeText(AddBank.this, "There was an issue resolving the account number, please try again", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountNameResponse> call, Throwable t) {
                        Toast.makeText(AddBank.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();

                    }
                });
            }

        }
    }

    private void showAllBanks() {
        pDialog = new SweetAlertDialog(AddBank.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Fetching all banks..please wait");
        pDialog.setCancelable(true);
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
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddBank.this,R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_all_banks, viewGroup, false);
                    dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
                    dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();
                    adapter = new AllBanksAdapter(bankListData,AddBank.this);
                    RecyclerView bankRv = dialogView.findViewById(R.id.rvAllBanks);
                    bankRv.setLayoutManager(new LinearLayoutManager(AddBank.this));
                    bankRv.setAdapter(adapter);







                }else{
                    pDialog.dismiss();
                    Toast.makeText(AddBank.this, "An issue occurred while fetching the list of banks, please try again later ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<BankListResponse> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(AddBank.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });




    }


    @Override
    public void userItemClick(int pos) {
        BankListData data = bankListData.get(pos);
        edtBankName.setText(data.getName());
        bankCode = data.getCode();
        bankName = data.getName();
        Toast.makeText(AddBank.this, data.getName() + " " + data.getCode(), Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
        pDialog.dismiss();

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
                startActivity(new Intent(AddBank.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(AddBank.this,VendorHome.class));
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
            startActivity(new Intent(AddBank.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(AddBank.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(AddBank.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(AddBank.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(AddBank.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(AddBank.this, aboutUs.class));
        } else if (id == R.id.logout) {
            confirmDialog(getApplicationContext());
        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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
                        Intent intent = new Intent(AddBank.this, Login.class);
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
