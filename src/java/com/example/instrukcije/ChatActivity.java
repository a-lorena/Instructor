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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    String receiverID, chatID, receiverName;

    MaterialToolbar headerInstruktor, headerStudent;
    RecyclerView recyclerView;
    EditText newMessage;
    ImageButton sendMessageButton, addRating;

    FirebaseFirestore firestore;
    FirebaseUser user;
    String userID, userType;
    DocumentReference receiverDocRef, userDoc;
    CollectionReference chatsColl, messageColl;

    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverID = getIntent().getStringExtra("receiverID");
        receiverName = getIntent().getStringExtra("receiverName");
        chatID = getIntent().getStringExtra("chatID");

        headerInstruktor = findViewById(R.id.chatHeaderInstructor);
        setSupportActionBar(headerInstruktor);
        headerStudent = findViewById(R.id.chatHeaderStudent);
        recyclerView = findViewById(R.id.singleChatRecycler);
        newMessage = findViewById(R.id.writeNewMessage);
        sendMessageButton = findViewById(R.id.sendNewMessageButton);
        addRating = findViewById(R.id.addRating);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDoc = firestore.collection("Users").document(userID);
        receiverDocRef = firestore.collection("Users").document(receiverID);
        chatsColl = firestore.collection("Chats");

        getSenderInfo();
        getReceiverInfo();
        setupRecycler();

        sendMessageButton.setOnClickListener(view -> getMessageText());
        addRating.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, RatingActivity.class);
            intent.putExtra("teacherID", receiverID);
            intent.putExtra("showButton", "show");
            startActivity(intent);
        });

    }

    private void sendMessage(String text) {
        HashMap<String, Object> message = new HashMap<>();

        message.put("message", text);
        message.put("date", new Date());
        message.put("ownerID", userID);
        message.put("chatID", chatID);

        messageColl = firestore.collection("Chats").document(chatID).collection("Messages");
        messageColl.add(message).addOnCompleteListener(task -> {
            String messageID = task.getResult().getId();

            messageColl.document(messageID).update("messageID", messageID);
        });

        newMessage.setText("");
    }

    private void getMessageText() {
        String txtNewMessage = newMessage.getText().toString();
        
        if (txtNewMessage.isEmpty()) {
            Toast.makeText(this, "Unesite poruku", Toast.LENGTH_SHORT).show();
        } else {
            sendMessage(txtNewMessage);
        }
    }

    private void setupRecycler() {
        Query query = firestore.collection("Chats").document(chatID).collection("Messages")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class)
                .build();

        messageAdapter = new MessageAdapter(this, options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }

    private void getSenderInfo() {
        userDoc.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                userType = task.getResult().getString("userType");

                if (userType.equals("Učenik")) {
                    addRating.setVisibility(View.VISIBLE);
                } else {
                    headerInstruktor.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Korisnik nije pronađen.", Toast.LENGTH_SHORT).show());
    }

    private void getReceiverInfo() {
        receiverDocRef.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                receiverName = task.getResult().getString("fullName");
                headerInstruktor.setTitle(receiverName);
                headerStudent.setTitle(receiverName);
            }
        }).addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Korisnik nije pronađen.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_header_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int resID = item.getItemId();

        if (resID == R.id.addToGroupChat) {
            Intent addToGroup = new Intent(ChatActivity.this, AddToGroupActivity.class);
            addToGroup.putExtra("receiverID", receiverID);
            addToGroup.putExtra("receiverName", receiverName);
            startActivity(addToGroup);
            return true;
        } else if (resID == R.id.addNewEventHeader) {
            Intent createEvent = new Intent(ChatActivity.this, NewEventActivity.class);
            createEvent.putExtra("receiverID", receiverID);
            startActivity(createEvent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatID != null) {
            messageAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatID != null) {
            messageAdapter.stopListening();
        }
    }
}