package com.example.instrukcije.Fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.instrukcije.Adapters.InstructorAdapter;
import com.example.instrukcije.Models.InstructorModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class InstructorsFragment extends Fragment {

    ConstraintLayout filterLayout;
    EditText fastSearch, filterBySubject, filterByCity;
    Spinner filterByRating, filterByOrder;
    CheckBox filterByAvailable;
    Button filterButton;
    ImageButton detailFilterOpenButton, detailFilterCloseButton;
    RecyclerView recyclerView;
    
    FirebaseFirestore firestore;
    CollectionReference collection;
    InstructorAdapter adapter;

    Query query;
    FirestoreRecyclerOptions<InstructorModel> options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructors, container, false);

        fastSearch = view.findViewById(R.id.fastSearch);
        filterLayout = view.findViewById(R.id.filterLayout);
        filterBySubject = view.findViewById(R.id.filterBySubject);
        filterByCity = view.findViewById(R.id.filterByCity);
        filterByRating = view.findViewById(R.id.filterByRating);
        filterByOrder = view.findViewById(R.id.filterByOrder);
        filterByAvailable = view.findViewById(R.id.filterByAvailable);
        filterButton = view.findViewById(R.id.filterButton);
        detailFilterOpenButton = view.findViewById(R.id.detailFilterOpenButton);
        detailFilterCloseButton = view.findViewById(R.id.detailFilterCloseButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        
        firestore = FirebaseFirestore.getInstance();
        collection = firestore.collection("Users");

        setupSpinners();
        setupRecyclerView();

        fastSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    query = collection.whereEqualTo("userType", "Instruktor")
                                        .orderBy("subjects", Query.Direction.ASCENDING);
                } else {
                    query = collection.whereEqualTo("userType", "Instruktor")
                            .whereArrayContains("tags", editable.toString())
                            .orderBy("rating", Query.Direction.DESCENDING);
                }

                options = new FirestoreRecyclerOptions.Builder<InstructorModel>()
                        .setQuery(query, InstructorModel.class)
                        .build();

                adapter.updateOptions(options);
            }
        });

        detailFilterOpenButton.setOnClickListener(view1 -> openDetailFilter());

        detailFilterCloseButton.setOnClickListener(view12 -> closeDetailFilter());

        filterButton.setOnClickListener(view13 -> filterData());
        
        return view;
    }

    private void closeDetailFilter() {
        fastSearch.setVisibility(View.VISIBLE);
        filterLayout.setVisibility(View.GONE);
        detailFilterCloseButton.setVisibility(View.GONE);
        detailFilterOpenButton.setVisibility(View.VISIBLE);
    }

    private void openDetailFilter () {
        fastSearch.setVisibility(View.GONE);
        filterLayout.setVisibility(View.VISIBLE);
        detailFilterCloseButton.setVisibility(View.VISIBLE);
        detailFilterOpenButton.setVisibility(View.GONE);
    }

    private void updateQuery(Query changedQuery) {
        options = new FirestoreRecyclerOptions.Builder<InstructorModel>()
                .setQuery(changedQuery, InstructorModel.class)
                .build();

        adapter.updateOptions(options);
    }

    private Query filter_available(Query newQuery, boolean available) {
        if (available) {
            return newQuery.whereEqualTo("available", true);
        }

        return newQuery;
    }

    private Query filter_rating(Query newQuery, String rating) {
        if (rating.equals("Sve ocijene")) {
            return newQuery;
        }

        return newQuery.whereEqualTo("rating", Long.parseLong(rating));
    }

    private Query filter_subject(Query newQuery, String subject) {
        if (subject.isEmpty()) {
            return newQuery;
        }

        return newQuery.whereArrayContains("tags", subject);
    }

    private Query filter_city(Query newQuery, String city) {
        if (city.isEmpty()) {
            return newQuery;
        }

        return newQuery.whereEqualTo("city", city);
    }

    private void filterData() {
        String inputSubject = filterBySubject.getText().toString();
        String inputCity = filterByCity.getText().toString();
        boolean inputAvailable = filterByAvailable.isChecked();
        String inputRating = filterByRating.getSelectedItem().toString();
        String inputOrder = filterByOrder.getSelectedItem().toString();

        switch (inputOrder) {
            case "Ocijene silazno":
                query = collection.orderBy("rating", Query.Direction.DESCENDING);
                break;
            case "Ocijene uzlazno":
                query = collection.orderBy("rating", Query.Direction.ASCENDING);
                break;
            case "Cijena silazno":
                query = collection.orderBy("price", Query.Direction.DESCENDING);
                break;
            case "Cijena uzlazno":
                query = collection.orderBy("price", Query.Direction.ASCENDING);
                break;
        }

        query = filter_available(query, inputAvailable);
        query = filter_rating(query, inputRating);
        query = filter_subject(query, inputSubject);
        query = filter_city(query, inputCity);

        updateQuery(query);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.choose_rating, android.R.layout.simple_spinner_item);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterByRating.setAdapter(ratingAdapter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.choose_order, android.R.layout.simple_spinner_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterByOrder.setAdapter(orderAdapter);
    }

    private void setupRecyclerView() {
        query = collection.whereEqualTo("userType", "Instruktor");

        options = new FirestoreRecyclerOptions.Builder<InstructorModel>()
                .setQuery(query, InstructorModel.class)
                .build();

        adapter = new InstructorAdapter(getContext(), options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
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