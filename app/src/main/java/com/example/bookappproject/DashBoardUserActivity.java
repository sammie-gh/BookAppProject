package com.example.bookappproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.bookappproject.databinding.ActivityDashBoardBinding;
import com.example.bookappproject.fragment.BooksUserFragment;
import com.example.bookappproject.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashBoardUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityDashBoardBinding binding;

    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        setupViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(DashBoardUserActivity.this,
                        MainActivity.class));
                finish();
            }
        });

    }

    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);

        categoryArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                ModelCategory modeAll = new ModelCategory("01", "All", "", 1);
                ModelCategory modelMostViewed = new ModelCategory("02", "Most Viewed", "", 1);
                ModelCategory modelMostDownloaded = new ModelCategory("03", "Most Downloaded", "", 1);

                //add models to list
                categoryArrayList.add(modeAll);
                categoryArrayList.add(modelMostViewed);
                categoryArrayList.add(modelMostDownloaded);

                //add data to view pager adapter
                viewPagerAdapter.addFragments(BooksUserFragment.newInstance(
                        "" + modeAll.getId(),
                        "" + modeAll.getCategory(),
                        "" + modeAll.getUid()
                ), modeAll.getCategory());

                viewPagerAdapter.addFragments(BooksUserFragment.newInstance(
                        "" + modelMostViewed.getId(),
                        "" + modelMostViewed.getCategory(),
                        "" + modelMostViewed.getUid()
                ), modelMostViewed.getCategory());

                viewPagerAdapter.addFragments(BooksUserFragment.newInstance(
                        "" + modelMostDownloaded.getId(),
                        "" + modelMostDownloaded.getCategory(),
                        "" + modelMostDownloaded.getUid()
                ), modelMostDownloaded.getCategory());

                //refresh list
                viewPagerAdapter.notifyDataSetChanged();

                // now load from firebase
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    //add data to list
                    categoryArrayList.add(model);
                    //add data to viewpager
                    viewPagerAdapter.addFragments(BooksUserFragment.newInstance(
                            "" + model.getId(),
                            "" + model.getCategory(),
                            "" + model.getUid()), model.getCategory());
                    //refresh list
                    viewPagerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set adapter to view pager
        viewPager.setAdapter(viewPagerAdapter);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<BooksUserFragment> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        private void addFragments(BooksUserFragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);

        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            binding.subTitleTv.setText("Not Logged In");
        } else {
            String email = firebaseUser.getEmail();
            binding.subTitleTv.setText(email);
        }
    }
}