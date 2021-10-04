package saveaseng.ng.savease;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import saveaseng.ng.savease.Utils.Welcome;

public class MainActivity extends AppCompatActivity {
    Boolean connected = false;
    SweetAlertDialog pDialog;
    String data1 ,data2;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();


        pDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE);
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            int secounds = 3;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(extras != null){
                        data1 = extras.getString("message");
                        data2 = extras.getString("title");
                        Intent intent = new Intent(MainActivity.this, Welcome.class);
                        intent.putExtra("message",data1);
                        intent.putExtra("title",data2);
                        startActivity(intent);
                        finish();

                    }else {
                        Intent intent = new Intent(MainActivity.this, Welcome.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, secounds * 1000);

     }
        else {
            connected = false;
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }



    }
}
