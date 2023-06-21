package com.example.instrukcije.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instrukcije.LoginActivity;
import com.example.instrukcije.R;
import com.example.instrukcije.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    TextView fullName, email, education, subjects, price, available, location, instrukcijeInfoTxt, additionalInfoTxt;
    ConstraintLayout instrukcijeInfo, additionalInfo;
    ImageButton settings;
    Button logout;
    RatingBar rating;

    FirebaseUser user;
    String profileID;
    FirebaseFirestore firestore;
    DocumentReference docRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fullName = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        education = view.findViewById(R.id.education);
        subjects = view.findViewById(R.id.subjects);
        price = view.findViewById(R.id.price);
        rating = view.findViewById(R.id.rating);
        available = view.findViewById(R.id.availability);
        location = view.findViewById(R.id.location);
        settings = view.findViewById(R.id.settingsButton);
        logout = view.findViewById(R.id.signOutButton);
        instrukcijeInfoTxt = view.findViewById(R.id.instructionsInfoTxt);
        instrukcijeInfo = view.findViewById(R.id.instructionsInfo);
        additionalInfoTxt = view.findViewById(R.id.additionalInfoTxt);
        additionalInfo = view.findViewById(R.id.additionalInfo);

        user = FirebaseAuth.getInstance().getCurrentUser();
        profileID = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("Users").document(profileID);

        userInformation();

        settings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(getContext(), "Uspješno ste se odjavili!", Toast.LENGTH_SHORT).show();
            Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            getActivity().finish();
        });

        return view;
    }

    private void userInformation() {
        docRef.get().addOnCompleteListener(task -> {
            if(task.getResult().exists()) {
                String userTypeResult = task.getResult().getString("userType");
                String fullNameResult = task.getResult().getString("fullName");
                String emailResult = task.getResult().getString("email");
                String educationResult = task.getResult().getString("education");
                String locationResult = task.getResult().getString("location");

                fullName.setText(fullNameResult);
                email.setText(emailResult);
                education.setText(educationResult);
                location.setText(locationResult);

                if (userTypeResult.equals("Instruktor")) {
                    String subjectsResult = task.getResult().getString("subjects");
                    long priceResult = task.getResult().getLong("price");
                    long ratingResult = task.getResult().getLong("rating");
                    boolean availableResult = task.getResult().getBoolean("available");

                    String priceTxt = priceResult + " kn/h";

                    subjects.setText(subjectsResult);
                    price.setText(priceTxt);
                    rating.setRating(ratingResult);

                    if (availableResult) {
                        available.setText("Dostupan");
                    } else {
                        available.setText("Nedostupan");
                    }

                    instrukcijeInfo.setVisibility(View.VISIBLE);
                    instrukcijeInfoTxt.setVisibility(View.VISIBLE);
                    additionalInfo.setVisibility(View.VISIBLE);
                    additionalInfoTxt.setVisibility(View.VISIBLE);

                }

            } else {
                Toast.makeText(getActivity(), "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}