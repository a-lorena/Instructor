package com.example.instrukcije.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.Models.MessageModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageAdapter extends FirestoreRecyclerAdapter<MessageModel, MessageAdapter.MessageHolder> {

    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference chatsColl = firestore.collection("Chats");
    Context context;
    static final String DELETED = "Poruka je obrisana...";

    public MessageAdapter(Context context, @NonNull FirestoreRecyclerOptions<MessageModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position, @NonNull MessageModel model) {
        holder.text.setText(model.getMessage());

        if (!model.getOwnerID().equals(userID)) {
            holder.name.setText(model.getOwnerName());
            holder.name.setVisibility(View.VISIBLE);
        }

        //String timestamp = model.getDate().toString();
        Date date = model.getDate().toDate();
        String formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
        holder.date.setText(formatDate);

        if (model.getOwnerID().equals(userID)) {
            holder.text.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_color));
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.text.setGravity(Gravity.END);
            holder.date.setGravity(Gravity.START);

            if (model.getMessage().equals(DELETED)) {
                holder.text.setBackgroundColor(ContextCompat.getColor(context, R.color.deleted_right_background));
                holder.text.setTextColor(ContextCompat.getColor(context, R.color.deleted_right_text));
            }

        } else {
            if (model.getMessage().equals(DELETED)) {
                holder.text.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                holder.text.setTextColor(ContextCompat.getColor(context, R.color.light_gray));
            }
        }

        holder.message.setOnLongClickListener(view -> {
            if (model.getOwnerID().equals(userID)) {
                deleteDialog(model);
            }

            return false;
        });
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_message, parent, false);
        return new MessageHolder(v);
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView name, text, date;
        ConstraintLayout message;

        public MessageHolder(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.messageName);
            text = view.findViewById(R.id.messageText);
            date = view.findViewById(R.id.messageDate);
            message = view.findViewById(R.id.messageLayout);
        }
    }

    private void deleteDialog(MessageModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Pozor!")
                .setMessage("Želite li obrisati poruku?")
                .setPositiveButton("Da", (dialogInterface, i) ->
                        chatsColl.document(model.getChatID()).collection("Messages").document(model.getMessageID())
                                .update(
                                        "message", "Poruka je obrisana..."
                                )).setNegativeButton("Ne", (dialogInterface, i) -> {

                });

        builder.create().show();
    }
}
