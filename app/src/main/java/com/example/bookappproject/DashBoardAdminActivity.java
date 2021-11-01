package com.example.bookappproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookappproject.adapters.AdapterCategory;
import com.example.bookappproject.databinding.ActivityDashBoardAdminBinding;
import com.example.bookappproject.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashBoardAdminActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityDashBoardAdminBinding binding;
    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategory();

        //search cat...
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            //called  as when user type each later

                try {
                    adapterCategory.getFilter().filter(s);
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardAdminActivity.this, CategoryAddActivity.class));
            }
        });

        binding.addPdfFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardAdminActivity.this, PdfAddActivity.class));
            }
        });
    }

    private void loadCategory() {
        categoryArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);
                }
                adapterCategory = new AdapterCategory(DashBoardAdminActivity.this, categoryArrayList);
                //set adapter
                binding.categoriesRv.setAdapter(adapterCategory);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            binding.subTitleTv.setText(email);
        }
    }
}