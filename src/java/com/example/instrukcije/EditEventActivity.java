package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity {

    EditText subject, person, date, time;
    ImageView calendar, clock;
    Button updateButton;

    FirebaseFirestore firestore;
    DocumentReference eventDocRef;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        String eventID = getIntent().getStringExtra("eventID");

        subject = findViewById(R.id.editEventSubject);
        person = findViewById(R.id.editEventPerson);
        date = findViewById(R.id.editEventDate);
        time = findViewById(R.id.editEventTime);
        calendar = findViewById(R.id.editDateImage);
        clock = findViewById(R.id.editTimeImage);
        updateButton = findViewById(R.id.updateEventButton);

        firestore = FirebaseFirestore.getInstance();
        eventDocRef = firestore.collection("Events").document(eventID);

        pd = new ProgressDialog(this);

        getEventInfo();

        calendar.setOnClickListener(view -> dateInput());

        clock.setOnClickListener(view -> timeInput());

        updateButton.setOnClickListener(view -> {
            String txtSubject = subject.getText().toString();
            String txtPerson = person.getText().toString();
            String txtDate = date.getText().toString();
            String txtTime = time.getText().toString();

            if (checkEmptyFields(txtSubject, txtPerson, txtDate, txtTime)) {
                updateEvent(txtSubject, txtPerson, txtDate, txtTime);
            }
        });
    }

    private void updateEvent(String subject, String person, String date, String time) {
        pd.setMessage("Spremanje izmjena...");
        pd.show();

        eventDocRef.update(
                "subject", subject,
                "studentName", person,
                "date", date,
                "time", time
        ).addOnSuccessListener(unused -> {
            pd.dismiss();
            Toast.makeText(EditEventActivity.this, "Izmjene uspješno spremljene.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(EditEventActivity.this, "Soremanje izmjena neuspješno!", Toast.LENGTH_SHORT).show();
        });

        finish();
    }

    private boolean checkEmptyFields(String subject, String person, String date, String time) {
        if (subject.isEmpty() || person.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void timeInput() {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.getTime().getHours();
        int minute = cal.getTime().getMinutes();

        TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog,
                (timePicker, h, m) -> {
                    String hours = String.valueOf(h);
                    String minutes = String.valueOf(m);

                    if (h < 10) {
                        hours = "0" + h;
                    }

                    if (m < 10) {
                        minutes = "0" + m;
                    }

                    time.setText(hours + ":" + minutes);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void dateInput() {
        final Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog,
                (datePicker, y, m, d) -> {
                    String day1 = String.valueOf(d);
                    String month1 = String.valueOf(m+1);

                    if (d < 10) {
                        day1 = "0" + d;
                    }

                    if ((m+1) < 10) {
                        month1 = "0" + (m+1);
                    }

                    date.setText(day1 + "." + month1 + "." + y);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void getEventInfo() {
        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                subject.setText(task.getResult().getString("subject"), TextView.BufferType.EDITABLE);
                person.setText(task.getResult().getString("studentName"), TextView.BufferType.EDITABLE);
                date.setText(task.getResult().getString("date"), TextView.BufferType.EDITABLE);
                time.setText(task.getResult().getString("time"), TextView.BufferType.EDITABLE);
            }
        });
    }
}