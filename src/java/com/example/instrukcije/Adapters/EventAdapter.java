package com.example.instrukcije.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instrukcije.EditEventActivity;
import com.example.instrukcije.Models.EventModel;
import com.example.instrukcije.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventAdapter extends FirestoreRecyclerAdapter<EventModel, EventAdapter.EventHolder> {

    private Context context;
    AlertDialog.Builder dialog;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DocumentReference eventDocRef;
    //CollectionReference eventsColl = firestore.collection("Events");

    public EventAdapter(Context context, @NonNull FirestoreRecyclerOptions<EventModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventAdapter.EventHolder holder, int position, @NonNull EventModel model) {
        holder.title.setText(model.getSubject());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());

        if (userID.equals(model.getStudentReceiver())) {
            holder.person.setText(model.getTeacherIme());
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.doneButton.setVisibility(View.GONE);
        } else {
            holder.person.setText(model.getStudentName());
        }

        String eventID = model.getEventID();

        holder.editButton.setOnClickListener(view -> {
            Intent editEvent = new Intent(context, EditEventActivity.class);
            editEvent.putExtra("eventID", eventID);
            context.startActivity(editEvent);
        });

        holder.deleteButton.setOnClickListener(view -> openDeleteDialog(holder, eventID));

        holder.doneButton.setOnClickListener(view -> openDoneDialog(holder, eventID));
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_event, parent, false);
        return new EventHolder(v);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        TextView title, person, date, time;
        ImageButton editButton, deleteButton, doneButton;
        CardView event;

        public EventHolder(@NonNull View view) {
            super(view);

            title = view.findViewById(R.id.eventSubject);
            person = view.findViewById(R.id.eventInstructorName);
            date = view.findViewById(R.id.eventDate);
            time = view.findViewById(R.id.eventTime);
            editButton = view.findViewById(R.id.eventEditButton);
            deleteButton = view.findViewById(R.id.eventDeleteButton);
            doneButton = view.findViewById(R.id.eventDoneButton);
            event = view.findViewById(R.id.eventContainer);
        }
    }

    private void hideViews(EventAdapter.EventHolder holder) {
        holder.person.setVisibility(View.GONE);
        holder.date.setVisibility(View.GONE);
        holder.time.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.GONE);
        holder.doneButton.setVisibility(View.GONE);
    }

    private void openDeleteDialog(EventAdapter.EventHolder holder, String eventID) {
        dialog = new AlertDialog.Builder(context);

        dialog.setTitle("Pozor!")
                .setMessage("Sigurno želite obrisati navedeni termin?")
                .setPositiveButton("Obriši", (dialogInterface, i) -> {
                    eventDocRef = firestore.collection("Events").document(eventID);

                    holder.event.setBackgroundColor(ContextCompat.getColor(context, R.color.delete_button));
                    holder.title.setText("Događaj je obrisan.");
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.white));
                    holder.editButton.setImageResource(R.drawable.ic_cancel);
                    holder.editButton.setBackgroundColor(Color.TRANSPARENT);

                    hideViews(holder);
                    eventDocRef.delete();
                })
                .setNegativeButton("Odustani", (dialogInterface, i) -> {
                });

        dialog.create().show();
    }

    public void openDoneDialog(EventAdapter.EventHolder holder, String eventID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Pozor!")
                .setMessage("Odradili ste navedeni termin?")
                .setPositiveButton("Da", (dialogInterface, i) -> {
                    eventDocRef = firestore.collection("Events").document(eventID);

                    holder.event.setBackgroundColor(ContextCompat.getColor(context, R.color.done_button));
                    holder.title.setText("Termin je odrađen.");
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.white));
                    holder.editButton.setImageResource(R.drawable.ic_done);
                    holder.editButton.setBackgroundColor(Color.TRANSPARENT);

                    hideViews(holder);

                    eventDocRef.delete();
                })
                .setNegativeButton("Ne", (dialogInterface, i) -> {
                });

        alert.create().show();
    }
}
