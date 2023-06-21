package com.example.instrukcije.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.ChatActivity;
import com.example.instrukcije.Models.InstructorModel;
import com.example.instrukcije.R;
import com.example.instrukcije.RatingActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static java.lang.Math.round;

public class InstructorAdapter extends FirestoreRecyclerAdapter<InstructorModel, InstructorAdapter.InstruktorHolder> {

    private Context context;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    CollectionReference userColl = firestore.collection("Users");
    CollectionReference chatsColl = firestore.collection("Chats");

    public InstructorAdapter(Context context, FirestoreRecyclerOptions<InstructorModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull InstructorAdapter.InstruktorHolder holder, int position, @NonNull InstructorModel model) {
        holder.instName.setText(model.getFullName());
        holder.instEducation.setText(model.getEducation());
        holder.instSubjects.setText(model.getSubjects());
        String priceTXT = String.valueOf(model.getPrice()) + " kn/h";
        holder.instPrice.setText(priceTXT);
        holder.instRating.setText(String.valueOf((double)round(model.getRating()*10) / 10));
        boolean isAvailable = model.getAvailable();
        holder.sendMessage.setVisibility(!isAvailable ? View.INVISIBLE : View.VISIBLE);

        holder.stars.setRating((float)model.getRating());

        holder.levels.setText((model.getLevels().toString().replace("[", "")
                .replace("]", "")));

        String teacherID = model.getId();

        holder.rateTeacher.setOnClickListener(view -> {
            Intent intent = new Intent(context, RatingActivity.class);
            intent.putExtra("teacherID", teacherID);
            context.startActivity(intent);
        });

        holder.sendMessage.setOnClickListener(view -> {
            HashMap<String, Object> chat = new HashMap<>();

            chat.put("teacherID", teacherID);
            chat.put("studentID", userID);
            chat.put("isGroup", false);

            chatsColl.add(chat).addOnCompleteListener(task -> {
                String chatID = task.getResult().getId();

                userColl.document(userID).get().addOnCompleteListener(t -> {
                    String studentName = t.getResult().getString("fullName");
                    chatsColl.document(chatID).update("studentName", studentName);
                });

                userColl.document(teacherID).get().addOnCompleteListener(t -> {
                    String teacherName = t.getResult().getString("fullName");
                    chatsColl.document(chatID).update("teacherName", teacherName);
                });

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatID", chatID);
                intent.putExtra("receiverID", teacherID);
                context.startActivity(intent);
            });
        });
    }

    @NonNull
    @Override
    public InstruktorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_instructor, parent, false);
        return new InstruktorHolder(v);
    }

    class InstruktorHolder extends RecyclerView.ViewHolder {
        TextView instName, instEducation, instSubjects, instRating, instPrice, levels;
        Button rateTeacher, sendMessage;

        RatingBar stars;

        public InstruktorHolder(@NonNull View view) {
            super(view);

            instName = view.findViewById(R.id.instructorName);
            instEducation = view.findViewById(R.id.instructorEducation);
            instSubjects = view.findViewById(R.id.instructorSubjects);
            instRating = view.findViewById(R.id.instructorRating);
            instPrice = view.findViewById(R.id.instructorPrice);
            levels = view.findViewById(R.id.instructorLevels);
            rateTeacher = view.findViewById(R.id.rateButton);
            sendMessage = view.findViewById(R.id.sendMessageButton);

            stars = view.findViewById(R.id.starsNumber);
        }
    }
}
