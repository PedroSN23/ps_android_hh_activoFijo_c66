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
import com.example.ps_android_hh_activofijo_c66.model.clases.Archivos;
import com.example.ps_android_hh_activofijo_c66.view.adapter.FilesAdapter;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FilesDialog extends DialogFragment {
    @SuppressLint("StaticFieldLeak")
    static Context mContext;
    static FilesDialogListener listener;
    private FilesAdapter filesAdapter;
    private ArrayList<Archivos> archivosArrayList;
    private RelativeLayout padre;
    private Timer timer;
    private final AtomicBoolean busy = new AtomicBoolean(false);
    public static FilesDialog newInstance(Context context, ArrayList<Archivos> archivos) {
        mContext = context;
        FilesDialog frag = new FilesDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("archivos", archivos);
        frag.setArguments(args);
        return frag;
    }
    private FilesDialog() {
    }

    @SuppressWarnings("AccessStaticViaInstance")
    public void addFilesDialogListener(FilesDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(mContext, R.layout.files_dialog, null);

        padre = view.findViewById(R.id.padre);

        assert getArguments() != null;
        archivosArrayList = getArguments().getParcelableArrayList("archivos");

        RecyclerView listView= view.findViewById(R.id.listArchivos);
        filesAdapter = new FilesAdapter(getActivity(), archivosArrayList);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(filesAdapter);

        Button button = view.findViewById(R.id.button2);
        button.setOnClickListener(view1 -> {
            if(!busy.get()) {
                if (listener != null) {
                    listener.closeDialog();
                }
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(listView);

        alertDialogBuilder.setView(view);

        return alertDialogBuilder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        private final AtomicBoolean borrado = new AtomicBoolean(false);
        Archivos tempAr;
        int pos;
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            pos = viewHolder.getAdapterPosition();
            if(!busy.get()) {
                if (direction == ItemTouchHelper.LEFT) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmar Borrar")
                            .setMessage("¿Desea borrar el archivo?")
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                if (!busy.get()) {
                                    busy.set(true);
                                    borrado.set(false);
                                    tempAr = archivosArrayList.remove(pos);
                                    filesAdapter.notifyItemRemoved(pos);
                                    Snackbar snackbar = Snackbar.make(padre, "Elemento Borrado!", Snackbar.LENGTH_LONG);
                                    snackbar.setDuration(4000);
                                    snackbar.setAction(R.string.deshacer, view -> {
                                        if (!borrado.get()) {
                                            borrado.set(true);
                                            timer.cancel();
                                            timer.purge();
                                            archivosArrayList.add(pos, tempAr);
                                            filesAdapter.notifyItemInserted(pos);
                                            busy.set(false);
                                        }
                                    });
                                    snackbar.show();
                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @SuppressWarnings("ConstantConditions")
                                        @Override
                                        public void run() {
                                            getActivity().runOnUiThread(() -> {
                                                if (!borrado.get()) {
                                                    borrado.set(true);
                                                    if (listener != null) {
                                                        listener.borrarArchivo(tempAr);
                                                    }
                                                }
                                            });
                                        }
                                    }, snackbar.getDuration());
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> filesAdapter.notifyItemChanged(pos))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } else {
                    Archivos a = archivosArrayList.get(pos);
                    if (listener != null && a != null) {
                        listener.processFile(a);
                    }
                }
            } else {
                filesAdapter.notifyItemChanged(pos);
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.sobrante.getCode()))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.bien.getCode()))
                    .addSwipeLeftActionIcon(R.drawable.ic_trash)
                    .addSwipeRightActionIcon(R.drawable.ic_open)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    public void notBusy() {
        busy.set(false);
    }
}
