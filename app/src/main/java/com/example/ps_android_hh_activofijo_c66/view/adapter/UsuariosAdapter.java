package com.example.ps_android_hh_activofijo_c66.view.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Usuario;

import java.util.ArrayList;
import java.util.Arrays;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {
    private final ArrayList<Usuario> usuarios;
    private OnClickListener onClickListener;
    private final boolean[] swipeEnabled;


    public UsuariosAdapter(ArrayList<Usuario> usuariosArraylist) {
        this.usuarios = usuariosArraylist;
        swipeEnabled = new boolean[usuarios.size()]; // Inicialmente, todos los elementos son deslizables
        Arrays.fill(swipeEnabled, true);
        swipeEnabled[0] = false;
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usuario;
        TextView rol;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View view) {
            super(view);
            usuario = view.findViewById(R.id.user);
            rol = view.findViewById(R.id.rol);

            itemView.setOnTouchListener((v, event) -> {
                return !swipeEnabled[getAdapterPosition()]; // Consume el evento táctil si el deslizamiento está deshabilitado
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_usuarios, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.usuario.setText(String.valueOf(usuarios.get(position).getUsuario()));

        if (position % 2 == 0) {
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#F5F0F0"));
        } else {
            // Reset the background color for even cells
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }

        switch (usuarios.get(position).getRol()) {
            case 1:
                viewHolder.rol.setText(R.string.root);
                break;
            case 2:
                viewHolder.rol.setText(R.string.administrador);
                break;
            case 3:
                viewHolder.rol.setText(R.string.operador);
                break;
        }

        viewHolder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position);
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public void removeItem(int position) {
        usuarios.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Usuario item, int position) {
        usuarios.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<Usuario> getData() {
        return usuarios;
    }

}
