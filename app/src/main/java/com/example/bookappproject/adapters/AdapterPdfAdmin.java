package com.example.bookappproject.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookappproject.MyApplication;
import com.example.bookappproject.PdfDetailActivity;
import com.example.bookappproject.PdfEditActivity;
import com.example.bookappproject.databinding.RowPdfAdminBinding;
import com.example.bookappproject.filters.FilterPdfAdmin;
import com.example.bookappproject.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList, filterList;
    private RowPdfAdminBinding binding;
    private static final String TAG = "PDF_ADAPTER_TAG";
    private FilterPdfAdmin filter;
    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();
        //we need to convert timestamp to dd/mm/yyyy format
        String formattedDate = MyApplication.formartTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        //load further details like category pdf , from url pdf size in seperate function
        MyApplication.loadCategory(
                "" + categoryId,
                holder.categoryTv);

        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfView,
                holder.progressBar,
                null);

        MyApplication.loadPdfSize(
                "" + pdfUrl,
                "" + title,
                holder.sizeTv);


        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", pdfId);
                context.startActivity(intent);

            }
        });

    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //edit clicked
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);

                        } else if (which == 1) {
                            //delete
                            MyApplication.deleteBook(context,
                                    "" + bookId,
                                    "" + bookUrl,
                                    "" + bookTitle);
                        }


                    }
                }).show();
    }


    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPdfAdmin(filterList, this);

        }
        return filter;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder {
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, dateTv, sizeTv;
        ImageButton moreBtn;


        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTV;
            categoryTv = binding.categoryTV;
            sizeTv = binding.sizeTV;
            dateTv = binding.dateTV;
            moreBtn = binding.moreBtn;


        }
    }
}
