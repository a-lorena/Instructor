package com.example.instrukcije;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadUserActivity extends AppCompatActivity {

	FirebaseAuth auth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_user);

		auth = FirebaseAuth.getInstance();

		DocumentReference df = FirebaseFirestore.getInstance().collection("Users")
				.document(FirebaseAuth.getInstance().getCurrentUser().getUid());
		df.get().addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.getString("userType").equals("Instruktor")) {
				startActivity(new Intent(LoadUserActivity.this, MainActivityInstructors.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
			} else if (documentSnapshot.getString("userType").equals("Učenik")) {
				startActivity(new Intent(LoadUserActivity.this, MainActivityStudents.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
			}
		});
	}
}