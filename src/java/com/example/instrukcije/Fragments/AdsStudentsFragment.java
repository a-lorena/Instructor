package com.example.instrukcije.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.Adapters.PostAdapterStudent;
import com.example.instrukcije.Models.PostModel;
import com.example.instrukcije.NewPostActivity;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdsStudentsFragment extends Fragment {

    ImageView newPost;
    ConstraintLayout header;
    RecyclerView recyclerView;

    FirebaseFirestore firestore;
    FirebaseUser user;
    CollectionReference collection;
    DocumentReference currentUserDocument;
    PostAdapterStudent adapter;
    String profileID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ads_student, container, false);

        newPost = view.findViewById(R.id.addNewPost);
        header = view.findViewById(R.id.header);
        recyclerView = view.findViewById(R.id.listOfPosts);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        profileID = user.getUid();
        collection = firestore.collection("Posts");
        currentUserDocument = firestore.collection("Users").document(profileID);

        newPost.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), NewPostActivity.class)));

        return view;
    }

    private void setUpRecyclerView() {
        Query query = collection.whereEqualTo("publisher", profileID);

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        adapter = new PostAdapterStudent(getContext(), options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpRecyclerView();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}