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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instrukcije.Adapters.ChatGroupAdapter;
import com.example.instrukcije.ChatGroupActivity;
import com.example.instrukcije.Models.ChatGroupModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class ChatsGroupFragment extends Fragment {

	String type;
	static final String STUDENT = "Učenik";
	static final String TEACHER = "Instruktor";

	public ChatsGroupFragment(String type) {
		this.type = type;
	}

	RecyclerView groupRecycler;
	FloatingActionButton makeGroupButton;

	FirebaseFirestore firestore;
	String userID;
	DocumentReference userDoc;
	CollectionReference groupChatsColl;

	ChatGroupAdapter groupAdapter;

	AlertDialog.Builder builder;
	AlertDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chats_group, container, false);

		groupRecycler = view.findViewById(R.id.groupRecycler);
		makeGroupButton = view.findViewById(R.id.makeGroupChat);
		if (type.equals(TEACHER)) makeGroupButton.setVisibility(View.VISIBLE);

		firestore = FirebaseFirestore.getInstance();
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		userDoc = firestore.collection("Users").document(userID);
		groupChatsColl = firestore.collection("GroupChats");

		setupRecycler(type);

		makeGroupButton.setOnClickListener(view1 -> newGroupDialog());

		return view;
	}

	private void createNewGroup(String name) {
		HashMap<String, Object> group = new HashMap<>();

		group.put("ownerID", userID);
		group.put("groupName", name);
		group.put("isGroup", true);

		firestore.collection("GroupChats").add(group).addOnSuccessListener(documentReference -> {
			String chatID = documentReference.getId();

			userDoc.get().addOnCompleteListener(task -> firestore.collection("GroupChats").document(chatID).update(
					"teacherName", task.getResult().getString("fullName")
			));

			dialog.dismiss();
			Toast.makeText(getContext(), "Grupa uspješno stvorena.", Toast.LENGTH_SHORT).show();
		});
	}

	private void newGroupDialog() {
		builder = new AlertDialog.Builder(getContext());
		View alertView = getLayoutInflater().inflate(R.layout.dialog_new_group, null);

		TextView name = alertView.findViewById(R.id.groupNameInput);
		ImageButton createButton = alertView.findViewById(R.id.createGroupButton);
		ImageButton cancelButton = alertView.findViewById(R.id.cancelGroupButton);

		builder.setTitle("Nova grupa")
				.setView(alertView);

		createButton.setOnClickListener(view -> {
			if (name.getText().toString().isEmpty()) {
				Toast.makeText(getContext(), "Upišite naziv grupe.", Toast.LENGTH_SHORT).show();
			} else {
				createNewGroup(name.getText().toString());
			}
		});

		cancelButton.setOnClickListener(view -> dialog.dismiss());

		dialog = builder.create();
		dialog.show();
	}

	private void setupRecycler(String type) {
		Query query;

		if (type.equals(STUDENT)) {
			query = groupChatsColl.whereArrayContains("studentID", userID);
		} else {
			query = groupChatsColl.whereEqualTo("ownerID", userID);
		}

		FirestoreRecyclerOptions<ChatGroupModel> options = new FirestoreRecyclerOptions.Builder<ChatGroupModel>()
				.setQuery(query, ChatGroupModel.class)
				.build();

		groupAdapter = new ChatGroupAdapter(getContext(), options);

		groupRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
		groupRecycler.setAdapter(groupAdapter);

		groupAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String id = documentSnapshot.getId();

			Intent intent = new Intent(getContext(), ChatGroupActivity.class);
			intent.putExtra("chatID", id);
			startActivity(intent);
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		groupAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		groupAdapter.stopListening();
	}
}