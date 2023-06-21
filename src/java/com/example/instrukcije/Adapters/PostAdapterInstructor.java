package com.example.instrukcije.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.ChatActivity;
import com.example.instrukcije.Models.PostModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PostAdapterInstructor extends FirestoreRecyclerAdapter<PostModel, PostAdapterInstructor.PostHolderInstruktor> implements Filterable {

    private Context context;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    CollectionReference userColl = firestore.collection("Users");
    CollectionReference chatsColl = firestore.collection("Chats");

    public PostAdapterInstructor(Context context, @NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolderInstruktor holder, int position, @NonNull PostModel model) {
        holder.postTitle.setText(model.getTitle());
        holder.postDescription.setText(model.getDescription());
        holder.postUsername.setText(model.getUsername());
        holder.postLevel.setText(model.getLevel());

        String receiverID = model.getPublisher();

        holder.sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> chat = new HashMap<>();

                chat.put("teacherID", userID);
                chat.put("studentID", receiverID);
                chat.put("isGroup", false);

                chatsColl.add(chat).addOnCompleteListener(task -> {
                    String chatID = task.getResult().getId();

                    userColl.document(userID).get().addOnCompleteListener(t -> {
                        String teacherName = t.getResult().getString("fullName");
                        chatsColl.document(chatID).update("teacherName", teacherName);
                    });

                    userColl.document(receiverID).get().addOnCompleteListener(t -> {
                        String receiverName = t.getResult().getString("fullName");
                        chatsColl.document(chatID).update("studentName", receiverName);
                    });

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("chatID", chatID);
                    intent.putExtra("receiverID", receiverID);
                    context.startActivity(intent);
                });
            }
        });
    }

    @NonNull
    @Override
    public PostAdapterInstructor.PostHolderInstruktor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_post_instructor, parent, false);
        return new PostHolderInstruktor(v);
    }

    class PostHolderInstruktor extends RecyclerView.ViewHolder {
        TextView postTitle, postDescription, postUsername, postLevel;
        Button sendMessageButton;

        public PostHolderInstruktor(View view) {
            super(view);

            postTitle = view.findViewById(R.id.postTitleInstructor);
            postDescription = view.findViewById(R.id.postDescriptionInstructor);
            postUsername = view.findViewById(R.id.postNameInstructor);
            sendMessageButton = view.findViewById(R.id.sendMessageToStudentButton);
            postLevel = view.findViewById(R.id.postLevelInstructor);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }
    };

}
