package saveaseng.ng.savease.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
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
import java.util.Timer;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Adapter.CardAdapter;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.CardModel;
import saveaseng.ng.savease.R;

public class BuyVoucherActivity extends Home {
    private static final String METHOD_NAME = "saveOrder2";
    private static final String SOAP_ACTION = "http://savease.ng/saveOrder2";

    private static final String METHOD_NAME2 = "updateBalance";
    private static final String SOAP_ACTION2 = "http://savease.ng/updateBalance";

    private static final String METHOD_NAME3 = "existTransPIN";
    private static final String SOAP_ACTION3 = "http://savease.ng/existTransPIN";


    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private EditText edtAccntNumber,edtPinCodeBuyVoucher;
    private ArrayList<CardModel> cardModels;
    private CardAdapter adapter;
    private int totalQty;
    private float totalAmnt;
    private String username, voucher, cardAmount,accountNumbers,cardTypeAmount;
    private String pent = "";
    private double percent;
    private int imageID;
    TextView txtAmount, txtQuantity,userType;
    String Quantity,PinCode,accountType;
    private RadioGroup radioCommissionGroup;
    private RadioButton radioCommissionButton;
    int B;
    SharedPreferences preferences;
    private Timer timer;
    boolean connected = false;
    private DrawerLayout drawer;
    private TextView accntName, accntNumber, balance;
    private int q;
    private Spinner spinnerVoucher;
    private MaterialSpinner spinnerPercentage;
    Button  buyCard;
    private String balan;
    Dialog dialog;

    private static final String[] percentageType = {
            "Percentage to charge",
            "2",
            "1.5",
            "1",
            "0.5"

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_voucher_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initList();
        totalAmnt = 0;
        totalQty = 0;


        dialog = new Dialog(this);


        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = preferences.getString("uname", "");
        accountNumbers = preferences.getString("accountNumber", "");
        accountType =preferences.getString("userType", "");
        radioCommissionGroup = (RadioGroup)findViewById(R.id.radioCommision);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_buy_voucher);
        setSupportActionBar(toolbar);

        accntName = (TextView) findViewById(R.id.txtAccountName);
        accntNumber = (TextView) findViewById(R.id.txtAccountNumber);
        balance = (TextView) findViewById(R.id.txtAccountBalance);
        userType = (TextView) findViewById(R.id.txtAccountType);

        userType.setText(accountType);

        String fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        accntName.setText(fulln);
        balance.setText("N "+preferences.getString("balance", ""));
        accntNumber.setText(preferences.getString("accountNumber", ""));
        balan = preferences.getString("balance", "");

