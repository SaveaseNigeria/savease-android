package saveaseng.ng.savease.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Adapter.AcAdapter;
import saveaseng.ng.savease.Adapter.AllBanksAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.AccountData;
import saveaseng.ng.savease.Model.AccountModel;
import saveaseng.ng.savease.Model.AccountNameResponse;
import saveaseng.ng.savease.Model.BankListData;
import saveaseng.ng.savease.Model.BankListResponse;
import saveaseng.ng.savease.Model.BankTransfer;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.Model.TransferCreationData;
import saveaseng.ng.savease.Model.TransferCreationResponse;
import saveaseng.ng.savease.Model.TransferData;
import saveaseng.ng.savease.Model.TransferRep;
import saveaseng.ng.savease.Model.TransferResponse;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiCli;
import saveaseng.ng.savease.Utils.ApiClient;
import saveaseng.ng.savease.Utils.ApiInterface;

public class WithdrawActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AllBanksAdapter.OnItemClicked {
    private DrawerLayout drawer;
    private EditText edtAmountFunded;
    private MaterialSpinner spinnerTranferBankType;
    TextView fullNameT, acctNumber, balanceT,userType;
    private String accountNumber, accountName, email, phone;
    SharedPreferences preferences;
    Dialog dialog;
    private EditText edtBankName,edtAccountNumber,edtAccountName,edtAmount,edtNarration;
    private final static String API_KEY = "Bearer sk_live_0dc08582961f9c1d0785245c36bc4a65658ce187";
    private Button btnContinue;
    ApiInterface apiService;
    private String bankCode,amount,narration,transRef,acctNum,pincode,acctName,bankName,accountType;
    private String ti = "Select Account";
    private AllBanksAdapter adapter;
    ArrayList<BankListData> bankListData = new ArrayList<>();
    android.app.AlertDialog alertDialog;
    SweetAlertDialog pDialog;
    AcAdapter acctAdapter;
    private Spinner spinnerAccount;
    private static final String[] transferBankType = {
            "Select Account",
            "Own account",
            "Third party account"
    };
    ArrayList<AccountModel> trans = new ArrayList<>();
    private static final String METHOD_NAME3 = "existTransPIN";
    private static final String SOAP_ACTION3 = "http://savease.ng/existTransPIN";
    private static final String METHOD_NAME5 = "updateBalance";
    private static final String SOAP_ACTION5 = "http://savease.ng/updateBalance";
    private static final String METHOD_NAME = "getBankDetails";
    private static final String SOAP_ACTION = "http://savease.ng/getBankDetails";
    boolean connected = false;
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final static String SMS_API_KEY = "9Pc1XtdCYg43wdJ6AlbCSCyTlLqc2voEFpl9DvmUq0zcKJTDbdE4aOYOPtzz";
    private LinearLayout linThirdParty, linOwn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_nav);

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
        linThirdParty = (LinearLayout) findViewById(R.id.linThirdParty);
        linOwn = (LinearLayout) findViewById(R.id.linOwn);
        userType.setText(accountType);
        Pinview pinview1 = findViewById(R.id.pinview4);
        pinview1.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                pincode = pinview.getValue();

            }
        });

        edtAmountFunded = (EditText) findViewById(R.id.edtAmountFunded);

        Button accontOfficer = (Button) findViewById(R.id.btnAccountOfficer);
        accontOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawActivity.this, AccountOfficer.class));
            }
        });

        spinnerTranferBankType = (MaterialSpinner)findViewById(R.id.spinTransferBankTypeWithdraw);
        spinnerTranferBankType.setItems(transferBankType);
        spinnerTranferBankType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {


                if (item.equalsIgnoreCase("Own account")){
                    ti = item;
                    linOwn.setVisibility(View.VISIBLE);
                    linThirdParty.setVisibility(View.GONE);

                }else if (item.equalsIgnoreCase("Third party account")){
                    ti = item;
                    linOwn.setVisibility(View.GONE);
                    linThirdParty.setVisibility(View.VISIBLE);

                }else if (item.equalsIgnoreCase("Select Account")){
                    ti = item;
                    linOwn.setVisibility(View.GONE);
                    linThirdParty.setVisibility(View.GONE);

                }
            }
        });
        spinnerTranferBankType.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });

        apiService = ApiClient.getClient().create(ApiInterface.class);
        edtAccountName = (EditText)findViewById(R.id.edtAccountName);
        edtBankName = (EditText)findViewById(R.id.edtBankName);
        edtAccountNumber = (EditText)findViewById(R.id.edtAccountNumber);
        edtAmount = (EditText)findViewById(R.id.edtAmount);
        edtNarration = (EditText)findViewById(R.id.edtNarration);
        btnContinue = (Button)findViewById(R.id.btnContinueTransfer);
        spinnerAccount = (Spinner) findViewById(R.id.spinAccounts);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ti.equalsIgnoreCase("Select Account")){
                    Toast.makeText(WithdrawActivity.this, "Please select a transfer type to withdraw funds", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    verifyPinAndCreateTransferRef();
                }

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

        spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AccountModel cardModels = (AccountModel) parent.getItemAtPosition(position);

                acctName = cardModels.getAccountName();
                acctNum = cardModels.getAccountNumber();
                bankName = cardModels.getBankName();
                bankCode  = cardModels.getBankcode();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_withdraw);
        setSupportActionBar(toolbar);

        String fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        balanceT.setText("N "+preferences.getString("balance", ""));
        acctNumber.setText(preferences.getString("accountNumber", ""));
        fullNameT.setText(fulln);





        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_withdraw);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_withdraw);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            new getBanks().execute(accountNumber);

        } else {
            connected = false;
            pDialog.setTitleText("Error")
                    .setContentText("Seems you are not connected to the internet, please do so and try again").show();
        }



    }

    private void verifyPinAndCreateTransferRef() {
        narration = edtNarration.getText().toString().trim();
        String amt = edtAmount.getText().toString().trim();
        int ab = Integer.valueOf(amt) * 100;
        amount = String.valueOf(ab);

        double a = Double.parseDouble("1200");
        double b = Double.parseDouble(preferences.getString("balance", ""));
        double ba = Double.parseDouble(amt);


        if (b<a){
            Toast.makeText(WithdrawActivity.this, "Oops, insufficient balance to transfer, you can only transfer from N1000 above and a charge of N100 is taken from each transfer  ", Toast.LENGTH_LONG).show();
            finish();
            startActivity(getIntent());
        }else{
            if (b<ba){
                Toast.makeText(WithdrawActivity.this, "Oops, insufficient balance to transfer, your balance is not up to the amount you want to withdraw ", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }else{
               // new verifyPinCode().execute(accountName, pincode);

                if (ba<a){
                    Toast.makeText(WithdrawActivity.this, "Oops, you can't withdraw less than a N1000 and a charge of N100 is required", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(getIntent());

                }else {
                    new verifyPinCode().execute(accountName, pincode);
                }
            }
        }
    }

    private class verifyPinCode extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(WithdrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Verifying,please wait....");
            this.pDialog.setCancelable(true);
            this.pDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == "null"){
                pDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "Your pin code is not correct and does not match the one you created, please enter a valid ", Toast.LENGTH_LONG).show();

            }else {
                if (s.equalsIgnoreCase("1"))
                {
                    pDialog.dismiss();
                    createTransactionRef();

                }else {
                    pDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, "Your pin code is not correct and does not match the one you created, please enter a valid ", Toast.LENGTH_LONG).show();

                }


            }


        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];
            String pin = strings[1];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);

            request.addProperty("in_username",amt);
            request.addProperty("transPIN",pin);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION3, envelope);
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
            return String.valueOf(result) ;

        }
    }

    private void createTransactionRef() {
        TransferRep rep = new TransferRep();
        rep.setType("nuban");
        rep.setAccount_number(acctNum);
        rep.setBank_code(bankCode);
        rep.setCurrency("NGN");
        rep.setDescription("Funds Transfer");
        rep.setName(acctName);

        pDialog = new SweetAlertDialog(WithdrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Verifying..please wait");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<TransferCreationResponse> call = apiService.createTransferRep(rep,API_KEY);

        call.enqueue(new Callback<TransferCreationResponse>() {
            @Override
            public void onResponse(Call<TransferCreationResponse> call, Response<TransferCreationResponse> response) {
                if (response.body() != null){

                    final TransferCreationData data = response.body().getData();
                    pDialog.dismiss();
                    Rect displayRectangle = new Rect();
                    Window window = getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WithdrawActivity.this,R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_complete_transfer, viewGroup, false);
                    dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
                    dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();

                    TextView saveaseAccountNuber = (TextView)dialogView.findViewById(R.id.txtSaveaseAccountNumber);
                    TextView saveaseAccountBalance = (TextView)dialogView.findViewById(R.id.txtBalance);
                    TextView bankNam = (TextView)dialogView.findViewById(R.id.txtBanNameTrans);
                    TextView bankAcctName = (TextView)dialogView.findViewById(R.id.txtBankAccountName);
                    TextView txtBankAccountNumber = (TextView)dialogView.findViewById(R.id.txtBankAccountNumber);
                    final TextView txtAmount = (TextView)dialogView.findViewById(R.id.txtAmount);

                    saveaseAccountNuber.setText(accountNumber);
                    saveaseAccountBalance.setText("N"+preferences.getString("balance", ""));
                    bankNam.setText(bankName);
                    bankAcctName.setText(acctName);
                    txtBankAccountNumber.setText(acctNum);

                    int a = Integer.valueOf(amount);
                    final int b = a /100;

                    final int c = b + 100;
                    txtAmount.setText("N"+ String.valueOf(b));

                    Button btnTransfer = (Button)dialogView.findViewById(R.id.btnTransfer);

                    btnTransfer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BankTransfer transfer = new BankTransfer();
                            transfer.setAmount(amount);
                            transfer.setReason(narration);
                            transfer.setRecipient(data.getRecipientCode());
                            transfer.setSource("balance");

                            pDialog = new SweetAlertDialog(WithdrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Transferring..please wait");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            Call<TransferResponse> call = apiService.transferFund(transfer,API_KEY);
                            call.enqueue(new Callback<TransferResponse>() {
                                @Override
                                public void onResponse(Call<TransferResponse> call, Response<TransferResponse> response) {

                                    if (response.body() != null){
                                        pDialog.dismiss();

                                        TransferData transferData = response.body().getData();
                                        new updateBalance().execute(accountName,String.valueOf(c));
                                        sendMessage("");
                                        alertDialog.dismiss();
                                        final Dialog dialog = new Dialog(WithdrawActivity.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.success_transfer);
                                        TextView message = dialog.findViewById(R.id.txtMessage);
                                        TextView txtAmt = dialog.findViewById(R.id.txtRecieptAmount);

                                        TextView txtTransCode = dialog.findViewById(R.id.txtRecieptTransferCode);
                                        final LinearLayout linReciept = dialog.findViewById(R.id.linReciept);
                                        message.setText("Your transfer was successful and the recipient wallet has been credited. Thank you for using Savease");
                                        txtAmt.setText("N"+ String.valueOf(b));
                                        txtTransCode.setText(transferData.getTransferCode());

                                        Button discard = dialog.findViewById(R.id.btnDismissReciept);
                                        discard.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                if (accountType.equalsIgnoreCase("user")){
                                                    startActivity(new Intent(WithdrawActivity.this, Home.class));
                                                }else {

                                                    startActivity(new Intent(WithdrawActivity.this, VendorHome.class));
                                                }

                                            }
                                        });


                                        Button save = dialog.findViewById(R.id.btnSaveReciept);
                                        save.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                File file = saveBitMap(WithdrawActivity.this, linReciept);    //which view you want to pass that view as parameter
                                                if (file != null) {
                                                    //Log.i("TAG", "Drawing saved to the gallery!");
                                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                                    StrictMode.setVmPolicy(builder.build());
                                                    Toast.makeText(WithdrawActivity.this, "Drawing saved to the gallery!", Toast.LENGTH_SHORT).show();
                                                    if (accountType.equalsIgnoreCase("user")){
                                                        startActivity(new Intent(WithdrawActivity.this, Home.class));
                                                    }else {

                                                        startActivity(new Intent(WithdrawActivity.this, VendorHome.class));
                                                    }

                                                } else {
                                                    Toast.makeText(WithdrawActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialog.show();

//                                        Toast.makeText(WithdrawActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(WithdrawActivity.this, "There was an issue transferring your funds, please try again later", Toast.LENGTH_LONG).show();
                                        pDialog.dismiss();
                                    }

                                }

                                @Override
                                public void onFailure(Call<TransferResponse> call, Throwable t) {
                                    Toast.makeText(WithdrawActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            });

                        }
                    });



                }else{
                    Toast.makeText(WithdrawActivity.this, "There was an issue verify your detail, please try again later", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<TransferCreationResponse> call, Throwable t) {
                Toast.makeText(WithdrawActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
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


    private void getAcountName(String acctNum) {

        if (acctNum.length() < 10){

        }else {

            pDialog = new SweetAlertDialog(WithdrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                            Toast.makeText(WithdrawActivity.this, "There was an issue resolving the account number, please try again", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountNameResponse> call, Throwable t) {
                        Toast.makeText(WithdrawActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();

                    }
                });
            }

        }
    }

    private void showAllBanks() {
        pDialog = new SweetAlertDialog(WithdrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WithdrawActivity.this,R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_all_banks, viewGroup, false);
                    dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
                    dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();
                    adapter = new AllBanksAdapter(bankListData,WithdrawActivity.this);
                    RecyclerView bankRv = dialogView.findViewById(R.id.rvAllBanks);
                    bankRv.setLayoutManager(new LinearLayoutManager(WithdrawActivity.this));
                    bankRv.setAdapter(adapter);







                }else{
                    pDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, "An issue occurred while fetching the list of banks, please try again later ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<BankListResponse> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });




    }


    @Override
    public void userItemClick(int pos) {
        BankListData data = bankListData.get(pos);
        edtBankName.setText(data.getName());
        bankCode = data.getCode();
        bankName = data.getName();
        Toast.makeText(WithdrawActivity.this, data.getName() + " " + data.getCode(), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(WithdrawActivity.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(WithdrawActivity.this,VendorHome.class));
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
            startActivity(new Intent(WithdrawActivity.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(WithdrawActivity.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(WithdrawActivity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(WithdrawActivity.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(WithdrawActivity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(WithdrawActivity.this, aboutUs.class));
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
                        Intent intent = new Intent(WithdrawActivity.this, Login.class);
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

    private class updateBalance extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(WithdrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... strings) {


            String amt =  strings[0];
            String pin = strings[1];


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);

            request.addProperty("inuser",amt);
            request.addProperty("App_amt",pin);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION5, envelope);
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
            return "" ;

        }
    }
    private void sendMessage(String message) {
        String newString = accountNumber.substring(0, 3) + "XXXX" + accountNumber.substring(3+4);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int a = Integer.valueOf(amount);
        int b = a /100;
        String amt = String.valueOf(b);

        double ca = Double.valueOf(preferences.getString("balance", ""));
        double e = ca - b;
        String balance = String.valueOf(e);


        String formattedDate = df.format(c.getTime());
        ApiInterface apiInterface  = ApiCli.getClient().create(ApiInterface.class);

        Call<SmsRes> call = apiInterface.sendMessage(SMS_API_KEY,"Savease","0"+accountNumber,"Your Acct "+newString+ " Has Been Debited with NGN"+ amt+ " On "+ formattedDate +" By SAVEASE TRANSFER - (Transaction Ref) Bal: NGN"+balance+"DB,  Kindly dial *384*3358# to use our USSD platform","2");

        call.enqueue(new Callback<SmsRes>() {
            @Override
            public void onResponse(Call<SmsRes> call, Response<SmsRes> response) {
                if (response.body() != null){

                    if (response.body().getData().getStatus().equalsIgnoreCase("success")){


                    }else{
                        Toast.makeText(WithdrawActivity.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(WithdrawActivity.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SmsRes> call, Throwable t) {
                Toast.makeText(WithdrawActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
            acctAdapter = new AcAdapter(WithdrawActivity.this, transactionModels);
            spinnerAccount.setAdapter(acctAdapter);

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
       new getBanks().execute(accountNumber);
    }

}
