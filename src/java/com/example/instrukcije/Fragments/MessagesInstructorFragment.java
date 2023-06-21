package com.example.instrukcije.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instrukcije.Adapters.ChatAdapter;
import com.example.instrukcije.ChatActivity;
import com.example.instrukcije.Models.ChatModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class MessagesInstructorFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton makeGroupButton;

    FirebaseFirestore firestore;
    String userID;
    DocumentReference userDoc;
    CollectionReference chatsColl, userChatsColl;

    ChatAdapter chatAdapter;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.chatsRecycler);
        makeGroupButton = view.findViewById(R.id.makeGroupChat);

        firestore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDoc = firestore.collection("Users").document(userID);
        chatsColl = firestore.collection("Chats");
        userChatsColl = firestore.collection("Users").document(userID).collection("UserChats");

        setupRecycler();
        
        makeGroupButton.setOnClickListener(view1 -> showNewGroupDialog());

        return view;
    }

    private void createGroup(String name) {
        HashMap<String, Object> group = new HashMap<>();

        group.put("groupName", name);
        group.put("teacherID", userID);
        group.put("isGroup", true);

        chatsColl.add(group).addOnSuccessListener(documentReference -> {

        });
    }

    private void showNewGroupDialog() {
        builder = new AlertDialog.Builder(getContext());
        View alertView = getLayoutInflater().inflate(R.layout.dialog_new_group, null);

        EditText name = alertView.findViewById(R.id.groupNameInput);

        builder.setView(alertView)
                .setTitle("Novi grupni razgovor")
                .setPositiveButton("Stvori", (dialogInterface, i) -> {
                    String txtName = name.getText().toString();

                    if (txtName.isEmpty()) {
                        Toast.makeText(getContext(), "Upišite naziv grupe!", Toast.LENGTH_SHORT).show();
                    } else {
                        createGroup(txtName);
                    }
                }).setNegativeButton("Odustani", (dialogInterface, i) -> dialog.dismiss());

        dialog = builder.create();
        dialog.show();
    }

    private void setupRecycler() {
        Query query = chatsColl.whereEqualTo("teacherID", userID);

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel.class)
                .build();

        chatAdapter = new ChatAdapter(getActivity(), options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(chatAdapter);

        chatAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
            //ChatModel Chat = documentSnapshot.toObject(ChatModel.class);
            String id = documentSnapshot.getId();
            boolean isGroup = documentSnapshot.getBoolean("isGroup");
            String receiverID = documentSnapshot.getString("studentID");

            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatID", id);
            intent.putExtra("receiverID", receiverID);

            if (isGroup) {
                //String groupName = documentSnapshot.getString("groupName");
            } else {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }
}