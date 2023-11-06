package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Switch;

import androidx.core.content.ContextCompat;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.pp_android_handheld_library.view.clases.IconGeneric;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;

import java.util.ArrayList;

public class ArchivosAdapter extends BaseAdapter {

    private Context context;
    private class ViewHolder {
        TextView nombreTv;
        IconGeneric viewIcon;
        IconGeneric editIcon;
        IconGeneric filtroIcon;
        IconGeneric indexIcon;
        IconGeneric keyIcon;
    }

    public ArrayList<Encabezados> encabezadosArrayList;

    public void cambiarEncabezados(ArrayList<Encabezados> encabezados) {
        this.encabezadosArrayList = encabezados;
    }

    public ArchivosAdapter(Context context) {
        this.context = context;
        this.encabezadosArrayList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return encabezadosArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return encabezadosArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return encabezadosArrayList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_encabezados, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nombreTv = rowView.findViewById(R.id.nombreEnc);
            viewHolder.viewIcon = rowView.findViewById(R.id.viewIcon);
            viewHolder.editIcon = rowView.findViewById(R.id.editIcon);
            viewHolder.filtroIcon = rowView.findViewById(R.id.filtroIcon);
            viewHolder.indexIcon = rowView.findViewById(R.id.indexIcon);
            viewHolder.keyIcon = rowView.findViewById(R.id.keyIcon);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nombreTv.setText(encabezadosArrayList.get(position).getNombre());
        if(encabezadosArrayList.get(position).isVisible()) {
            viewHolder.viewIcon.setText(context.getResources().getString(R.string.check));
            viewHolder.viewIcon.setTextColor(ContextCompat.getColor(context, R.color.bien));
        } else {
            viewHolder.viewIcon.setText(context.getResources().getString(R.string.times));
            viewHolder.viewIcon.setTextColor(ContextCompat.getColor(context, R.color.sobrante));
        }
        if(encabezadosArrayList.get(position).isEditable()) {
            viewHolder.editIcon.setText(context.getResources().getString(R.string.check));
            viewHolder.editIcon.setTextColor(ContextCompat.getColor(context, R.color.bien));
        } else {
            viewHolder.editIcon.setText(context.getResources().getString(R.string.times));
            viewHolder.editIcon.setTextColor(ContextCompat.getColor(context, R.color.sobrante));
        }
        if(encabezadosArrayList.get(position).isFiltro()) {
            viewHolder.filtroIcon.setText(context.getResources().getString(R.string.check));
            viewHolder.filtroIcon.setTextColor(ContextCompat.getColor(context, R.color.bien));
        } else {
            viewHolder.filtroIcon.setText(context.getResources().getString(R.string.times));
            viewHolder.filtroIcon.setTextColor(ContextCompat.getColor(context, R.color.sobrante));
        }
        if(encabezadosArrayList.get(position).isIndexado()) {
            viewHolder.indexIcon.setText(context.getResources().getString(R.string.check));
            viewHolder.indexIcon.setTextColor(ContextCompat.getColor(context, R.color.bien));
        } else {
            viewHolder.indexIcon.setText(context.getResources().getString(R.string.times));
            viewHolder.indexIcon.setTextColor(ContextCompat.getColor(context, R.color.sobrante));
        }
        if(encabezadosArrayList.get(position).isLlavePrimaria()) {
            viewHolder.keyIcon.setText(context.getResources().getString(R.string.check));
            viewHolder.keyIcon.setTextColor(ContextCompat.getColor(context, R.color.bien));
        } else {
            viewHolder.keyIcon.setText(context.getResources().getString(R.string.times));
            viewHolder.keyIcon.setTextColor(ContextCompat.getColor(context, R.color.sobrante));
        }
        return rowView;
    }
}
