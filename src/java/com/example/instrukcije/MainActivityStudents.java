package com.example.instrukcije;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.instrukcije.Fragments.ChatsFragment;
import com.example.instrukcije.Fragments.EventsStudentFragment;
import com.example.instrukcije.Fragments.InstructorsFragment;
import com.example.instrukcije.Fragments.AdsStudentsFragment;
import com.example.instrukcije.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityStudents extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ImageButton editPost;

    FirebaseUser user;
    String profileID;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

        bottomNav = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.adsStudentsFragment);

        editPost = (ImageButton) findViewById(R.id.editPostButton);

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
                    Fragment selectedFragment = new AdsStudentsFragment();

                    switch (item.getItemId()) {
                        case R.id.adsStudentsFragment:
                            selectedFragment = new AdsStudentsFragment();
                            break;

                        case R.id.instructorsFragment:
                            selectedFragment = new InstructorsFragment();
                            break;

                        case R.id.porukeStudentFragment:
                            selectedFragment = new ChatsFragment("Učenik");
                            break;

                        case R.id.calendarFragment:
                            selectedFragment = new EventsStudentFragment();
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