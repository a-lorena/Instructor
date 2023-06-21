package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instrukcije.Adapters.PickGroupChatAdapter;
import com.example.instrukcije.Models.ChatGroupModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class AddToGroupActivity extends AppCompatActivity {

	RecyclerView pickGroupRecycler;
	FloatingActionButton makeGroup;

	FirebaseFirestore firestore;
	String userID, receiverID, receiverName;
	DocumentReference userDoc;
	CollectionReference groupChatsColl, chatsColl;

	PickGroupChatAdapter groupAdapter;

	AlertDialog.Builder builder;
	AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_group_chat);

		receiverID = getIntent().getStringExtra("receiverID");
		receiverName = getIntent().getStringExtra("receiverName");

		pickGroupRecycler = findViewById(R.id.pickgroupRecycler);
		makeGroup = findViewById(R.id.addNewGroup);

		firestore = FirebaseFirestore.getInstance();
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		userDoc = firestore.collection("Users").document(userID);
		groupChatsColl = firestore.collection("GroupChats");
		chatsColl = firestore.collection("Chats");


		setupChatRecycler();

		makeGroup.setOnClickListener(view -> newGroupDialog());
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
			Toast.makeText(AddToGroupActivity.this, "Grupa uspješno stvorena", Toast.LENGTH_SHORT).show();
		});
	}

	private void newGroupDialog() {
		builder = new AlertDialog.Builder(this);
		View alertView = getLayoutInflater().inflate(R.layout.dialog_new_group, null);

		TextView name = alertView.findViewById(R.id.groupNameInput);
		ImageButton createButton = alertView.findViewById(R.id.createGroupButton);
		ImageButton cancelButton = alertView.findViewById(R.id.cancelGroupButton);

		builder.setTitle("Nova grupa")
				.setView(alertView);

		createButton.setOnClickListener(view -> {
			if (name.getText().toString().isEmpty()) {
				Toast.makeText(AddToGroupActivity.this, "Upišite naziv grupe.", Toast.LENGTH_SHORT).show();
			} else {
				createNewGroup(name.getText().toString());
			}
		});

		cancelButton.setOnClickListener(view -> dialog.dismiss());

		dialog = builder.create();
		dialog.show();
	}

	private void setupChatRecycler() {
		Query query = groupChatsColl.whereEqualTo("ownerID", userID);

		FirestoreRecyclerOptions<ChatGroupModel> options = new FirestoreRecyclerOptions.Builder<ChatGroupModel>()
				.setQuery(query, ChatGroupModel.class)
				.build();

		groupAdapter = new PickGroupChatAdapter(this, options);

		pickGroupRecycler.setLayoutManager(new LinearLayoutManager(this));
		pickGroupRecycler.setAdapter(groupAdapter);

		groupAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			ChatGroupModel Chat = documentSnapshot.toObject(ChatGroupModel.class);
			String id = documentSnapshot.getId();
			String name = documentSnapshot.getString("groupName");

			groupChatsColl.document(id).update(
					"studentID", FieldValue.arrayUnion(receiverID),
					"studentName", FieldValue.arrayUnion(receiverName)
			).addOnSuccessListener(unused -> {
				Toast.makeText(context, receiverName + " je dodan/a u grupu " + name + ".", Toast.LENGTH_SHORT).show();
				finish();
			});
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		groupAdapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();
		groupAdapter.stopListening();
	}
}