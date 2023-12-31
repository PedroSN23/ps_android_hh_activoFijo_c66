package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.RectangleEnum;
import com.example.pp_android_handheld_library.view.clases.IconGeneric;

import java.util.ArrayList;

public class SgtinAdapter extends RecyclerView.Adapter<SgtinAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<UHFTagsRead> uhfTagsReads;

    public SgtinAdapter(Context context, ArrayList<UHFTagsRead> uhfTagsReads) {
        this.context = context;
        this.uhfTagsReads = uhfTagsReads;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.simple_list_item_validacion, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.iconGeneric.setText(context.getResources().getString(uhfTagsReads.get(position).getTipo().getCode()));
        holder.iconGeneric.setTextColor(ContextCompat.getColor(context, uhfTagsReads.get(position).getTipo().getColor()));
        holder.titDesc.setText("EPC");
        holder.layoutCantidades.setVisibility(View.GONE);
        holder.desc.setText(uhfTagsReads.get(position).getEpc());
        switch (uhfTagsReads.get(position).getInventariado()) {
            case -1:
                holder.parent.setBackground(ContextCompat.getDrawable(context, RectangleEnum.sobrantes.getCodigo()));
                holder.resultadoIcon.setText(context.getResources().getString(IconGenericEnum.fontawesome_times_circle.getCode()));
                holder.resultadoIcon.setTextColor(ContextCompat.getColor(context, ColorEnum.sobrante.getCode()));
                break;
            case 0:
                holder.parent.setBackground(ContextCompat.getDrawable(context, RectangleEnum.faltantes.getCodigo()));
                holder.resultadoIcon.setText(context.getResources().getString(IconGenericEnum.fontawesome_question_circle.getCode()));
                holder.resultadoIcon.setTextColor(ContextCompat.getColor(context, ColorEnum.faltante.getCode()));
                break;
            case 1:
                holder.parent.setBackground(ContextCompat.getDrawable(context, RectangleEnum.correctos.getCodigo()));
                holder.resultadoIcon.setText(context.getResources().getString(IconGenericEnum.fontawesome_check_circle.getCode()));
                holder.resultadoIcon.setTextColor(ContextCompat.getColor(context, ColorEnum.bien.getCode()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return uhfTagsReads.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        IconGeneric iconGeneric;
        TextView desc;
        TextView titDesc;
        LinearLayout layoutCantidades;
        TextView cantText;
        TextView espText;
        IconGeneric resultadoIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parentLl);
            iconGeneric = itemView.findViewById(R.id.icon_type);
            desc = itemView.findViewById(R.id.descTv);
            titDesc = itemView.findViewById(R.id.descTitTv);
            layoutCantidades = itemView.findViewById(R.id.cantidadLl);
            cantText = itemView.findViewById(R.id.cantTv);
            espText = itemView.findViewById(R.id.espTv);
            resultadoIcon = itemView.findViewById(R.id.selicon);
        }
    }
}
