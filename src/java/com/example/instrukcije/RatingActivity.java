package com.example.instrukcije;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.instrukcije.Adapters.RatingAdapter;
import com.example.instrukcije.Models.RatingModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;

public class RatingActivity extends AppCompatActivity {

    Toolbar ratingHeader;
    Spinner filterByRating, filterByOrder;
    ImageButton filterButton;
    RecyclerView ratingRecyclerView;
    FloatingActionButton newRating;

    FirebaseFirestore firestore;
    DocumentReference docRefTecher, docRefStudent;
    CollectionReference ratingsCollection;
    RatingAdapter ratingAdapter;

    String teacherID, studentID, teacherFullName, studentFullName;
    long teacherComments, teacherStars;
    long teacherRating;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    RatingBar ratingBar;
    EditText ratingComment;
    Button cancelRatingButton, saveRatingButton;

    ProgressDialog pd;

    Query query;
    FirestoreRecyclerOptions<RatingModel> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingHeader = findViewById(R.id.ratingHeader);
        filterByRating = (Spinner)findViewById(R.id.filterByRating);
        filterByOrder = (Spinner) findViewById(R.id.filterByOrder);
        filterButton = (ImageButton) findViewById(R.id.filterButton);
        ratingRecyclerView = findViewById(R.id.ratingRecycler);
        newRating = (FloatingActionButton) findViewById(R.id.addRatingButton);

        firestore = FirebaseFirestore.getInstance();
        studentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        teacherID = getIntent().getStringExtra("teacherID");

        docRefStudent = firestore.collection("Users").document(studentID);
        docRefTecher = firestore.collection("Users").document(teacherID);
        ratingsCollection = firestore.collection("Ratings");

        String showButton = getIntent().getStringExtra("showButton");
        if (showButton != null) newRating.setVisibility(View.VISIBLE);

        pd = new ProgressDialog(this);

        getTeacherInfo();
        getStudentInfo();
        setupSpinners();
        setupRecyclerView();

        filterButton.setOnClickListener(view -> filterData());

        newRating.setOnClickListener(view -> showRatingDialog());
    }

    private void updateTeacher() {
        docRefTecher.update(
                "stars", teacherStars,
                "rating", teacherRating,
                "comments", teacherComments
        );
    }

    private void saveRating(String comment, long stars) {
        pd.setMessage("Objavljivanje...");
        pd.show();

        HashMap<String, Object> rating = new HashMap<>();

        rating.put("ratingID", "");
        rating.put("teacherID", teacherID);
        rating.put("teacherFullName", teacherFullName);
        rating.put("studentID", studentID);
        rating.put("studentFullName", studentFullName);
        rating.put("comment", comment);
        rating.put("stars", stars);
        rating.put("date", new Date());

        firestore.collection("Ratings").add(rating).addOnSuccessListener(documentReference -> {
            String ratingID = documentReference.getId();
            documentReference.update("ratingID", ratingID);

            updateTeacher();

            pd.dismiss();
            dialog.dismiss();
            Toast.makeText(RatingActivity.this, "Ocjena spremljena.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            dialog.dismiss();
            Toast.makeText(RatingActivity.this, "Spremanje nije uspjelo.", Toast.LENGTH_SHORT).show();
        });
    }
    
    private boolean checkEmptyFields(String comment, long rating) {
        if (comment.isEmpty() || rating==0) {
            Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void showRatingDialog() {
        builder = new AlertDialog.Builder(RatingActivity.this);
        View alertView = getLayoutInflater().inflate(R.layout.dialog_rating, null);

        ratingBar = (RatingBar) alertView.findViewById(R.id.ratingBar);
        ratingComment = (EditText) alertView.findViewById(R.id.ratingComment);
        cancelRatingButton = (Button) alertView.findViewById(R.id.cancelRatingButton);
        saveRatingButton = (Button) alertView.findViewById(R.id.saveRatingButton);

        cancelRatingButton.setOnClickListener(view -> dialog.dismiss());

        saveRatingButton.setOnClickListener(view -> {
            String txtRatingComment = ratingComment.getText().toString();
            long txtRatingBar = (long)ratingBar.getRating();

            if (checkEmptyFields(txtRatingComment, txtRatingBar)) {
                teacherComments++;
                teacherStars += txtRatingBar;
                teacherRating = Math.round(teacherStars / teacherComments);

                saveRating(txtRatingComment, txtRatingBar);
            }
        });

        builder.setView(alertView);
        dialog = builder.create();
        dialog.show();
    }

    private void updateQuery(Query changedQuery) {
        options = new FirestoreRecyclerOptions.Builder<RatingModel>()
                .setQuery(changedQuery, RatingModel.class)
                .build();

        ratingAdapter.updateOptions(options);
    }

    private Query filter_rating(Query newQuery, String rating) {
        if (rating.equals("Sve ocijene")) {
            return newQuery;
        }

        return newQuery.whereEqualTo("stars", Long.parseLong(rating));
    }

    private void filterData() {
        String inputRating = filterByRating.getSelectedItem().toString();
        String inputOrder = filterByOrder.getSelectedItem().toString();

        switch (inputOrder) {
            case "Ocijene silazno":
                query = ratingsCollection.whereEqualTo("teacherID", teacherID).orderBy("stars", Query.Direction.DESCENDING);
                break;
            case "Ocijene uzlazno":
                query = ratingsCollection.whereEqualTo("teacherID", teacherID).orderBy("stars", Query.Direction.ASCENDING);
                break;
            case "Najnovije prvo":
                query = ratingsCollection.whereEqualTo("teacherID", teacherID).orderBy("date", Query.Direction.DESCENDING);
                break;
            case "Najstarije prvo":
                query = ratingsCollection.whereEqualTo("teacherID", teacherID).orderBy("date", Query.Direction.ASCENDING);
                break;
        }

        query = filter_rating(query, inputRating);

        updateQuery(query);
    }

    private void setupRecyclerView() {
        query = ratingsCollection.whereEqualTo("teacherID", teacherID);

        options = new FirestoreRecyclerOptions.Builder<RatingModel>()
                .setQuery(query, RatingModel.class)
                .build();

        ratingAdapter = new RatingAdapter(options);

        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRecyclerView.setAdapter(ratingAdapter);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(RatingActivity.this,
                R.array.choose_rating, android.R.layout.simple_spinner_item);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterByRating.setAdapter(ratingAdapter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(RatingActivity.this,
                R.array.choose_date, android.R.layout.simple_spinner_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterByOrder.setAdapter(orderAdapter);
    }

    private void getStudentInfo() {
        docRefStudent.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                studentFullName = task.getResult().getString("fullName");
            }
        });
    }

    private void getTeacherInfo() {
        docRefTecher.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                teacherFullName = task.getResult().getString("fullName");
                teacherComments = task.getResult().getLong("comments");
                teacherStars = task.getResult().getLong("stars");
                teacherRating = task.getResult().getLong("rating");

                ratingHeader.setTitle(teacherFullName);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ratingAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ratingAdapter.stopListening();
    }
}