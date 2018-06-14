package it.android.j940549.mybiblioteca;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
    @Override
    protected void onStart() {
        super.onStart();
        //cassaforteaperta.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent vaiaHome =new Intent(getBaseContext(), Login_Ute_Ges_Activity.class);
                startActivity(vaiaHome);
                finish();
            }
        },2000);


    }

}


