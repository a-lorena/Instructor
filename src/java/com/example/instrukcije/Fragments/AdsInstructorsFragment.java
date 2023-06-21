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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.instrukcije.Adapters.PostAdapterInstructor;
import com.example.instrukcije.Models.PostModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdsInstructorsFragment extends Fragment {

    EditText fastSearch, filterBySubject, filterByCity;
    ConstraintLayout filterLayout;
    Button filterButton;
    ImageButton detailFilterOpenButton, detailFilterCloseButton;

    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String userID;
    CollectionReference collection;
    DocumentReference currentUserDocument;

    PostAdapterInstructor adapter;
    Query query;
    FirestoreRecyclerOptions<PostModel> options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ads_instructor, container, false);

        fastSearch = view.findViewById(R.id.searchAds);
        filterLayout = view.findViewById(R.id.filterLayout);
        filterBySubject = view.findViewById(R.id.filterBySubject);
        filterByCity = view.findViewById(R.id.filterByCity);
        filterButton = view.findViewById(R.id.filterButton);
        detailFilterOpenButton = view.findViewById(R.id.detailFilterOpenButton);
        detailFilterCloseButton = view.findViewById(R.id.detailFilterCloseButton);
        recyclerView = view.findViewById(R.id.listOfPosts);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        collection = firestore.collection("Posts");
        currentUserDocument = firestore.collection("Users").document(userID);

        setUpRecyclerView();

        fastSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    query = collection.orderBy("title", Query.Direction.ASCENDING);
                } else {
                    query = collection.whereEqualTo("title", editable.toString());
                }

                options = new FirestoreRecyclerOptions.Builder<PostModel>()
                        .setQuery(query, PostModel.class)
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
        detailFilterOpenButton.setVisibility(View.VISIBLE);
    }

    private void openDetailFilter () {
        fastSearch.setVisibility(View.GONE);
        filterLayout.setVisibility(View.VISIBLE);
        detailFilterOpenButton.setVisibility(View.GONE);
    }

    private void updateQuery(Query changedQuery) {
        options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(changedQuery, PostModel.class)
                .build();

        adapter.updateOptions(options);
    }

    private Query filter_subject(Query newQuery, String subject) {
        if (subject.isEmpty()) {
            return newQuery;
        }

        return newQuery.whereEqualTo("title", subject);
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

        query = collection.orderBy("title", Query.Direction.ASCENDING);

        query = filter_subject(query, inputSubject);
        query = filter_city(query, inputCity);

        updateQuery(query);
    }

    private void setUpRecyclerView() {
        query = collection;

        options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        adapter = new PostAdapterInstructor(getContext(), options);

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