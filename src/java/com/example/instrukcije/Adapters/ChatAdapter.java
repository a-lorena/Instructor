package com.example.instrukcije.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.Models.ChatModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatModel, ChatAdapter.ChatHolder> {

    Context context;
    OnItemClickListener listener;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ChatAdapter(Context context, @NonNull FirestoreRecyclerOptions<ChatModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatModel model) {
        String receiverID = model.getStudentID();
        boolean isGroup = model.getIsGroup();

        if (isGroup) {
            holder.name.setText(model.getGroupName());
        } else {
            if (userID.equals(receiverID)) {
                holder.name.setText(model.getTeacherName());
            } else {
                holder.name.setText(model.getStudentName());
            }
        }
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_chat, parent, false);
        return new ChatHolder(v);
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ChatHolder(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.chatName);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(context, getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Context context, DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
