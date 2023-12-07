package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.view.fragment.FiltrosFragment;

import java.util.ArrayList;

public class FiltrosAdapter extends RecyclerView.Adapter<FiltrosAdapter.ViewHolder> {
    public ArrayList<String> filtros;
    private FiltrosFragment.OnItemClickListener listener;
    private Context context;

    public FiltrosAdapter(Context context, ArrayList<String> filtros) {
        this.context = context;
        this.filtros = filtros;
    }

    public void setOnItemClickListener(FiltrosFragment.OnItemClickListener listener) {
        this.listener = listener;
    }

    public boolean agregarFiltro(String valor) {
        boolean found = false;
        for (String val : filtros) {
            if (val.compareTo(valor) == 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            filtros.add(valor);
        }
        return found;
    }

    public void borrarFiltro(int position) {
        filtros.remove(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textText;
        LinearLayout fondo;

        public ViewHolder(View itemView) {
            super(itemView);
            textText = itemView.findViewById(R.id.valorFiltro);
            fondo = itemView.findViewById(R.id.lista_back);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_filtros, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textText.setText(filtros.get(position));
        if (position % 2 == 0) {
            holder.fondo.setBackgroundColor(ContextCompat.getColor(context, R.color.filaGris));
        } else {
            holder.fondo.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return filtros.size();
    }
}