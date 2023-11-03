package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.adapter.FiltrosAdapter;

import java.util.regex.Pattern;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FiltrosFragment extends Fragment {
    private InterfazBD interfazBD;
    private FiltrosAdapter FiltrosAdapter;
    private Pattern p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filtros_lfragment, container, false);

        p = Pattern.compile("^[A-Za-z]{1,9}$");

        RecyclerView recyclerView = rootView.findViewById(R.id.listViewFiltros);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayout nuevoFiltroButton = rootView.findViewById(R.id.butNuevoFiltro);

        nuevoFiltroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NuevoFiltro(view);
            }
        });

        interfazBD = new InterfazBD(getActivity());
        FiltrosAdapter = new FiltrosAdapter(getActivity(), interfazBD.obtenerFiltros());
        recyclerView.setAdapter(FiltrosAdapter);

        // Agrega el deslizamiento hacia la derecha
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String filtroEliminado = FiltrosAdapter.filtros.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("¿Desea eliminar el filtro " + filtroEliminado + "?")
                        .setCancelable(false)
                        .setPositiveButton("Confirmar", (dialogInterface, i) -> {
                            interfazBD.eliminarFiltro(filtroEliminado);
                            FiltrosAdapter.borrarFiltro(position);
                            FiltrosAdapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                            FiltrosAdapter.notifyDataSetChanged();
                            dialogInterface.cancel();
                        });

                final AlertDialog alert = builder.create();
                alert.setOnShowListener(arg0 -> {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.azulAstlix));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.azulAstlix));
                });
                alert.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.sobrante.getCode()))
                        .addSwipeLeftActionIcon(R.drawable.ic_trash)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }
    public void SalirConfigFiltros(View view) {
        requireActivity().finish();
    }

    public void NuevoFiltro(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View dialogView = View.inflate(getActivity(), R.layout.dialog_filtro_nuevo, null);
        alertDialog.setView(dialogView);
        final EditText etFilt = dialogView.findViewById(R.id.editFiltro);

        LinearLayout butG = dialogView.findViewById(R.id.digGuardarFilt);
        LinearLayout butC = dialogView.findViewById(R.id.diagCancelFilt);
        final AlertDialog dialog = alertDialog.create();

        butG.setOnClickListener(view1 -> {
            String f = etFilt.getText().toString();
            if (!f.isEmpty()) {
                if (p.matcher(f).matches()) {
                    if (!FiltrosAdapter.agregarFiltro(f)) {
                        FiltrosAdapter.notifyDataSetChanged();
                        interfazBD.insertarFiltro(f);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.errFitlRep), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.errFiltFormat), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.errFiltEmpty), Toast.LENGTH_LONG).show();
            }
        });

        butC.setOnClickListener(view12 -> dialog.dismiss());

        dialog.show();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
