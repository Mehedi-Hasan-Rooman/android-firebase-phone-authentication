package com.example.phone_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register_Activity extends AppCompatActivity {


    private static final String TAG ="Register" ;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    EditText PhoneNumber, CodeEnter;
    Button Next_Button;
    ProgressBar progressBar;
    TextView stateText;
    CountryCodePicker codePicker;
    String VarificationId;
    PhoneAuthProvider.ForceResendingToken Token;
    Boolean verificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        //Variable initialization

        firestore =FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        PhoneNumber = findViewById(R.id.phone);
        CodeEnter = findViewById(R.id.codeEnter);
        Next_Button = findViewById(R.id.nextBtn);
        progressBar = findViewById(R.id.progressBar);
        stateText = findViewById(R.id.state);
        codePicker = findViewById(R.id.ccp);



        Next_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!verificationInProgress){

                   if (!PhoneNumber.getText().toString().isEmpty() && PhoneNumber.getText().toString().length() ==10){

                       String Phonenum = codePicker.getSelectedCountryCodeWithPlus()+PhoneNumber.getText().toString();
                       Log.d(TAG, "Phone Number:> " +PhoneNumber);
                       Toast.makeText(Register_Activity.this, "OK", Toast.LENGTH_SHORT).show();
                       progressBar.setVisibility(View.VISIBLE);
                       stateText.setText("Sending OTP");
                       stateText.setVisibility(View.VISIBLE);
                       RequestOTP(Phonenum);

                   }else {
                       PhoneNumber.setError("Phone Number Not Valid");
                   }
               }else {

                   String userOTP = CodeEnter.getText().toString();
                   if (!userOTP.isEmpty() && userOTP.length() == 6){

                       PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VarificationId,userOTP);

                       VerifyAuth(credential);

                   }else {
                       CodeEnter.setError("Please Valid OTP");
                   }

               }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if ( auth.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            stateText.setText("Checking......");
            stateText.setVisibility(View.VISIBLE);
            CheckUserProfile();
        }
    }

    private void VerifyAuth(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    CheckUserProfile();

                }else {
                    Toast.makeText(Register_Activity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void CheckUserProfile(){
        DocumentReference documentRef = firestore.collection("User").document(auth.getCurrentUser().getUid());
        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),Profile.class));
                    finish();

                }else {
                    startActivity(new Intent(getApplicationContext(),AddDetailes.class));
                    finish();
                }

            }
        });
    }

    private void RequestOTP(String phonenum) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                CodeEnter.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                stateText.setVisibility(View.GONE);

                VarificationId =s;
                Token = forceResendingToken;
                Next_Button.setText("Verify");
                verificationInProgress = true;




            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                Toast.makeText(Register_Activity.this, "OTP Timeout Please Request again ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                VerifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(Register_Activity.this, "Account Can,t create" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
