package com.example.instrukcije.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.Models.ChatGroupModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatGroupAdapter extends FirestoreRecyclerAdapter<ChatGroupModel, ChatGroupAdapter.ChatGroupHolder> {

	Context context;
	ChatGroupAdapter.OnItemClickListener listener;
	CollectionReference usersColl = FirebaseFirestore.getInstance().collection("Users");

	public ChatGroupAdapter(Context context, @NonNull FirestoreRecyclerOptions<ChatGroupModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull ChatGroupAdapter.ChatGroupHolder holder, int position, @NonNull ChatGroupModel model) {
		holder.name.setText(model.getGroupName());

		if (model.getStudentName() == null) {
			holder.students.setVisibility(View.GONE);
		} else {
			holder.students.setText(model.getStudentName().toString().replace("[", "")
					.replace("]", ""));
			holder.students.setVisibility(View.VISIBLE);
		}

		usersColl.document(model.getOwnerID()).get().addOnCompleteListener(task -> {
			holder.teacher.setText(task.getResult().getString("fullName"));
			holder.teacher.setVisibility(View.VISIBLE);
		});
	}

	@NonNull
	@Override
	public ChatGroupAdapter.ChatGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_chat, parent, false);
		return new ChatGroupAdapter.ChatGroupHolder(v);
	}

	public class ChatGroupHolder extends RecyclerView.ViewHolder {
		TextView name, teacher, students;

		public ChatGroupHolder(@NonNull View view) {
			super(view);

			name = view.findViewById(R.id.chatName);
			teacher = view.findViewById(R.id.chatInstructor);
			students = view.findViewById(R.id.chatStudents);

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

	public void setOnItemClickListener(ChatGroupAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
