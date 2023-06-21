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
import com.google.firebase.firestore.DocumentSnapshot;

public class PickStudentAdapter extends FirestoreRecyclerAdapter<ChatModel, PickStudentAdapter.PickStudentHolder> {

	Context context;
	PickStudentAdapter.OnItemClickListener listener;

	public PickStudentAdapter(Context context, @NonNull FirestoreRecyclerOptions<ChatModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull PickStudentAdapter.PickStudentHolder holder, int position, @NonNull ChatModel model) {
		holder.name.setText(model.getStudentName());
	}

	@NonNull
	@Override
	public PickStudentAdapter.PickStudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_chat, parent, false);
		return new PickStudentAdapter.PickStudentHolder(v);
	}

	public class PickStudentHolder extends RecyclerView.ViewHolder {
		TextView name;

		public PickStudentHolder(@NonNull View view) {
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

	public void setOnItemClickListener(PickStudentAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
