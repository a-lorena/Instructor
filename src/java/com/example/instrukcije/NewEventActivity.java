package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class NewEventActivity extends AppCompatActivity {

    private EditText subject, student, date, time;
    private ImageView dateImage, timeImage;
    private Button saveButton;

    FirebaseFirestore firestore;
    String teacherName, teacherID, studentID, studentName, userType, chatID;
    DocumentReference teacherDocRef, studentDocRef;
    CollectionReference eventCollection;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        studentID = getIntent().getStringExtra("receiverID");
        chatID = getIntent().getStringExtra("chatID");

        subject = (EditText) findViewById(R.id.inputEventSubject);
        student = (EditText) findViewById(R.id.inputEventTeacher);
        date = (EditText) findViewById(R.id.inputEventDate);
        time = (EditText) findViewById(R.id.inputEventTime);
        dateImage = (ImageView) findViewById(R.id.inputDateImage);
        timeImage = (ImageView) findViewById(R.id.inputTimeImage);
        saveButton = (Button) findViewById(R.id.saveEventButton);

        firestore = FirebaseFirestore.getInstance();
        teacherID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        teacherDocRef = firestore.collection("Users").document(teacherID);
        studentDocRef = firestore.collection("Users").document(studentID);
        eventCollection = firestore.collection("Events");

        pd = new ProgressDialog(this);

        getUserInfo();
        getClientInfo();

        dateImage.setOnClickListener(view -> dateInput());

        timeImage.setOnClickListener(view -> timeInput());

        saveButton.setOnClickListener(view -> {
            String txtTitle = subject.getText().toString();
            String txtStudent = student.getText().toString();
            String txtDate = date.getText().toString();
            String txtTime = time.getText().toString();

            if (chechkInputFields(txtTitle, txtStudent, txtDate, txtTime)) {
                saveEvent(txtTitle, txtStudent, txtDate, txtTime);
            }
        });
    }

    private void saveEvent(String subject, String studentName, String date, String time) {
        pd.setMessage("Spremanje...");
        pd.show();

        HashMap<String, Object> event = new HashMap<>();

        event.put("subject", subject);
        event.put("studentReceiver", studentID);
        event.put("studentName", studentName);
        event.put("teacherID", teacherID);
        event.put("teacherIme", teacherName);
        event.put("date", date);
        event.put("time", time);

        eventCollection.add(event).addOnSuccessListener(documentReference -> {
            String eventID = documentReference.getId();
            documentReference.update("eventID", eventID);
            pd.dismiss();
            Toast.makeText(NewEventActivity.this, "Događaj spremljen.", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(NewEventActivity.this, "Pogreška prilikom spremanja događaja, pokušajte kasnije.", Toast.LENGTH_SHORT).show();
        });

        Intent addToCalendar = new Intent(Intent.ACTION_INSERT);
        addToCalendar.setData(CalendarContract.Events.CONTENT_URI);
        addToCalendar.putExtra(CalendarContract.Events.TITLE, subject);
        addToCalendar.putExtra(CalendarContract.Events.DTSTART, time);
    }

    private boolean chechkInputFields(String subject, String student, String date, String time) {
        if (subject.isEmpty() || student.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void timeInput() {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.getTime().getHours();
        int minute = cal.getTime().getMinutes();

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewEventActivity.this,
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventActivity.this,
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

    public void getClientInfo() {
        studentDocRef.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                studentName = task.getResult().getString("fullName");
                student.setText(studentName);
            }
        }).addOnFailureListener(e -> Toast.makeText(NewEventActivity.this, "Korisnik ne postoji.", Toast.LENGTH_SHORT).show());
    }

    public void getUserInfo() {
        teacherDocRef.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                teacherName = task.getResult().getString("fullName");
                userType = task.getResult().getString("userType");
            }
        }).addOnFailureListener(e -> Toast.makeText(NewEventActivity.this, "Korisnik nije pronađen.", Toast.LENGTH_SHORT).show());
    }
}