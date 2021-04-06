package com.example.phone_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    TextView pro_name,pro_email,pro_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pro_name = findViewById(R.id.profileFullName);
        pro_email=findViewById(R.id.profileEmail);
        pro_phone=findViewById(R.id.profilePhone);



        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User *_* Profile");



        DocumentReference documentRef = firestore.collection("User").document(auth.getCurrentUser().getUid());

        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()){
                    pro_name.setText(documentSnapshot.getString("name_user"));
                    pro_email.setText(documentSnapshot.getString("email_user"));
                    pro_phone.setText(auth.getCurrentUser().getPhoneNumber());
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

      switch (item.getItemId()){



          case R.id.home_button:
              Intent intent = new Intent(getApplicationContext(),MainActivity.class);
              startActivity(intent);
              finish();
             Toast.makeText(this, "Home Button Click", Toast.LENGTH_SHORT).show();
             return true;


          case R.id.logout_button:
              FirebaseAuth.getInstance().signOut();
              startActivity(new Intent(getApplicationContext(),Register_Activity.class));
              finish();
              Toast.makeText(this, "LogoutButton Clicked", Toast.LENGTH_SHORT).show();
           return true;

      }
        return super.onOptionsItemSelected(item);
    }


}
