package com.example.bookappproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookappproject.MyApplication;
import com.example.bookappproject.PdfDetailActivity;
import com.example.bookappproject.databinding.RowPdfUserBinding;
import com.example.bookappproject.filters.FilterPdfUser;
import com.example.bookappproject.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private Context context;
    private FilterPdfUser filter;
    private RowPdfUserBinding binding;
    private static final String TAG = "ADAPTER_PDF_USER";

    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String title = model.getTitle();
        String bookId = model.getId();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        long timestamp = model.getTimestamp();

        //convert time
        String date = MyApplication.formartTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(date);


        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfView,
                holder.progressBar,
                null);

        MyApplication.loadCategory("" + categoryId, holder.categoryTv);


        MyApplication.loadPdfSize("" + pdfUrl, "" + title, holder.sizeTv);

        //handle click show pdf details activities
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", bookId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter ==null){
            filter = new FilterPdfUser(filterList,this);
        }
        return filter;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder {
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        PDFView pdfView;
        ProgressBar progressBar;

        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTV;
            dateTv = binding.dateTV;
            categoryTv = binding.categoryTV;
            sizeTv = binding.sizeTV;
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;


        }
    }
}
