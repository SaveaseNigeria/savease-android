package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import saveaseng.ng.savease.Adapter.ExpandableListAdapter;
import saveaseng.ng.savease.R;

public class FaqActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private String accountName,accountType;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_nav);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_faq);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountType =preferences.getString("userType", "");


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_faq);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_faq);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("What is Savease");
        listDataHeader.add("Getting started with Savease");
        listDataHeader.add("What is a username");
        listDataHeader.add("Changing your password");
        listDataHeader.add("Forgot Password");
        listDataHeader.add("How to make a deposit to your wallet/account");
        listDataHeader.add("How to make a deposit to another wallet/account");
        listDataHeader.add("How to transfer money from a wallet to another wallet");
        listDataHeader.add("How to transfer money from your wallet to a bank account");
        listDataHeader.add("How to become a Vendor on Savease");
        listDataHeader.add("How to buy the Savease prepaid deposit cards");
        listDataHeader.add("What to do if a Savease voucher is misplaced or stolen");
        listDataHeader.add("How to make money on Savease");
        listDataHeader.add("How to block vouchers and rebatch them");
        listDataHeader.add("How to view your transaction summary on Savease");
        listDataHeader.add("Who is an account officer");
        listDataHeader.add("How can Savease help you gain financial independence");
        listDataHeader.add("Is Savease a bank");
        listDataHeader.add("Reporting a problem");
        listDataHeader.add("Updating Savease");
        listDataHeader.add("How to use Savease on your computer");
        listDataHeader.add("Reinstalling Savease");
        listDataHeader.add("Where can i buy Savease Voucher");
        listDataHeader.add("How many wallet can i have");
        listDataHeader.add("How to fund your account");
        listDataHeader.add("What is Pin Code");
        listDataHeader.add("How to make withdrawal");

        // Adding child data
        List<String> a = new ArrayList<String>();
        a.add("Savease is a financial solution that solves the complex problem of making deposits and savings. It is available as a USSD App, Mobile App, and a Web App. It adopts the style of scratch cards, but uses preloaded prepaid deposit pin as exchange for value, thereby translating paper money into electronic money which is then understood electronically. It is helping millions of people save their money, due to convenience it presents and represents. It has broken the banking barrier of distance, space and structure. ");
        List<String> b = new ArrayList<String>();
        b.add("You can get started on any of the following: USSD, Mobile App and Web App. To use the USSD App, kindly dial the *0456#, then follow the on-screen options to sign up to Savease. You can use the Mobile App by downloading it from Playstore for Android users or Applestore for IOS users. When downloaded, navigate to the Register now page, then fill in the required information. You can also log on to www.savease.ng, then nagivate to sign up page.");
        List<String> c = new ArrayList<String>();
        c.add("A username is a unique identification used by a person with access to the Savease online services ");
        List<String> d = new ArrayList<String>();
        d.add("To change your password, log on to Savease, then navigate to your profile page. Click on Change Password");
        List<String> e = new ArrayList<String>();
        e.add("To change your password, kindly go to your login page. Click on Forgot Password. Type in your Savease ID, which is also your wallet/account number. A password change link will be generated and sent to email address used during registration.");
        List<String> f = new ArrayList<String>();
        f.add("Kindly locate and purchase a Savease prepaid deposit card with the equivalent value of money from the nearest authorized vendor. Open the Savease mobile app and login. Select the Deposit icon on the dashboard. Select self deposit. Provide the card pin then proceed by clicking the make deposit button. A success message will be sent if transaction is successful, also a failure message will be received.");
        List<String> g = new ArrayList<String>();
        g.add("Kindly locate and purchase a Savease prepaid deposit card with the equivalent value of money from the nearest authorized vendor. Open the Savease mobile app and login. Select the Deposit icon on the dashboard. Provide the card pin, wallet/account number, then proceed by clicking the make deposit button. A success message will be sent if transaction is successful, also a failure message will be received.");
        List<String> h = new ArrayList<String>();
        h.add("Login. Click on the transfer icon or navigation button. Click on the select transfer type and select Savease Account. Provide the wallet ID of the recipient, followed by the amount to be transferred. Confirm the attestation. A success message will be sent if transaction is successful, else a failure message will be received.");
        List<String> i = new ArrayList<String>();
        i.add("Login. Click on the Transfer icon or the navigation bar. Click on the dropdown tab or button to select the appropriate account. Click on other bank, then select the appropriate bank as available in the drop-down tab. Insert the account number of the recipient, followed by the amount to be transferred. Confirm the attestation. A success message will be sent if transaction is successful, also a failure message will be received.");
        List<String> j = new ArrayList<String>();
        j.add("All registered Savease users are Vendors. Login to Savease, then navigate to the buy voucher icon and vendor table. On the vendor table, click on Request for Vendor Materials. A Savease representative will contact you to complete your registration.");
        List<String> k = new ArrayList<String>();
        k.add("Savease vouchers are available in stores, markets, minimarkets, neighboring shops, kiosks and any other place where human activities are done. You could also buy the prepaid cards from the mobile app or web app by logging in, then navigate to the buy voucher page.");
        List<String> l = new ArrayList<String>();
        l.add("Login to the web application. Click on Voucher Table navigation bar. Select the Block Voucher radio button. The block voucher button present an elevated section where vouchers can be blocked, whenever there is a case of misplacement or theft. You can block vouchers by their serial number, voucher pin or batch code. Once blocked, the vouchers will be rebatched and the rebatched vouchers will be sent to your inbox and voucher table.");
        List<String> m = new ArrayList<String>();
        m.add("Everything");
        List<String> n = new ArrayList<String>();
        n.add("After logging in to the web application, navigate to the voucher table. You will find two radio button labelled Voucher Table and Block Voucher. Select the block voucher button, which present an elevated section where vouchers can be blocked, whenever there is a case of misplacement or theft. You can block vouchers by their serial number, voucher pin or batch code. Once blocked, the vouchers will be rebatched and the rebatched vouchers will be sent to your inbox and voucher table. ");
        List<String> p = new ArrayList<String>();
        p.add("Login. On your dashboard, open the statement icon, which presents a list of your transactions. According to date, time,amount,beneficiary name, among others");
        List<String> r = new ArrayList<String>();
        r.add("The account officer assigned to you is tasked with the responsibility of helping you gain financial literacy. His/her ultimate purpose is to help you build capital through savings then design financial packages to aid you gain financial independence.");
        List<String> s = new ArrayList<String>();
        s.add("No. it is not bank. It is a financial technology corporation in partnership to the banking and other financial institutions.");
        List<String> t = new ArrayList<String>();
        t.add("From the mobile app, navigate to the complaint page and fill in as requested. Otherwise, call customer care on 456; or log on to www.savease.ng , then navigate to the compliant page to fill in the required information. ");
        List<String> u = new ArrayList<String>();
        u.add("Periodically, modifications are made available to serve you better. This update will require you to download a new version from Google playstore or Apple store respectively. Updates on the web applications are automatic, and will not require you downloading anything. The USSD update are made available automatically, but with adequate user guide information as they become available.");
        List<String> v = new ArrayList<String>();
        v.add("To use Savease on your computer, you will log on to the www.savease.ng from a web browser.");
        List<String> w = new ArrayList<String>();
        w.add("Kindly go to google playstore or apple store respectively. Download and install the Savease app.");
        List<String> x = new ArrayList<String>();
        x.add("Savease vouchers are available in stores, markets, minimarkets, neighbouring shops, kiosks and any other place where human activities are done. You could also buy the prepaid cards from the mobile app or web app by logging in, then navigate to the buy voucher page.");
        List<String> y = new ArrayList<String>();
        y.add("Due to regulations, you can only have a maximum of 3 wallets attached to a BVN.");
        List<String> z = new ArrayList<String>();
        z.add("Login. On your dashboard, click on your fund account icon. Provide details from credit card you intend to fund your account with. An OTP(One Time Password) will be sent to the credit card telephone number which will be provided upon request. Your account will be credited and updated with the equivalent. ");
        List<String> za = new ArrayList<String>();
        za.add("This is your private identification number which serves as a personal secret number that shouldn't be disclosed to any other person, which will serve as your true authentication and signature.");
        List<String> zb = new ArrayList<String>();
        zb.add("Login. CLick on the withdraw icon on the dashboard. Provide the receiving bank details, followed with your personal secret pin, then click on the verify button. Automatically the transaction will be processed and a credit alert will received. Please note that to make a withdrawal you must have a minimum balance of one thousand five hundred naira.");


        listDataChild.put(listDataHeader.get(0), a);
        listDataChild.put(listDataHeader.get(1), b);
        listDataChild.put(listDataHeader.get(2), c);
        listDataChild.put(listDataHeader.get(3), d);
        listDataChild.put(listDataHeader.get(4), e);
        listDataChild.put(listDataHeader.get(5), f);
        listDataChild.put(listDataHeader.get(6), g);
        listDataChild.put(listDataHeader.get(7), h);
        listDataChild.put(listDataHeader.get(8), i);
        listDataChild.put(listDataHeader.get(9), j);
        listDataChild.put(listDataHeader.get(10), k);
        listDataChild.put(listDataHeader.get(11), l);
        listDataChild.put(listDataHeader.get(12), m);
        listDataChild.put(listDataHeader.get(13), n);
        listDataChild.put(listDataHeader.get(14), p);
        listDataChild.put(listDataHeader.get(16), r);
        listDataChild.put(listDataHeader.get(17), s);
        listDataChild.put(listDataHeader.get(18), t);
        listDataChild.put(listDataHeader.get(19), u);
        listDataChild.put(listDataHeader.get(20), v);
        listDataChild.put(listDataHeader.get(21), w);
        listDataChild.put(listDataHeader.get(22), x);
        listDataChild.put(listDataHeader.get(23), y);
        listDataChild.put(listDataHeader.get(24), z);
        listDataChild.put(listDataHeader.get(25), za);
        listDataChild.put(listDataHeader.get(26), zb);
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
        if (id == R.id.homeBack) {
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      if (id == R.id.makeTransfer) {
            startActivity(new Intent(getApplicationContext(),TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(getApplicationContext(),TransactionStatementActivity.class));

        } else if (id == R.id.settings) {

            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("accountType","0");
            edit.apply();
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }  else if (id == R.id.appSettings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
