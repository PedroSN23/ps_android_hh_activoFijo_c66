package com.example.ps_android_hh_activofijo_c66.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.view.adapter.SgtinAdapter;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DetalleValidacionDialog extends DialogFragment {
    @SuppressLint("StaticFieldLeak")
    static Context mContext;
    static DetalleValidacionDialogListener listener;
    private ArrayList<UHFTagsRead> uhfTagsReads;
    private SgtinAdapter sgtinAdapter;

    private RelativeLayout padre;
    private int posGlob;

    public static DetalleValidacionDialog newInstance(Context context, ArrayList<UHFTagsRead> uhfTagsReads, int pos) {
        mContext = context;
        DetalleValidacionDialog frag = new DetalleValidacionDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("lista", uhfTagsReads);
        args.putInt("pos", pos);
        frag.setArguments(args);
        return frag;
    }

    private DetalleValidacionDialog() {
    }

    @SuppressWarnings("AccessStaticViaInstance")
    public void addDetalleValidacionDialogListener(DetalleValidacionDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(mContext, R.layout.sgtin_dialog, null);

        assert getArguments() != null;
        uhfTagsReads = getArguments().getParcelableArrayList("lista");
        posGlob = getArguments().getInt("pos");

        padre = view.findViewById(R.id.padre);

        RecyclerView listView= view.findViewById(R.id.listProd);
        sgtinAdapter = new SgtinAdapter(getActivity(), uhfTagsReads);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(sgtinAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(listView);

        Button button = view.findViewById(R.id.button2);
        button.setOnClickListener(view1 -> {
            if (listener != null) {
                listener.closeDialog(posGlob);
            }
        });

        alertDialogBuilder.setView(view);

        return alertDialogBuilder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        int pos;
        UHFTagsRead tagsRead;
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int init = viewHolder.getAbsoluteAdapterPosition();
            int fin = target.getAbsoluteAdapterPosition();

            Collections.swap(uhfTagsReads, init, fin);

            sgtinAdapter.notifyItemMoved(init, fin);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            pos = viewHolder.getAbsoluteAdapterPosition();
            if(direction==ItemTouchHelper.LEFT) {
                switch (uhfTagsReads.get(pos).getInventariado()) {
                    case 0:
                        break;
                    case -1:
                        if(uhfTagsReads.size()<=1) {
                            if(listener!=null) {
                                listener.itemRemoved(posGlob);
                            }
                        } else {
                            tagsRead = uhfTagsReads.remove(pos);
                            sgtinAdapter.notifyItemRemoved(pos);
                            showSnackBar();
                        }
                        break;
                    case 1:
                        uhfTagsReads.get(pos).setInventariado(0);
                        sgtinAdapter.notifyItemChanged(pos);
                        break;
                }
            } else {
                if(listener!=null) {
                    listener.buscarTag(uhfTagsReads.get(pos).getEpc(), posGlob);
                }
                sgtinAdapter.notifyItemChanged(pos);
            }
        }

        private void showSnackBar() {
            Snackbar snackbar = Snackbar.make(padre, "Tag Borrado!", Snackbar.LENGTH_LONG);
            snackbar.setDuration(4000);
            snackbar.setAction(R.string.deshacer, view -> {
                uhfTagsReads.add(pos, tagsRead);
                sgtinAdapter.notifyItemInserted(pos);
            });
            snackbar.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int p = viewHolder.getAbsoluteAdapterPosition();
            if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE) {
                if(dX<0.0) {
                    if (p >= 0 && p < uhfTagsReads.size()) {
                        switch (uhfTagsReads.get(p).getInventariado()) {
                            case 0:
                                break;
                            case -1:
                                drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.sobrante.getCode(), R.drawable.ic_trash);
                                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                break;
                            case 1:
                                drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart);
                                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                break;
                        }
                    }
                } else {
                    drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @SuppressWarnings("ConstantConditions")
        private void drawSwipeDecorator(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive, int colorL, int drawableL) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), colorL))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.status_blue.getCode()))
                    .addSwipeLeftActionIcon(drawableL)
                    .addSwipeRightActionIcon(R.drawable.ic_search)
                    .create()
                    .decorate();
        }
    };
}
