package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Archivos;

import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<Archivos> archivosArrayList;

    public FilesAdapter(Context context, ArrayList<Archivos> archivos) {
        this.context = context;
        this.archivosArrayList = archivos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_files, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileTv.setText(archivosArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return archivosArrayList.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fileTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fileTv = itemView.findViewById(R.id.filename);
        }
    }
}