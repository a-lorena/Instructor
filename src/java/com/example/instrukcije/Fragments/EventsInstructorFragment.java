package com.example.instrukcije.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instrukcije.Adapters.EventAdapter;
import com.example.instrukcije.Models.EventModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventsInstructorFragment extends Fragment {

    RecyclerView eventsRecycler;

    FirebaseFirestore firestore;
    FirebaseUser user;
    String userID;
    DocumentReference userDocRef;
    CollectionReference eventsColl;

    EventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        eventsRecycler = view.findViewById(R.id.eventsRecycler);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDocRef = firestore.collection("Users").document(userID);
        eventsColl = firestore.collection("Events");

        setupRecycler();

        return view;
    }

    private void setupRecycler() {
        Query query = eventsColl.whereEqualTo("teacherID", userID);

        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();

        adapter = new EventAdapter(getContext(), options);

        eventsRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        eventsRecycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();
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