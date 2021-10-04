package saveaseng.ng.savease.Auth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import saveaseng.ng.savease.Activities.VerifyPinActivity;
import saveaseng.ng.savease.R;

public class AuthWelcome extends AppCompatActivity {

    Button btnRegister,btnLogin,btnVerifyPin;

    String message,title;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_welcome);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnVerifyPin = (Button)findViewById(R.id.btnVer);

       Bundle extra = getIntent().getExtras();

       if (extra != null){
           message = extra.getString("message");
           title = extra.getString("title");

           if (message == null || title == null){

           }else {

               if (message.isEmpty() || title.isEmpty()) {

               } else {

                   AlertDialog.Builder builder = new AlertDialog.Builder(this);
                   builder.setMessage(message)
                           .setTitle(title)
                           .setCancelable(false)
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   alert.dismiss();
                               }
                           });
                   alert = builder.create();
                   alert.show();

               }
           }
       }





        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthWelcome.this,Login.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog mDialog = new Dialog(AuthWelcome.this, R.style.AppBaseTheme);
                mDialog.setContentView(R.layout.dialog_reg);

                Button btnRegisterDialog = (Button)mDialog.findViewById(R.id.btnRegisterDialog);

                btnRegisterDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AuthWelcome.this,RegisterationActivity.class));
                    }
                });
                mDialog.show();

            }
        });

        btnVerifyPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthWelcome.this, VerifyPinActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        confirmDialog(getApplicationContext());
    }

    private void confirmDialog(Context context){

        final AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Alert");
        alert.setMessage("Do you want to close the savease ?");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();
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
