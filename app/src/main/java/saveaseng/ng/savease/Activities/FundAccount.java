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
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.Model.SmsRes;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Utils.ApiCli;
import saveaseng.ng.savease.Utils.ApiInterface;

public class FundAccount extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY};
    private static final String METHOD_NAME = "FundAcct";
    private static final String SOAP_ACTION = "http://savease.ng/FundAcct";
    private static final String URL = "http://savease.ng/webservice1.asmx";
    private static final String NAMESPACE = "http://savease.ng/";
    private final static String SMS_API_KEY = "9Pc1XtdCYg43wdJ6AlbCSCyTlLqc2voEFpl9DvmUq0zcKJTDbdE4aOYOPtzz";


    private SupportedCardTypesView mSupportedCardTypesView;
    private Button startFunding;
    private DrawerLayout drawer;
    protected CardForm mCardForm;
    private EditText edtAmountFunded;
    TextView fullNameT, acctNumber, balanceT,userType;
    private String accountNumber, accountName, email, phone,amount,accountType;
    SharedPreferences preferences;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fundaccount_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountName = preferences.getString("uname", "");
        accountNumber = preferences.getString("accountNumber","");
        email = preferences.getString("email", "");
        phone = preferences.getString("phone", "");
        accountType =preferences.getString("userType", "");

        fullNameT = (TextView) findViewById(R.id.txtAccountName);
        userType = (TextView) findViewById(R.id.txtAccountType);
        userType.setText(accountType);
        acctNumber = (TextView) findViewById(R.id.txtAccountNumber);
        balanceT = (TextView) findViewById(R.id.txtAccountBalance);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        startFunding = (Button) findViewById(R.id.btnFundAccount);
        edtAmountFunded = (EditText) findViewById(R.id.edtAmountFunded);

        Button accontOfficer = (Button) findViewById(R.id.btnAccountOfficer);
        accontOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FundAccount.this, AccountOfficer.class));
            }
        });


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_fundaccount);
        setSupportActionBar(toolbar);

        String fulln = preferences.getString("fname", "") + " " + preferences.getString("lname", "");
        balanceT.setText("N "+preferences.getString("balance", ""));
        acctNumber.setText(preferences.getString("accountNumber", ""));
        fullNameT.setText(fulln);


        mCardForm = findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
                .actionLabel("Fund Account")
                .setup(FundAccount.this);
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);


        startFunding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumber = mCardForm.getCardNumber();
                String month = mCardForm.getExpirationMonth();
                String year = mCardForm.getExpirationYear();
                String cvc = mCardForm.getCvv();
                String name = mCardForm.getCardholderName();
                amount = edtAmountFunded.getText().toString().trim();
                Charge charge = new Charge();

                if (cardNumber.isEmpty() && month.isEmpty() && year.isEmpty() && cvc.isEmpty() && name.isEmpty() && amount.isEmpty()) {
                    Toast.makeText(FundAccount.this, "Please complete all required fields to fund account", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (mCardForm.isValid()) {
                        final SweetAlertDialog pDialog = new SweetAlertDialog(FundAccount.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Funding account,please wait....");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        Card card = new Card(cardNumber, Integer.parseInt(month), Integer.parseInt(year), cvc);
                        charge.setCard(card);
                        int amt = Integer.parseInt(amount);
                        final int a = amt * 100;
                        charge.setAmount(a);
                        charge.setEmail(email);


                        PaystackSdk.chargeCard(FundAccount.this, charge, new Paystack.TransactionCallback() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                pDialog.dismiss();
                               new updateAccount().execute(amount,accountName);
                            }

                            @Override
                            public void beforeValidate(Transaction transaction) {

                            }

                            @Override
                            public void onError(Throwable error, Transaction transaction) {
                                pDialog.dismiss();
                                Toast.makeText(FundAccount.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            }
                        });


                    } else {
                        Toast.makeText(FundAccount.this, "Please enter a valid card to fund account ", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_fundacount);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_fundaccount);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onCardFormSubmit() {
        if (mCardForm.isValid()) {
            Toast.makeText(this, " Card is Valid", Toast.LENGTH_LONG).show();
        } else {
            mCardForm.validate();
            Toast.makeText(this, "Card is Invalid", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
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
                startActivity(new Intent(FundAccount.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(FundAccount.this,VendorHome.class));
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
            startActivity(new Intent(FundAccount.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(FundAccount.this, TransactionStatementActivity.class));

        } else if (id == R.id.verifyV) {
            startActivity(new Intent(FundAccount.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(FundAccount.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(FundAccount.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(FundAccount.this, aboutUs.class));
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

    private class updateAccount extends AsyncTask<String, String, String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(FundAccount.this, SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            this.pDialog.setTitleText("Updating Account ,please wait....");
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.pDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {
                //  new RefreshBalance().execute(String.valueOf(totalAmnt),username);
                sendMessage();
                new FancyAlertDialog.Builder(FundAccount.this)
                        .setTitle("Success")
                        .setBackgroundColor(Color.parseColor("#212435"))
                        .setMessage("Your Funding Account transaction was successful")
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

                            }
                        })
                        .build();
            } else {

                new FancyAlertDialog.Builder(FundAccount.this)
                        .setTitle("Failed")
                        .setBackgroundColor(Color.parseColor("#DF5C4E"))
                        .setMessage("Your Funding Account transaction was unsuccessful, please contact customer service to assist you")
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
            String amount = strings[0];
            String username = strings[1];



            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("inuser", username);
            request.addProperty("App_amt", amount);


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


    private void confirmDialog(Context context){

        final AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Logout");
        alert.setMessage("Do you want to logot ?");
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
                        Intent intent = new Intent(FundAccount.this, Login.class);
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

    private void sendMessage() {
        double b = Double.valueOf(amount);
        double ca = Double.valueOf(preferences.getString("balance", ""));
        double e = ca - b;
        String balance = String.valueOf(e);

        String newString = accountNumber.substring(0, 3) + "XXXX" + accountNumber.substring(3+4);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ApiInterface apiInterface  = ApiCli.getClient().create(ApiInterface.class);

        Call<SmsRes> call = apiInterface.sendMessage(SMS_API_KEY,"Savease","0"+accountNumber,"Your Acct "+newString+ " Has Been Credited with NGN"+ amount+ " On "+ formattedDate +" By SAVEASE FUNDACCOUNT - (Transaction Ref) Bal: +NGN"+balance+"CR","2");

        call.enqueue(new Callback<SmsRes>() {
            @Override
            public void onResponse(Call<SmsRes> call, Response<SmsRes> response) {
                if (response.body() != null){

                    if (response.body().getData().getStatus().equalsIgnoreCase("success")){

                    }else{
                        Toast.makeText(FundAccount.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(FundAccount.this, "There was an issue sending alert sms", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SmsRes> call, Throwable t) {
                Toast.makeText(FundAccount.this
                        , t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
