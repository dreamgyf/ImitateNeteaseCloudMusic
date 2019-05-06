package com.dreamgyf.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dreamgyf.R;
import com.dreamgyf.service.LoginService;
import com.dreamgyf.util.DataManage;

public class LoginActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initButton();

    }

    void initButton(){
        Button signIn = findViewById(R.id.sign_in);
        Button signUp = findViewById(R.id.sign_up);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) signIn.getLayoutParams();
        layoutParams.setMargins(0,MainActivity.RESOURCES.getDisplayMetrics().heightPixels / 3,0,0);
        layoutParams.width = (int)(MainActivity.RESOURCES.getDisplayMetrics().widthPixels * 0.8);
        signIn.setLayoutParams(layoutParams);
        signUp.getLayoutParams().width = (int)(MainActivity.RESOURCES.getDisplayMetrics().widthPixels * 0.8);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.layout_sign_in);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginActivity.this.setContentView(R.layout.activity_login);
                        initButton();
                    }
                });
                final EditText phoneNumberEditText = findViewById(R.id.phone_number);
                final EditText passwordEditText = findViewById(R.id.password);
                Button signIn = findViewById(R.id.sign_in);
                phoneNumberEditText.getLayoutParams().width = (int)(MainActivity.RESOURCES.getDisplayMetrics().widthPixels * 0.9);
                passwordEditText.getLayoutParams().width = (int)(MainActivity.RESOURCES.getDisplayMetrics().widthPixels * 0.9);
                signIn.getLayoutParams().width = (int)(MainActivity.RESOURCES.getDisplayMetrics().widthPixels * 0.9);
                Drawable phoneNumberDrawable = MainActivity.RESOURCES.getDrawable(R.drawable.mobile_phone_icon);
                final Drawable passwordDrawable = MainActivity.RESOURCES.getDrawable(R.drawable.password_icon);
                phoneNumberDrawable.setBounds(0,0,50,50);
                passwordDrawable.setBounds(0,0,50,50);
                phoneNumberEditText.setCompoundDrawables(phoneNumberDrawable,null,null,null);
                passwordEditText.setCompoundDrawables(passwordDrawable,null,null,null);
                signIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String phone = phoneNumberEditText.getText().toString();
                        final String password = passwordEditText.getText().toString();
                        DataManage.saveAccountInfo(LoginActivity.this,phone,password);
                        Intent intent = new Intent(LoginActivity.this, LoginService.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("password",password);
                        startService(intent);
                    }
                });
            }
        });
    }

}
