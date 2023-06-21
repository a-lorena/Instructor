package com.example.instrukcije.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.EditPostActivity;
import com.example.instrukcije.Models.PostModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostAdapterStudent extends FirestoreRecyclerAdapter<PostModel, PostAdapterStudent.PostHolderStudent> {

    private Context context;
    AlertDialog.Builder alert;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DocumentReference docRefPost;

    public PostAdapterStudent(Context context, @NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolderStudent holder, int position, @NonNull PostModel model) {
        holder.postTitle.setText(model.getTitle());
        holder.postDescription.setText(model.getDescription());
        holder.postUsername.setText(model.getUsername());
        holder.postLevel.setText(model.getLevel());

        String postID = model.getPostID();

        holder.editPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditPostActivity.class);
            intent.putExtra("postID", postID);
            context.startActivity(intent);
        });

        holder.deletePostButton.setOnClickListener(view -> openDialog(model, postID));
    }

    @NonNull
    @Override
    public PostHolderStudent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_post_student, parent, false);
        return new PostHolderStudent(v);
    }

    class PostHolderStudent extends RecyclerView.ViewHolder {
        TextView postTitle, postDescription, postUsername, postLevel;
        ImageButton editPostButton, deletePostButton;

        public PostHolderStudent(View view) {
            super(view);

            postTitle = view.findViewById(R.id.postTitle);
            postDescription = view.findViewById(R.id.postDescription);
            postUsername = view.findViewById(R.id.postName);
            postLevel = view.findViewById(R.id.postLevelInstructor);
            editPostButton = view.findViewById(R.id.editPostButton);
            deletePostButton = view.findViewById(R.id.deletePostButton);
        }
    }

    private void openDialog(PostModel model, String postID) {
        alert = new AlertDialog.Builder(context);

        alert.setTitle("Pozor!")
                .setMessage("Sigurni ste da želite obrisati oglas?")
                .setPositiveButton("Obriši", (dialogInterface, i) -> {
                    docRefPost = firestore.collection("Posts").document(postID);
                    docRefPost.delete();
                })
                .setNegativeButton("Odustani", (dialogInterface, i) -> {
                });

        alert.create().show();
    }
}
