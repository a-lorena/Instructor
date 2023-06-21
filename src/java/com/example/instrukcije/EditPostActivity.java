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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPostActivity extends AppCompatActivity {

    EditText editTitle, editDescription;
    Button saveEditButton;
    RadioGroup radioGroup;
    RadioButton level, osnovna, srednja, fakultet;

    String userID, postID;
    FirebaseFirestore firestore;
    DocumentReference docRefPost;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editTitle = findViewById(R.id.editPostTitle);
        editDescription = findViewById(R.id.editPostDescription);
        saveEditButton = findViewById(R.id.savePostEdit);
        radioGroup = findViewById(R.id.adLevel);
        osnovna = findViewById(R.id.osnovnaRadio);
        srednja = findViewById(R.id.srednjaRadio);
        fakultet = findViewById(R.id.fakultetRadio);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();

        postID = getIntent().getStringExtra("postID");
        docRefPost = firestore.collection("Posts").document(postID);

        pd = new ProgressDialog(this);

        getPostInfo();

        saveEditButton.setOnClickListener(view -> {
            String txtTitle = editTitle.getText().toString();
            String txtDescription = editDescription.getText().toString();
            String txtLevel = checkLevel(view);

            if (checkEmptyFields(txtTitle, txtDescription, txtLevel)) {
                savePostEdit(txtTitle, txtDescription, txtLevel);
            } else {
                Toast.makeText(EditPostActivity.this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String checkLevel(View v){
        int radioID = radioGroup.getCheckedRadioButtonId();
        level = findViewById(radioID);

        return level.getText().toString();
    }

    private void savePostEdit(String title, String description, String level) {
        pd.setMessage("Spremanje izmjena...");
        pd.show();

        docRefPost.update(
                "title", title,
                "description", description,
                "level", level
        ).addOnSuccessListener(unused -> {
            pd.dismiss();
            Toast.makeText(EditPostActivity.this, "Izmjene spremljene", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(EditPostActivity.this, "Spremanje izmjena neuspješno", Toast.LENGTH_SHORT).show();
        });

        startActivity(new Intent(EditPostActivity.this, MainActivityStudents.class));
        finish();
    }

    private boolean checkEmptyFields(String title, String description, String level) {
        if (title.isEmpty() || description.isEmpty() || level.isEmpty()) {
            return false;
        }

        return true;
    }

    private void getPostInfo() {
        docRefPost.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                editTitle.setText(task.getResult().getString("title"), TextView.BufferType.EDITABLE);
                editDescription.setText(task.getResult().getString("description"), TextView.BufferType.EDITABLE);

                String levelTxt = task.getResult().getString("level");

                switch (levelTxt) {
                    case "Osnovna škola":
                        osnovna.setChecked(true);
                        break;

                    case "Srednja škola":
                        srednja.setChecked(true);
                        break;

                    case "Fakultet":
                        fakultet.setChecked(true);
                        break;
                }
            }
        });
    }
}