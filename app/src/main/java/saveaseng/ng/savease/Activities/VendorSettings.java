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
import android.widget.RelativeLayout;

import saveaseng.ng.savease.Auth.ChangePassword;
import saveaseng.ng.savease.R;

public class VendorSettings extends AppCompatActivity implements
NavigationView.OnNavigationItemSelectedListener{
    RelativeLayout linChangePassword;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_settings);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        linChangePassword = (RelativeLayout)findViewById(R.id.linChangePassword);
        linChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VendorSettings.this, ChangePassword.class));
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
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
            Intent intent = new Intent(getApplicationContext(), VendorHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.deposits) {
            startActivity(new Intent(getApplicationContext(),DepositActivity.class));
            // Handle the camera action
        } else if (id == R.id.makeTransfer) {
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

}
