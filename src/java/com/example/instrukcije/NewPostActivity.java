package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class NewPostActivity extends AppCompatActivity {

    EditText title, description;
    Button post;
    String userFullName, userCity;
    RadioGroup radioGroup;
    RadioButton level;

    FirebaseFirestore firestore;
    String userID;
    DocumentReference documentUser;
    ProgressDialog pd;

    String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        title = (EditText)findViewById(R.id.inputTitle);
        description = (EditText)findViewById(R.id.inputDescription);
        post = (Button)findViewById(R.id.postButton);
        radioGroup = (RadioGroup) findViewById(R.id.adLevel);

        firestore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        documentUser = firestore.collection("Users").document(userID);

        pd = new ProgressDialog(this);

        getUsername();

        post.setOnClickListener(view -> {
            String txtTitle = title.getText().toString();
            String txtDescription = description.getText().toString();
            String txtLevel = checkLevel(view);

            if (check_empty_fields(txtTitle, txtDescription, txtLevel)) {
                post(txtTitle, txtDescription, txtLevel);
            } else {
                Toast.makeText(NewPostActivity.this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public String checkLevel(View v){
        int radioID = radioGroup.getCheckedRadioButtonId();
        level = findViewById(radioID);

        return level.getText().toString();
    }

    private void getUsername() {
        documentUser.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                userFullName = task.getResult().getString("fullName");
                userCity = task.getResult().getString("city");
            } else {
                Toast.makeText(NewPostActivity.this, "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean check_empty_fields(String title, String description, String level) {
        if (title.isEmpty() || description.isEmpty() || level.isEmpty()) {
            return false;
        }

        return true;
    }

    private void post(String title, String description, String level) {
        pd.setMessage("Objavljivanje...");
        pd.show();

        HashMap<String, Object> post = new HashMap<>();
        post.put("postID", "");
        post.put("title", title);
        post.put("description", description);
        post.put("username", userFullName);
        post.put("publisher", userID);
        post.put("city", userCity);
        post.put("level", level);

        firestore.collection("Posts").add(post).addOnSuccessListener(documentReference -> {
            postID = documentReference.getId();
            documentReference.update("postID", postID);
            pd.dismiss();
            Toast.makeText(NewPostActivity.this, "Objavljeno!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(NewPostActivity.this, MainActivityStudents.class));
            finish();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(NewPostActivity.this, "Objavljivanje nije uspjelo!", Toast.LENGTH_SHORT).show();
        });
    }
}