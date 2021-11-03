package com.example.bookappproject.filters;

import android.widget.Filter;

import com.example.bookappproject.adapters.AdapterPdfUser;
import com.example.bookappproject.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be implemented
    AdapterPdfUser adapterPdfUser;

    //constructor

    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value to be seacrhed
        if (constraint != null || constraint.length() > 0) {
            // not null nor empty
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)) {
                    filterModels.add(filterList.get(i));

                }
            }
            results.count = filterModels.size();
            results.values = filterModels;

        } else {
            results.count = filterList.size();
            results.values = filterList;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>) results.values;
        //notify changes
        adapterPdfUser.notifyDataSetChanged();

    }
}
