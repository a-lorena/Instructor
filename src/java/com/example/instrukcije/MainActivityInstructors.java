package com.example.instrukcije;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instrukcije.Fragments.ChatsFragment;
import com.example.instrukcije.Fragments.EventsInstructorFragment;
import com.example.instrukcije.Fragments.RatingsFragment;
import com.example.instrukcije.Fragments.AdsInstructorsFragment;
import com.example.instrukcije.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityInstructors extends AppCompatActivity {

    BottomNavigationView bottomNav;

    FirebaseUser user;
    String profileID;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_instructor);

        bottomNav = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.adsInstructorsFragment);

        user = FirebaseAuth.getInstance().getCurrentUser();
        profileID = user.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("Users").document(profileID);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = new AdsInstructorsFragment();

                    switch (item.getItemId()) {
                        case R.id.adsInstructorsFragment:
                            selectedFragment = new AdsInstructorsFragment();
                            break;

                        case R.id.ratingsFragment:
                            selectedFragment = new RatingsFragment();
                            break;

                        case R.id.messagesFragment:
                            selectedFragment = new ChatsFragment("Instruktor");
                            break;

                        case R.id.calendarFragment:
                            selectedFragment = new EventsInstructorFragment();
                            break;

                        case R.id.profileFragment:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                            selectedFragment).commit();

                    return true;
                }
            };

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}