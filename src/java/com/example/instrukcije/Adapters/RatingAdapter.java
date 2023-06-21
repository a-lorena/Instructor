package com.example.instrukcije.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.Models.RatingModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RatingAdapter extends FirestoreRecyclerAdapter<RatingModel, RatingAdapter.RatingHolder> {

    public RatingAdapter(@NonNull FirestoreRecyclerOptions<RatingModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RatingAdapter.RatingHolder holder, int position, @NonNull RatingModel model) {
        holder.name.setText(model.getStudentFullName());
        holder.stars.setRating((float) model.getStars());
        holder.comment.setText(model.getComment());

        Date date = model.getDate().toDate();
        String formatDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
        holder.date.setText(formatDate);
    }

    @NonNull
    @Override
    public RatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_rating, parent, false);
        return new RatingHolder(v);
    }

    class RatingHolder extends RecyclerView.ViewHolder {
        TextView name, comment, date;
        RatingBar stars;

        public RatingHolder(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.ratingStudentName);
            stars = view.findViewById(R.id.ratingStars);
            comment = view.findViewById(R.id.ratingComment);
            date = view.findViewById(R.id.ratingDate);
        }
    }
}
