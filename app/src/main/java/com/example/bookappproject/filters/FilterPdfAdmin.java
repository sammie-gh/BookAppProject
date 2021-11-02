package com.example.bookappproject.filters;

import android.widget.Filter;

import com.example.bookappproject.adapters.AdapterCategory;
import com.example.bookappproject.adapters.AdapterPdfAdmin;
import com.example.bookappproject.models.ModelCategory;
import com.example.bookappproject.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be implemented
    AdapterPdfAdmin adapterPdfAdmin;

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null or empty

        if (constraint != null && constraint.length() > 0) {
            //change to upper case or lower case too avaoid case sens.
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {

                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)) {
                    // add filter to list
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
        //apply filter
        adapterPdfAdmin.pdfArrayList = (ArrayList<ModelPdf>) results.values;

        //notify changes
        adapterPdfAdmin .notifyDataSetChanged();


    }
}
