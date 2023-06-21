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
import com.google.firebase.firestore.DocumentSnapshot;

public class PickGroupChatAdapter extends FirestoreRecyclerAdapter<ChatGroupModel, PickGroupChatAdapter.PickGroupChatHolder> {

	Context context;
	PickGroupChatAdapter.OnItemClickListener listener;

	public PickGroupChatAdapter(Context context, @NonNull FirestoreRecyclerOptions<ChatGroupModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull PickGroupChatAdapter.PickGroupChatHolder holder, int position, @NonNull ChatGroupModel model) {
		holder.name.setText(model.getGroupName());

		if (model.getStudentName() == null) {
			holder.students.setVisibility(View.GONE);
		} else {
			holder.students.setText(model.getStudentName().toString().replace("[", "")
					.replace("]", ""));
			holder.students.setVisibility(View.VISIBLE);
		}
	}

	@NonNull
	@Override
	public PickGroupChatAdapter.PickGroupChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_chat, parent, false);
		return new PickGroupChatAdapter.PickGroupChatHolder(v);
	}

	public class PickGroupChatHolder extends RecyclerView.ViewHolder {
		TextView name, students;

		public PickGroupChatHolder(@NonNull View view) {
			super(view);

			name = view.findViewById(R.id.chatName);
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

	public void setOnItemClickListener(PickGroupChatAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
