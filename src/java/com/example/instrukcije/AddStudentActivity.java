package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.instrukcije.Adapters.PickStudentAdapter;
import com.example.instrukcije.Models.ChatModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddStudentActivity extends AppCompatActivity {
	RecyclerView recycler;

	FirebaseFirestore firestore;
	CollectionReference chatsColl, groupChatsColl;
	String userID;

	PickStudentAdapter adapter;
	String chatID, chatName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_student);

		chatID = getIntent().getStringExtra("chatID");
		chatName = getIntent().getStringExtra("chatName");

		recycler = findViewById(R.id.pickStudentRecycler);

		firestore = FirebaseFirestore.getInstance();
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		chatsColl = firestore.collection("Chats");
		groupChatsColl = firestore.collection("GroupChats");

		setupRecycler();
	}

	private void setupRecycler() {
		Query query = chatsColl.whereEqualTo("teacherID", userID);

		FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>()
				.setQuery(query, ChatModel.class)
				.build();

		adapter = new PickStudentAdapter(this, options);

		recycler.setLayoutManager(new LinearLayoutManager(this));
		recycler.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String studentID = documentSnapshot.getString("studentID");
			String studentName = documentSnapshot.getString("studentName");

			groupChatsColl.document(chatID).update(
					"studentID", FieldValue.arrayUnion(studentID),
					"studentName", FieldValue.arrayUnion(studentName)
			).addOnSuccessListener(unused -> {
				Toast.makeText(context, studentName + " je dodan/a u grupu " + chatName + ".", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(AddStudentActivity.this, ChatGroupActivity.class);
				intent.putExtra("chatID", chatID);
				startActivity(intent);
				finish();
			});
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();
		adapter.stopListening();
	}
}