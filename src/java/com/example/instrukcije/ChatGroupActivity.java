package com.example.instrukcije;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.instrukcije.Adapters.MessageAdapter;
import com.example.instrukcije.Models.MessageModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;

public class ChatGroupActivity extends AppCompatActivity {

	MaterialToolbar headerInstruktor, headerStudent;
	RecyclerView recyclerView;
	EditText newMessage;
	ImageButton sendMessageButton;

	FirebaseFirestore firestore;
	String userID, chatID;
	DocumentReference userDoc, chatDoc;
	CollectionReference chatsColl, messageColl;

	MessageAdapter messageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_group);

		chatID = getIntent().getStringExtra("chatID");

		headerInstruktor = findViewById(R.id.chatHeaderInstructor);
		setSupportActionBar(headerInstruktor);
		headerStudent = findViewById(R.id.chatHeaderStudent);
		recyclerView = findViewById(R.id.singleChatRecycler);
		newMessage = findViewById(R.id.writeNewMessage);
		sendMessageButton = findViewById(R.id.sendNewMessageButton);

		firestore = FirebaseFirestore.getInstance();
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		userDoc = firestore.collection("Users").document(userID);
		chatDoc = firestore.collection("GroupChats").document(chatID);
		chatsColl = firestore.collection("GroupChats");

		getChatInfo();
		setupRecycler();

		sendMessageButton.setOnClickListener(view -> getMessageText());
	}

	private void sendMessage(String text) {
		HashMap<String, Object> message = new HashMap<>();

		message.put("message", text);
		message.put("date", new Date());
		message.put("ownerID", userID);

		messageColl = firestore.collection("GroupChats").document(chatID).collection("Messages");
		messageColl.add(message).addOnSuccessListener(documentReference -> {
			String newMessageID = documentReference.getId();

			userDoc.get().addOnCompleteListener(task -> {
				String ownerName = task.getResult().getString("fullName");

				messageColl.document(newMessageID).update(
					"ownerName", ownerName
				);
			});
		});

		newMessage.setText("");
	}

	private void getMessageText() {
		String txtNewMessage = newMessage.getText().toString();

		if (txtNewMessage.isEmpty()) {
			Toast.makeText(this, "Unesite poruku.", Toast.LENGTH_SHORT).show();
		} else {
			sendMessage(txtNewMessage);
		}
	}

	private void setupRecycler() {
		Query query = chatDoc.collection("Messages")
				.orderBy("date", Query.Direction.ASCENDING);

		FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
				.setQuery(query, MessageModel.class)
				.build();

		messageAdapter = new MessageAdapter(this, options);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(messageAdapter);
	}

	private void getChatInfo() {
		chatDoc.get().addOnCompleteListener(task -> {
			String groupName = task.getResult().getString("groupName");
			String teacherName = task.getResult().getString("teacherName");
			String studentName;

			if (task.getResult().get("studentName") != null) {
				studentName = task.getResult().get("studentName").toString().replace("[", "")
				.replace("]", "");
			} else {
				studentName = "";
			}

			userDoc.get().addOnCompleteListener(taskDoc -> {
				if (taskDoc.getResult().getString("userType").equals("Instruktor")) {
					headerInstruktor.setTitle(groupName);
					headerInstruktor.setSubtitle(studentName);
					headerInstruktor.setVisibility(View.VISIBLE);
				}

				if (taskDoc.getResult().getString("userType").equals("Učenik")) {
					String subtitle = teacherName + ", " + studentName;
					headerStudent.setTitle(groupName);
					headerStudent.setSubtitle(subtitle);
					headerStudent.setVisibility(View.VISIBLE);
				}
			});
		}).addOnFailureListener(e -> Toast.makeText(ChatGroupActivity.this, "Razgovor nije pronađen", Toast.LENGTH_SHORT).show());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_chat_header_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int resID = item.getItemId();

		if (resID == R.id.addStudent) {
			Intent addToGroup = new Intent(ChatGroupActivity.this, AddStudentActivity.class);
			addToGroup.putExtra("chatID", chatDoc.getId());
			addToGroup.putExtra("chatName", headerInstruktor.getTitle());
			startActivity(addToGroup);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		messageAdapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();
		messageAdapter.stopListening();
	}
}