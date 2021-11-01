package com.example.bookappproject.model;

import android.widget.Filter;

import com.example.bookappproject.adapters.AdapterCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelCategory> filterList;
    //adapter in which filter need to be implemented
    AdapterCategory adapterCategory;

    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null or empty

        if (constraint != null && constraint.length() > 0) {
            //change to upper case or lower case too avaoid case sens.
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filterModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {

                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)){
                    // add filter to list
                   filterModels.add(filterList.get(i));

                }

            }
            results.count = filterModels.size();
            results.values = filterModels;


        }else {
            results.count = filterList.size();
            results.values  = filterList;
        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter
        adapterCategory.categoryArrayList =  (ArrayList<ModelCategory>)results.values;

        //notify changes
        adapterCategory.notifyDataSetChanged();


    }
}
