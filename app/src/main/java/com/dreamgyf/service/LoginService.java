package com.dreamgyf.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.dreamgyf.activity.MainActivity;

import org.json.JSONException;

import java.io.IOException;

public class LoginService extends IntentService {

    private Handler handler = new Handler();

    public final static String SIGN_IN_ACTION = "com.dreamgyf.action.SIGN_IN_ACTION";

    public LoginService() {
        super("LoginService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        String phone = intent.getStringExtra("phone");
        String password = intent.getStringExtra("password");
        try {
            final String accountId = ResponseProcessing.get().signIn(phone,password);
            if(accountId.length() == 3){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (accountId){
                            case "400":
                                Toast.makeText(LoginService.this,"未知错误",Toast.LENGTH_LONG).show();
                                break;
                            case "501":
                                Toast.makeText(LoginService.this,"用户不存在",Toast.LENGTH_LONG).show();
                                break;
                            case "502":
                                Toast.makeText(LoginService.this,"密码错误",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            } else {
                MainActivity.accountId = accountId;
                Intent signIn = new Intent(LoginService.this,MainActivity.class);
                signIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LoginService.this.startActivity(signIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
