package saveaseng.ng.savease;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import saveaseng.ng.savease.Activities.TransactionStatementActivity;
import saveaseng.ng.savease.Activities.TransferActicvity;
import saveaseng.ng.savease.Activities.UserGuide;
import saveaseng.ng.savease.Activities.VerifyPinActivity;
import saveaseng.ng.savease.Activities.aboutUs;
import saveaseng.ng.savease.Auth.Login;

public class Complaint extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private EditText edtTitle,edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.complaint_nav);

        edtMessage = (EditText)findViewById(R.id.edtMessageComplaint);
        edtTitle = (EditText)findViewById(R.id.edtTitleComplaint);

        Button sendCOmplain = (Button)findViewById(R.id.btnSendComplain);

        sendCOmplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_complaint);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_complaint);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_complaint);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void sendMail() {
        String title  = edtTitle.getText().toString().trim();
        String message = edtMessage.getText().toString().trim();

        String[] TO = {"compliant@savease.ng"};
        String[] CC = {"escalate@savease.ng"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Complaint.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(Complaint.this, TransferActicvity.class));

        } else if (id == R.id.transStatement) {
            startActivity(new Intent(Complaint.this, TransactionStatementActivity.class));

        }else if (id == R.id.verifyV) {
            startActivity(new Intent(Complaint.this, VerifyPinActivity.class));
        }  else if (id == R.id.userGuide) {
            startActivity(new Intent(Complaint.this, UserGuide.class));
        } else if (id == R.id.complain) {
            startActivity(new Intent(Complaint.this, Complaint.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(Complaint.this, aboutUs.class));
        }else if (id == R.id.logout){
            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("accountType", "0");
            edit.apply();
            Intent intent = new Intent(Complaint.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
