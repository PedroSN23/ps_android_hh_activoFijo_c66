package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.BarcodeData;

import java.util.ArrayList;
import java.util.Locale;

public class BarcodeAdapter extends RecyclerView.Adapter<BarcodeAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<BarcodeData> barcodeDataArrayList;

    public BarcodeAdapter(Context context, ArrayList<BarcodeData> uhfTagsReadArrayList) {
        this.context = context;
        this.barcodeDataArrayList = uhfTagsReadArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.simple_list_item_barcode, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bcText.setText(barcodeDataArrayList.get(position).getValue());
        holder.typeText.setText(barcodeDataArrayList.get(position).getName());
        holder.countText.setText(String.format(Locale.getDefault(), "%d", barcodeDataArrayList.get(position).getCant()));
    }

    @Override
    public int getItemCount() {
        return barcodeDataArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public boolean sentTagsAlt(BarcodeData uhfTagsRead) {
        boolean found = false;
        for(BarcodeData tr: barcodeDataArrayList) {
            if(tr.getValue().compareTo(uhfTagsRead.getValue())==0) {
                tr.addCant();
                found = true;
                break;
            }
        }
        if(!found) {
            barcodeDataArrayList.add(uhfTagsRead);
        }

        barcodeDataArrayList.sort((tagsRead, t1) -> tagsRead.compareTo(t1.getCant()));

        notifyDataSetChanged();
        return !found;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView typeText;
        TextView bcText;
        TextView countText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bcText = itemView.findViewById(R.id.bcText);
            typeText = itemView.findViewById(R.id.typeText);
            countText = itemView.findViewById(R.id.countText);
        }
    }
}
