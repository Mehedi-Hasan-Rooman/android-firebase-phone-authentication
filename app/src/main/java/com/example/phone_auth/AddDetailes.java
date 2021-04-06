package com.example.phone_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDetailes extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    EditText user_name,county_name,email;
    Button saveButton;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_detailes);
        user_name = findViewById(R.id.name_text);
        county_name = findViewById(R.id.counrty_text);
        email = findViewById(R.id.email_text);

        saveButton = findViewById(R.id.save_button);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentRef = firestore.collection("User").document(userId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!user_name.getText().toString().isEmpty() && !county_name.getText().toString().isEmpty() &&  !email.getText().toString().isEmpty()){

                    String name =user_name.getText().toString();
                    String county =county_name.getText().toString();
                    String useremail = email.getText().toString();

                    Map<String,Object> user_data = new HashMap<>();
                    user_data.put("name_user",name);
                    user_data.put("country_user",county);
                    user_data.put("email_user",useremail);

                    documentRef.set(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),Profile.class));
                                finish();
                            }else {
                                Toast.makeText(AddDetailes.this, "Data Not Saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(AddDetailes.this, "Please Provide All Data", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
