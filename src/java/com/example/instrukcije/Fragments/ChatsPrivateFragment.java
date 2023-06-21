package com.example.instrukcije.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instrukcije.Adapters.ChatAdapter;
import com.example.instrukcije.ChatActivity;
import com.example.instrukcije.Models.ChatModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatsPrivateFragment extends Fragment {

	String type;
	static final String STUDENT = "Učenik";

	public ChatsPrivateFragment(String type) {
		this.type = type;
	}

	RecyclerView recyclerView;

	FirebaseFirestore firestore;
	String userID;
	DocumentReference userDoc;
	CollectionReference chatsColl, userChatsColl;

	ChatAdapter chatAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chats_private, container, false);

		recyclerView = view.findViewById(R.id.privateRecycler);

		firestore = FirebaseFirestore.getInstance();
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		userDoc = firestore.collection("Users").document(userID);
		chatsColl = firestore.collection("Chats");
		userChatsColl = firestore.collection("Users").document(userID).collection("UserChats");

		setupRecycler(type);

		return view;
	}

	private void setupRecycler(String type) {
		Query query;

		if (type.equals(STUDENT)) {
			query = chatsColl.whereEqualTo("studentID", userID);
		} else {
			query = chatsColl.whereEqualTo("teacherID", userID);
		}

		FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>()
				.setQuery(query, ChatModel.class)
				.build();

		chatAdapter = new ChatAdapter(getActivity(), options);

		recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
		recyclerView.setAdapter(chatAdapter);

		chatAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			//ChatModel Chat = documentSnapshot.toObject(ChatModel.class);
			String id = documentSnapshot.getId();
			String receiverID, receiverName;

			if (type.equals(STUDENT)) {
				receiverID = documentSnapshot.getString("teacherID");
				receiverName = documentSnapshot.getString("teacherName");
			} else {
				receiverID = documentSnapshot.getString("studentID");
				receiverName = documentSnapshot.getString("studentName");
			}

			Intent intent = new Intent(getContext(), ChatActivity.class);
			intent.putExtra("chatID", id);
			intent.putExtra("receiverID", receiverID);
			intent.putExtra("receiverName", receiverName);
			context.startActivity(intent);
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