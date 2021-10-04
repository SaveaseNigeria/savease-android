package saveaseng.ng.savease.Auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import saveaseng.ng.savease.R;

public class SignupCategory extends AppCompatActivity {

    ImageView user,vendor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_category);

        user = (ImageView)findViewById(R.id.usersSignup);
        vendor = (ImageView)findViewById(R.id.vendorSignup);


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupCategory.this,Signup.class));
            }
        });

        vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupCategory.this,SignupVendor.class));
            }
        });





    }
}
