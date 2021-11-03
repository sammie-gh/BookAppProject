package com.example.bookappproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookappproject.databinding.ActivityPdfAddBinding;
import com.example.bookappproject.models.ModelCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {
    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = " ADD_PDF_TAG";
    private static final int PICK_CODE_PDF = 1000;

    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;
    //url of picked doc
    private Uri pdfUri = null;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadPdfCategories();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });

        binding.submintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();

            }
        });

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPickDialog();
            }
        });
    }


    private String title = "", description = "";
    private void validateData() {
        //step 1: validate data
        Log.d(TAG, "validateData: validating data...");

        //get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();


        //validate data
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "select a category...", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pls select pdf...", Toast.LENGTH_SHORT).show();
        } else {
            // add is ok
            uploadPdfToStorage();
        }

    }

    private void uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: uploading to storage");
        progressDialog.setMessage("Uploading pdf ...");
        progressDialog.show();

        //timestamp
        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Books/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: pdf uploaded to storage");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadPdfUrl = "" + uriTask.getResult();

                        //upload to firebase
                        uploadPdfInfoToDatabase(uploadPdfUrl, timestamp);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PdfAddActivity.this, "pdf upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void uploadPdfInfoToDatabase(String uploadPdfUrl, long timestamp) {
        //step 3
        Log.d(TAG, "uploadPdfToStorage: uploading to info to database");
        progressDialog.setMessage("Uploading pdf info...");

        String uid = firebaseAuth.getUid();
        //setup data load
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", "" + title);
        hashMap.put("description", "" + description);
        hashMap.put("categoryId", "" + ""+selectedCategoryId);
        hashMap.put("url", "" + uploadPdfUrl);
        hashMap.put("viewsCount", 0);
        hashMap.put("downloadsCount", 0);

        //db book ref
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.keepSynced(true);
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded");
                        Toast.makeText(PdfAddActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();

                        //call finish
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: failed to save to DB" + e.getMessage());
                Toast.makeText(PdfAddActivity.this, "failed to save to DB " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: loading pdf categories");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear(); // clear before adding data
                categoryIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle = ""+ds.child("category").getValue();

                    //add to respective arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //selected category id and category title
    private String selectedCategoryId,selectedCategoryTitle;
    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");

        //get string array of categories from arraylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i < categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        //    alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle item click
                        // get clicked item from list
                        selectedCategoryTitle= categoryTitleArrayList.get(which);
                        selectedCategoryId= categoryIdArrayList.get(which);
                        binding.categoryTv.setText(selectedCategoryTitle);

                        Log.d(TAG, "onClick: Selected Category:" + selectedCategoryId+" "+selectedCategoryTitle);

                    }
                }).show();


    }

    private void pdfPickIntent() {
        Log.e(TAG, "pdfPickIntent: starting pdf pick ");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_CODE_PDF);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_CODE_PDF) {
                Log.d(TAG, "onActivityResult: PDF PICKED");
                pdfUri = data.getData();

                Log.d(TAG, "onActivityResult: URI: " + pdfUri);
            }
        } else {

            Log.d(TAG, "onActivityResult: cancelled picking");
            Toast.makeText(PdfAddActivity.this, "cancelled pdf picking", Toast.LENGTH_SHORT).show();
        }
    }
}