        spinnerVoucher = (Spinner) findViewById(R.id.spinVouchers);
        spinnerPercentage = (MaterialSpinner)findViewById(R.id.spinPercentage);
        spinnerPercentage.setItems(percentageType);
        spinnerPercentage.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {


                if (item.toString().equalsIgnoreCase("Percentage to charge")){
                    pent = "";

                }else {
                    pent = item.toString();

                }
            }
        });

        spinnerPercentage.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();

            }
        });

        adapter = new CardAdapter(BuyVoucherActivity.this, cardModels);
        spinnerVoucher.setAdapter(adapter);
        spinnerVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CardModel cardModels = (CardModel) parent.getItemAtPosition(position);


                if (cardModels.getCardId() == 0) {
                    voucher = cardModels.getAmount();
                } else {

                    voucher = cardModels.getAmount();
                    imageID = cardModels.getCardId();
                    cardTypeAmount = cardModels.getAmount();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuyVoucherActivity.this,AccountOfficer.class));
            }
        });

        Pinview pinview1 = findViewById(R.id.edtPinCodeBuyVoucher);
        pinview1.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                PinCode = pinview1.getValue();

            }
        });

        edtAccntNumber = (EditText) findViewById(R.id.edtQuantityNeeded);

        buyCard = (Button) findViewById(R.id.btnBuyVoucher);


        buyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String qn = edtAccntNumber.getText().toString().trim();
                totalAmnt = Float.valueOf(voucher)*Float.valueOf(qn);

                int selectedId=radioCommissionGroup.getCheckedRadioButtonId();
                radioCommissionButton=(RadioButton)findViewById(selectedId);

                String pect = radioCommissionButton.getText().toString().trim();

                if (pect.contains("0.5")) {
                    Toast.makeText(BuyVoucherActivity.this, "0.5", Toast.LENGTH_SHORT).show();
                    pent = "0.5";
                }else if (pect.contains("1.5")){
                    Toast.makeText(BuyVoucherActivity.this, "1.5", Toast.LENGTH_SHORT).show();
                    pent = "1.5";

                }else if (pect.contains("2")){
                    Toast.makeText(BuyVoucherActivity.this, "2", Toast.LENGTH_SHORT).show();
                    pent = "2";
                }else {
                    Toast.makeText(BuyVoucherActivity.this, "1", Toast.LENGTH_SHORT).show();
                    pent = "1";
                }

                    double a = Double.parseDouble(voucher)* Double.parseDouble(qn);
                    double amount = Double.parseDouble(balan);
                    int bal = (int) amount;

                    if (a > amount){
                        Toast.makeText(BuyVoucherActivity.this, "oops, insufficient balance to buy this voucher. Please fund your account and try again", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());
                    }else {

                        if (pent.isEmpty() || pent.equalsIgnoreCase("")){
                            Toast.makeText(BuyVoucherActivity.this, "Please select a percentage to charge the voucher", Toast.LENGTH_LONG).show();
                            return;

                        }else {
                            dialog(totalAmnt, bal, qn, pent);
                        }

                    }

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_buy_voucher);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_buy_voucher);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void dialog(final float sub, final int b, final String quantity,String percent) {

        buyCard.setEnabled(false);

        double pe = Double.parseDouble(percent);

        float c = (float) pe/100;
        float be = c *sub;
        float o = sub - be;

        cardAmount = String.valueOf(o);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.buy_voucher_dialog);
        TextView subTotal = dialog.findViewById(R.id.subTotalText);
        TextView commission = dialog.findViewById(R.id.commissionTxt);
        TextView orderTotal = dialog.findViewById(R.id.orderTotalTxt);
        subTotal.setText("Sub-Total: " + String.valueOf(sub));
        commission.setText("Commission: " + String.valueOf(be));
        orderTotal.setText("Order Total: " + String.valueOf(o));
        Button discard = dialog.findViewById(R.id.btnDiscard);
        Button confirm = dialog.findViewById(R.id.btnConfirm);

        final CheckBox agree = dialog.findViewById(R.id.chkAgree);
        dialog.show();
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BuyVoucherActivity.this, "discard", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                buyCard.setEnabled(true);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agree.isChecked()) {
                    if (sub > b) {
                        Toast.makeText(BuyVoucherActivity.this, "oops seems like yours balance is low to purchase this voucher, please fund your account or use other payment methods", Toast.LENGTH_LONG).show();
                    } else {
                        voucher = String.valueOf(o);
                        Quantity = quantity;
                        B = b;

                        dialog.dismiss();
                        new verifyPinCode().execute(username,PinCode);

                    }

                } else {
                    Toast.makeText(BuyVoucherActivity.this, "Please agree to the Terms and Condition to continue", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });


    }



    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{

            if (accountType.equalsIgnoreCase("user")){
                startActivity(new Intent(BuyVoucherActivity.this,Home.class));

            }else {
                startActivity(new Intent(BuyVoucherActivity.this,VendorHome.class));
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
                startActivity(new Intent(BuyVoucherActivity.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(BuyVoucherActivity.this,VendorHome.class));
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
            startActivity(new Intent(BuyVoucherActivity.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(BuyVoucherActivity.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(BuyVoucherActivity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
              startActivity(new Intent(BuyVoucherActivity.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(BuyVoucherActivity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(BuyVoucherActivity.this,aboutUs.class));
        }else if (id == R.id.logout){
            confirmDialog(getApplicationContext());
        }else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initList() {
        cardModels = new ArrayList<>();
        cardModels.add(new CardModel("Vouchers", 0));
        cardModels.add(new CardModel("100", R.drawable.onehundred));
        cardModels.add(new CardModel("200", R.drawable.twohundred));
        cardModels.add(new CardModel("500", R.drawable.cardfive));
        cardModels.add(new CardModel("1000", R.drawable.cardonethousand));
        cardModels.add(new CardModel("2000", R.drawable.twothousand));
        cardModels.add(new CardModel("5000", R.drawable.fivethousand));
        cardModels.add(new CardModel("10000", R.drawable.tenthousand));
    }


    private class Buy extends AsyncTask<String, String, String> {

        SweetAlertDialog pDialogBuy = new SweetAlertDialog(BuyVoucherActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialogBuy.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialogBuy.setTitleText("Buying pin..please wait");
            this.pDialogBuy.setCancelable(true);
            this.pDialogBuy.show();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("1")) {
                this.pDialogBuy.dismiss();

                new FancyAlertDialog.Builder(BuyVoucherActivity.this)
                        .setTitle("Success")
                        .setBackgroundColor(Color.parseColor("#212435"))
                        .setMessage("Your voucher purchase transaction was successful")
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
                                startActivity(new Intent(BuyVoucherActivity.this,VendorHome.class));
                            }
                        })
                        .build();

            } else {
                pDialogBuy.dismiss();

                new FancyAlertDialog.Builder(BuyVoucherActivity.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Your voucher purchase transaction was unsuccessful")
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
        protected String doInBackground(String... arrayLists) {
            String re = "";
            String amount = "";


            String usernames = arrayLists[3];
            String amountNumber = arrayLists[0];
            String cardTypd= arrayLists[1];
            String cardAmount = arrayLists[2];
            String quanty = arrayLists[4];
            String balance = arrayLists[5];
            String perct = arrayLists[6];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("saveaseIDz", amountNumber);
            request.addProperty("in_cardType", cardTypd);
            request.addProperty("in_cardAmount", cardAmount);
            request.addProperty("in_orderby",usernames);
            request.addProperty("percentage",perct);
            request.addProperty("qty", Integer.parseInt(quanty));
            request.addProperty("lblBa",balance);


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
            SoapObject response = null;
            try {
                result = (Object) envelope.getResponse();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            }

            re = String.valueOf(result);

            return re;
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
                        Intent intent = new Intent(BuyVoucherActivity.this, Login.class);
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

    private class verifyPinCode extends AsyncTask<String,String,String> {

        SweetAlertDialog pDialogVerifyPin = new SweetAlertDialog(BuyVoucherActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialogVerifyPin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialogVerifyPin.setTitleText("Buying pin..please wait");
            this.pDialogVerifyPin.setCancelable(true);
            this.pDialogVerifyPin.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == "null"){
               this.pDialogVerifyPin.dismiss();
                Toast.makeText(BuyVoucherActivity.this, "Your pin code is not correct and does not match the one you created, please enter a valid ", Toast.LENGTH_LONG).show();

            }else {
                if (s.equalsIgnoreCase("1"))
                {
                   this.pDialogVerifyPin.dismiss();
                    new Buy().execute(accountNumbers,cardTypeAmount,cardAmount,username,Quantity,String.valueOf(B),pent);
                }else {
                   this.pDialogVerifyPin.dismiss();
                    Toast.makeText(BuyVoucherActivity.this, "Your pin code is not correct and does not match the one you created, please enter a valid ", Toast.LENGTH_LONG).show();

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

}


