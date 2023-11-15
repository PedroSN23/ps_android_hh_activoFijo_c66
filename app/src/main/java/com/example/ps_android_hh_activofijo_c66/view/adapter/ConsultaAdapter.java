package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Activos;

import java.util.List;

public class ConsultaAdapter extends ArrayAdapter<Activos> {
    private LayoutInflater inflater;
    public ConsultaAdapter(Context context, int resourceId, int textViewId, List<Activos> catalogos) {
        super(context, resourceId, textViewId, catalogos);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner, parent, false);
        }
        Activos catalogo = getItem(position);
        TextView textTitle = convertView.findViewById(R.id.textSpinner);
        if (catalogo != null) {
            textTitle.setText(catalogo.toString());
        }
        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner, parent, false);
        }
        Activos catalogo = getItem(position);
        TextView textTitle = convertView.findViewById(R.id.textSpinner);
        if (catalogo != null) {
            textTitle.setText(catalogo.toString());
        }
        return convertView;
    }
}
