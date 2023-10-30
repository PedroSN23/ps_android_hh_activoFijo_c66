package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.view.clases.IconGeneric;
import java.util.ArrayList;
import java.util.Locale;

public class RFIDAdapter extends RecyclerView.Adapter<RFIDAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<UHFTagsRead> uhfTagsFiltered;

    public RFIDAdapter(Context context, ArrayList<UHFTagsRead> uhfTagsFiltered) {
        this.context = context;
        this.uhfTagsFiltered = uhfTagsFiltered;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.simple_list_item_rfid, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.epcText.setText(uhfTagsFiltered.get(position).getEpc());
        holder.desc.setText(String.format(Locale.getDefault(), "%s",
                uhfTagsFiltered.get(position).getDetail()));
        holder.iconGeneric.setText(context.getResources().getString(uhfTagsFiltered.get(position).getTipo().getCode()));
        holder.iconGeneric.setTextColor(ContextCompat.getColor(context, uhfTagsFiltered.get(position).getTipo().getColor()));
        holder.countText.setText(String.format(Locale.getDefault(), "%d", uhfTagsFiltered.get(position).getCantidad()));
        holder.rssiText.setText(uhfTagsFiltered.get(position).getRssi());
    }

    @Override
    public int getItemCount() {
        return uhfTagsFiltered.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView desc;
        TextView epcText;
        TextView countText;
        TextView rssiText;
        IconGeneric iconGeneric;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            epcText = itemView.findViewById(R.id.epcText);
            desc = itemView.findViewById(R.id.detail);
            countText = itemView.findViewById(R.id.countText);
            rssiText = itemView.findViewById(R.id.rssiText);
            iconGeneric = itemView.findViewById(R.id.icon_type);
        }
    }
}
