package com.example.instrukcije.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.instrukcije.Adapters.RatingAdapter;
import com.example.instrukcije.Models.RatingModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RatingsFragment extends Fragment {

    Spinner filterByRating, filterByOrder;
    ImageButton filterButton;
    RecyclerView notifsRecycler;

    FirebaseFirestore firestore;
    String userID;
    DocumentReference userDoc;
    CollectionReference ratingColl;

    Query query;
    FirestoreRecyclerOptions<RatingModel> options;
    RatingAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);

        filterByRating = view.findViewById(R.id.filterByRating);
        filterByOrder = view.findViewById(R.id.filterByOrder);
        filterButton = view.findViewById(R.id.filterButton);
        notifsRecycler = view.findViewById(R.id.notifsRecycler);

        firestore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ratingColl = firestore.collection("Ratings");
        userDoc = firestore.collection("Users").document(userID);

        setupSpinners();
        setupRecycler();

        filterButton.setOnClickListener(v -> filterData());

        return view;
    }

    private void updateQuery(Query changedQuery) {
        options = new FirestoreRecyclerOptions.Builder<RatingModel>()
                .setQuery(changedQuery, RatingModel.class)
                .build();

        adapter.updateOptions(options);
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
                query = ratingColl.whereEqualTo("teacherID", userID).orderBy("stars", Query.Direction.DESCENDING);
                break;
            case "Ocijene uzlazno":
                query = ratingColl.whereEqualTo("teacherID", userID).orderBy("stars", Query.Direction.ASCENDING);
                break;
            case "Najnovije prvo":
                query = ratingColl.whereEqualTo("teacherID", userID).orderBy("date", Query.Direction.DESCENDING);
                break;
            case "Najstarije prvo":
                query = ratingColl.whereEqualTo("teacherID", userID).orderBy("date", Query.Direction.ASCENDING);
                break;
        }

        query = filter_rating(query, inputRating);

        updateQuery(query);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.choose_rating, android.R.layout.simple_spinner_item);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterByRating.setAdapter(ratingAdapter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.choose_date, android.R.layout.simple_spinner_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterByOrder.setAdapter(orderAdapter);
    }

    private void setupRecycler() {
        query = ratingColl.whereEqualTo("teacherID", userID);

        options = new FirestoreRecyclerOptions.Builder<RatingModel>()
                .setQuery(query, RatingModel.class)
                .build();

        adapter = new RatingAdapter(options);

        notifsRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        notifsRecycler.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}