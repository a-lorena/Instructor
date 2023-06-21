package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    EditText fullName, email, education, subjects, price;
    Switch available;
    String userType;
    Button saveButton, deactivateButton;
    ImageButton theme;
    TextView location, checkText;
    ImageButton mapButton;
    CheckBox osnovna, srednja, fakultet;

    FirebaseFirestore firestore;
    FirebaseUser user;
    String profileID;
    CollectionReference postsRef;
    DocumentReference docRefUsers;

    ProgressDialog pd;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fullName = (EditText)findViewById(R.id.editFullName);
        email = (EditText)findViewById(R.id.editEmail);
        location = (TextView) findViewById(R.id.editLocation);
        education = (EditText)findViewById(R.id.editEducation);
        subjects = (EditText)findViewById(R.id.editSubjects);
        price = (EditText)findViewById(R.id.editPrice);
        available = (Switch)findViewById(R.id.availableChange);
        saveButton = (Button)findViewById(R.id.saveChangesButton);
        deactivateButton = (Button)findViewById(R.id.deactivateUserButton);
        mapButton = findViewById(R.id.openMap);
        checkText = findViewById(R.id.checkText);
        osnovna = findViewById(R.id.osnovna);
        srednja = findViewById(R.id.srednja);
        fakultet = findViewById(R.id.fakultet);

        theme = findViewById(R.id.changeTheme);

        SharedPreferences appSettings = getSharedPreferences("AppSettingPrefs", 0);
        SharedPreferences.Editor sharedPrefsEdit = appSettings.edit();
        Boolean isNigthModeOn = appSettings.getBoolean("NightMode", false);

        if (isNigthModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        theme.setOnClickListener(view -> {
            if (isNigthModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPrefsEdit.putBoolean("NightMode", false);
                sharedPrefsEdit.apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPrefsEdit.putBoolean("NightMode", true);
                sharedPrefsEdit.apply();
            }

        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        profileID = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        docRefUsers = firestore.collection("Users").document(profileID);
        postsRef = firestore.collection("Posts");

        pd = new ProgressDialog(this);

        userInformation();
        
        saveButton.setOnClickListener(view -> {
            String txtFullName = fullName.getText().toString();
            String txtEmail = email.getText().toString();
            String txtEducation = education.getText().toString();

            String txtTags = subjects.getText().toString();
            String[] tagArray = txtTags.split("\\s*,\\s*");
            List<String> subjectTags = Arrays.asList(tagArray);

            List<String> levels = new ArrayList<>();

            if (osnovna.isChecked()) levels.add(osnovna.getText().toString());
            if (srednja.isChecked()) levels.add(srednja.getText().toString());
            if (fakultet.isChecked()) levels.add(fakultet.getText().toString());

            if (userType.equals("Instruktor")) {
                String txtSubjects = subjects.getText().toString();
                String txtPrice = price.getText().toString();
                Boolean isAvailable = available.isChecked();
                if (check_empty_fields_teacher(txtFullName, txtEmail, txtEducation, txtSubjects, txtPrice, levels)) {
                    long valuePrice = Long.parseLong(txtPrice);
                    updateProfileTeacher(txtFullName, txtEmail, txtEducation, txtSubjects,
                            subjectTags, valuePrice, isAvailable, levels);
                }
            } else {
                if (check_empty_fields_student(txtFullName, txtEmail, txtEducation, levels)) {
                    updateProfileStudent(txtFullName, txtEmail, txtEducation);
                }
            }
        });
        
        deactivateButton.setOnClickListener(view -> openDialog());

        mapButton.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, MapsActivity.class)));
    }

    private void deactivateUser() {

        docRefUsers.delete().addOnSuccessListener(unused -> {
            Toast.makeText(SettingsActivity.this, "Korisnik je deaktiviran.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Nismo uspjeli deaktivirati račun! Molimo Vas pokušajte kasnije.", Toast.LENGTH_SHORT).show());

        FirebaseAuth.getInstance().getCurrentUser().delete();
    }

    private void openDialog() {
        alert = new AlertDialog.Builder(this);

        alert.setTitle("Pozor!")
                .setMessage("Jeste li sigurni da želite deaktivirati račun?")
                .setPositiveButton("Deaktiviraj", (dialogInterface, i) -> {
                    postsRef.get().addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docID = document.getId();
                            if (document.getString("publisher").equals(profileID)) {
                                postsRef.document(docID).delete();
                            }
                        }
                    });
                    deactivateUser();
                })
                .setNegativeButton("Odustani", (dialogInterface, i) -> {
                });

        alert.create().show();
    }

    private void userInformation() {
        docRefUsers.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                fullName.setText(task.getResult().getString("fullName"), TextView.BufferType.EDITABLE);
                email.setText(user.getEmail(), TextView.BufferType.EDITABLE);
                location.setText(task.getResult().getString("location"));
                education.setText(task.getResult().getString("education"), TextView.BufferType.EDITABLE);

                userType = task.getResult().getString("userType");

                if (userType.equals("Instruktor")) {
                    subjects.setVisibility(View.VISIBLE);
                    available.setVisibility(View.VISIBLE);
                    price.setVisibility(View.VISIBLE);
                    checkText.setVisibility(View.VISIBLE);
                    osnovna.setVisibility(View.VISIBLE);
                    srednja.setVisibility(View.VISIBLE);
                    fakultet.setVisibility(View.VISIBLE);

                    subjects.setText(task.getResult().getString("subjects"), TextView.BufferType.EDITABLE);
                    available.setChecked(task.getResult().getBoolean("available"));
                    price.setText(String.valueOf(task.getResult().getLong("price")), TextView.BufferType.EDITABLE);
                }
            }
        });
    }

    private boolean check_empty_fields_teacher(String fullName, String email, String education,
                                               String subjects, String price, List<String> levels) {
        if (fullName.isEmpty() || email.isEmpty() || education.isEmpty() || subjects.isEmpty() ||
                price.isEmpty() || levels == null) {
            Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean check_empty_fields_student(String fullName, String email, String education, List<String> levels) {
        if (fullName.isEmpty() || email.isEmpty() || education.isEmpty() || levels == null) {
            Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateProfileTeacher(String fullName, String email, String education,
                                      String subjects, List<String> tags, long price,
                                      Boolean available, List<String> levels) {
        pd.setMessage("Spremanje podataka...");
        pd.show();

        user.updateEmail(email);

        docRefUsers.update(
                "fullName", fullName,
                "email", email,
                "education", education,
                "subjects", subjects,
                "price", price,
                "available", available,
                "tags", tags,
                "levels", levels
        ).addOnSuccessListener(unused -> {
            pd.dismiss();

            Toast.makeText(SettingsActivity.this, "Podaci spremljeni!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, MainActivityInstructors.class));
            finish();
        }).addOnFailureListener(e -> {
            pd.dismiss();

            Toast.makeText(SettingsActivity.this, "Spremanje podataka neuspješno!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileStudent(String fullName, String email, String education) {
        pd.setMessage("Spremanje podataka...");
        pd.show();

        user.updateEmail(email);

        docRefUsers.update(
                "fullName", fullName,
                "email", email,
                "education", education
        ).addOnSuccessListener(unused -> {
            pd.dismiss();

            Toast.makeText(SettingsActivity.this, "Podaci spremljeni.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, MainActivityStudents.class));
            finish();
        }).addOnFailureListener(e -> {
            pd.dismiss();

            Toast.makeText(SettingsActivity.this, "Spremanje podataka nije uspjelo.", Toast.LENGTH_SHORT).show();
        });
    }
}