package com.example.instrukcije;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView registration;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.InputEmail);
        password = findViewById(R.id.InputPassword);
        login = findViewById(R.id.LoginButton);
        registration = findViewById(R.id.RegisterMe);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(view -> {
            String txt_email = email.getText().toString();
            String txt_pass = password.getText().toString();

            if (check_empty_fields(txt_email, txt_pass)) {
                loginUser(txt_email, txt_pass);
            }
        });

        registration.setOnClickListener(view -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }

    boolean check_empty_fields(String email, String pass) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Unesite sve podatke!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
            Toast.makeText(LoginActivity.this, "Prijava uspješna.", Toast.LENGTH_SHORT).show();
            checkUserType(authResult.getUser().getUid());
            finish();
        });
    }

    private void checkUserType(String uid) {
        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(uid);

        df.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getString("userType").equals("Instruktor")) {
                startActivity(new Intent(LoginActivity.this, MainActivityInstructors.class));
            } else if (documentSnapshot.getString("userType").equals("Učenik")) {
                startActivity(new Intent(LoginActivity.this, MainActivityStudents.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            startActivity(new Intent(LoginActivity.this, LoadUserActivity.class));

        }
    }
}