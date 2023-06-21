package com.example.instrukcije;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, email, password, repeatPassword;
    RadioGroup radioGroup;
    RadioButton userType;
    Button register;
    ConstraintLayout registrationLayout;

    FirebaseFirestore db;
    FirebaseAuth auth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = (EditText)findViewById(R.id.RegisterFullName);
        email = (EditText)findViewById(R.id.RegisterEmail);
        password = (EditText)findViewById(R.id.RegisterPassword);
        repeatPassword = (EditText)findViewById(R.id.RepeatPassword);
        register = (Button)findViewById(R.id.RegisterButton);
        radioGroup = (RadioGroup) findViewById(R.id.userType);
        registrationLayout = (ConstraintLayout)findViewById(R.id.registrationLayout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);

        register.setOnClickListener(view -> {
            String txtFullName = fullName.getText().toString();
            String txtEmail = email.getText().toString();
            String txtPassword = password.getText().toString();
            String txtRepeatPassword = repeatPassword.getText().toString();
            String txtUserType = checkUserType(view);

            if (check_fields(txtFullName, txtEmail, txtPassword, txtRepeatPassword, txtUserType)) {
                registerUser(txtFullName, txtEmail, txtPassword, txtUserType);
            }
        });
    }

    public String checkUserType(View v){
        int radioID = radioGroup.getCheckedRadioButtonId();
        userType = findViewById(radioID);

        return userType.getText().toString();
    }

    boolean check_fields(String fullName, String email, String password, String repeatPassword, String userType) {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || userType.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Unesite sve podatke!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(repeatPassword)) {
            Toast.makeText(RegisterActivity.this, "Lozinka nije jednaka!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Lozinka mora imati najmanje 6 znakova!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser(String fullName, String email, String password, String userType) {
        pd.setMessage("Molim Vas, pričekajte...");
        pd.show();

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {

            HashMap<String, Object> user = new HashMap<>();
            user.put("id", auth.getCurrentUser().getUid());
            user.put("fullName", fullName);
            user.put("email", email);
            user.put("education", "");
            user.put("userType", userType);

            if (userType.equals("Instruktor")) {
                user.put("available", true);
                user.put("subjects", "");
                user.put("price", 0);
                user.put("stars", 0);
                user.put("rating", 0);
                user.put("comments", 0);
            }

            db.collection("Users").document(auth.getCurrentUser().getUid()).set(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registracija uspješna!\nMolimo Vas, ispunite profil!", Toast.LENGTH_LONG).show();

                    if (userType.equals("Instruktor")) {
                        startActivity(new Intent(RegisterActivity.this, MainActivityInstructors.class));
                    } else {
                        startActivity(new Intent(RegisterActivity.this, MainActivityStudents.class));
                    }

                    finish();
                }
            });
        }).addOnFailureListener(e -> {
            pd.dismiss();

            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}