package com.example.friendshipforever.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.friendshipforever.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;


public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(PhoneNumberActivity.this,MainActivity.class));
            finish();
        }
        binding.ccp.registerCarrierNumberEditText(binding.phoneBox);
        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.phoneBox.getText().toString().isEmpty()) {
                    binding.phoneBox.setError("Fill in the blank");
                    return;
                }
//                else if(binding.phoneBox.getText().toString().length()!=11) {
//                    binding.phoneBox.setError("Incorrect phone number");
//                    return;
//                }
                else {
                    Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);
                    intent.putExtra("phoneNumber", binding.ccp.getFullNumberWithPlus().replace(" ", ""));
                    startActivity(intent);
                }
            }
        });
    }
}