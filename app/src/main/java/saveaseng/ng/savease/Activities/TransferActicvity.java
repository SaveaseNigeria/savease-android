package saveaseng.ng.savease.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Timer;

import saveaseng.ng.savease.Auth.Login;
import saveaseng.ng.savease.Complaint;
import saveaseng.ng.savease.R;
import saveaseng.ng.savease.Transfer.DefaultTransfer;
import saveaseng.ng.savease.Transfer.SaveaseTransfer;

public class TransferActicvity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private MaterialSpinner spinnerTranferBankType;
    private String bankType;
    private Timer timer;
    SharedPreferences preferences;
    private DrawerLayout drawer;
    String fulln;
    TextView fullNameT,acctNumber,balanceT,userType;
    String balance,saveaseId,accountNumber,accountType;

    private static final String[] transferBankType = {
            "Transfer Type",
            "Savease Wallet"

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_nav);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_transfer);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_transfer);

        balance = preferences.getString("balance", "");
        saveaseId = preferences.getString("accountNumber", "");
        fulln = preferences.getString("fname","") +" " + preferences.getString("lname","");
        accountType =preferences.getString("userType", "");

        fullNameT =(TextView)findViewById(R.id.txtAccountName);
        acctNumber =(TextView)findViewById(R.id.txtAccountNumber);
        balanceT =(TextView)findViewById(R.id.txtAccountBalance);
        userType =(TextView)findViewById(R.id.txtAccountType);

        balanceT.setText("N "+balance);
        acctNumber.setText(saveaseId);
        fullNameT.setText(fulln);
        userType.setText(accountType);


        spinnerTranferBankType = (MaterialSpinner)findViewById(R.id.spinTransferBankType);
        spinnerTranferBankType.setItems(transferBankType);
        spinnerTranferBankType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                bankType = item;

                if (bankType.equalsIgnoreCase("Transfer Type")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameTransfer,new DefaultTransfer())
                            .addToBackStack(null).commit();
                }else if (bankType.equalsIgnoreCase("Savease Wallet")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameTransfer,new SaveaseTransfer())
                            .addToBackStack(null).commit();
                }
            }
        });
        spinnerTranferBankType.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });

        Button btnAccountOfficer = (Button) findViewById(R.id.btnAccountOfficer);

        btnAccountOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransferActicvity.this,AccountOfficer.class));
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.frameTransfer,new DefaultTransfer())
                .addToBackStack(null).commit();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_transfer);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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
                startActivity(new Intent(TransferActicvity.this,Home.class));
            }
        }else {
            if (id == R.id.homeBack) {
                startActivity(new Intent(TransferActicvity.this,VendorHome.class));
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
            startActivity(new Intent(TransferActicvity.this,TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(TransferActicvity.this,TransactionStatementActivity.class));

        }else if (id == R.id.verifyV) {
            startActivity(new Intent(TransferActicvity.this, VerifyHome.class));
        } else if (id == R.id.userGuide) {
            startActivity(new Intent(TransferActicvity.this,UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(TransferActicvity.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(TransferActicvity.this,aboutUs.class));
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
                        Intent intent = new Intent(TransferActicvity.this, Login.class);
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
