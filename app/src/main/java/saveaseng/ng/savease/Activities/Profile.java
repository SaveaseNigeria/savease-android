package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.R;

public class Profile extends Home {
    private String acctType,accountTypeVal,accntNum;
    SharedPreferences preferences;
    private DrawerLayout drawer;
    private DatabaseReference bvnRef;
    private LinearLayout linAcc,bottom_sheet;
    private TextView accntName,accntNumber,balance,userType,firstName,lastName,phoneNumber,saveaseId,bvn,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        bvnRef = FirebaseDatabase.getInstance().getReference().child("Bvn");

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        accountTypeVal =preferences.getString("userType", "");
        accntNum = preferences.getString("accountNumber","");

        accntName = (TextView)findViewById(R.id.txtAccountName);
        accntNumber = (TextView)findViewById(R.id.txtAccountNumber);
        balance = (TextView)findViewById(R.id.txtAccountBalance);
        userType = (TextView)findViewById(R.id.txtAccountType);
        firstName = (TextView)findViewById(R.id.firstNameProfile);
        lastName = (TextView)findViewById(R.id.lasttNameProfile);
        phoneNumber = (TextView)findViewById(R.id.phoneProfile);
        saveaseId = (TextView)findViewById(R.id.saveaseIdProfile);
        bvn = (TextView)findViewById(R.id.bvnProfile);
        email = (TextView)findViewById(R.id.emailProfile);


        getBvnName(accntNum);


        firstName.setText(preferences.getString("fname",""));
        lastName.setText(preferences.getString("lname",""));
        email.setText(preferences.getString("email",""));
        phoneNumber.setText(preferences.getString("phone",""));
        saveaseId.setText(preferences.getString("accountNumber",""));

        userType.setText(accountTypeVal);

        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);
        Button btnaddAccnt = (Button) findViewById(R.id.btnaddAccnt);

        btnaddAccnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,BankActivity.class));
            }
        });

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                startActivity(new Intent(Profile.this,AccountOfficer.class));
            }
        });

        String fulln = preferences.getString("fname","") +" " + preferences.getString("lname","");
        balance.setText("N "+preferences.getString("balance",""));
        accntNumber.setText(preferences.getString("accountNumber",""));
        accntName.setText(fulln);
        linAcc = (LinearLayout)findViewById(R.id.linAcc);
        linAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,BankActivity.class));

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profile);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void getBvnName(String accntNum) {
        bvnRef.child(accntNum)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String bvnNum = dataSnapshot.child("bvnNumber").getValue().toString();
                            bvn.setText(bvnNum);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (accountTypeVal.equalsIgnoreCase("user")){
                startActivity(new Intent(Profile.this,Home.class));

            }else {
                startActivity(new Intent(Profile.this,VendorHome.class));
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
        if (accountTypeVal.equalsIgnoreCase("user")){
            if (id == R.id.homeBack) {
                startActivity(new Intent(Profile.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(Profile.this,VendorHome.class));
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
            startActivity(new Intent(Profile.this,TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(Profile.this,TransactionStatementActivity.class));

        }else if (id == R.id.verifyV) {
            startActivity(new Intent(Profile.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(Profile.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(Profile.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(Profile.this,aboutUs.class));
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
                        Intent intent = new Intent(Profile.this, Login.class);
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
