package com.example.friendshipforever.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.friendshipforever.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding otpBinding;
    FirebaseAuth auth;
    String otpid;
    String phoneNumber;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding= ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(otpBinding.getRoot());

        getSupportActionBar().hide();
        otpBinding.otpView.requestFocus();
        auth=FirebaseAuth.getInstance();
        phoneNumber=getIntent().getStringExtra("phoneNumber");

        otpBinding.phoneLbl.setText("Verify "+phoneNumber);

        InputMethodManager imm=(InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        otpBinding.otpView.requestFocus();

        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending otp...");
        dialog.setCancelable(false);
        dialog.show();initiateotp();

        otpBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(otpBinding.otpView.getText().toString().isEmpty()) {
                        otpBinding.otpView.setError("Fill in the blank");
                    Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(otpBinding.otpView.getText().toString().length()!=6) {
                    otpBinding.otpView.setError("Incorrect otp");
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(otpid,otpBinding.otpView.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });
    }

    private void initiateotp()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
                    {
                        otpid=s;
                        dialog.dismiss();
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
                    {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(OtpActivity.this,UserProfileActivity.class));
                            finishAffinity();
                           // Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(),"Signing Code Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}