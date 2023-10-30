package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.SortHolder;
import com.example.ps_android_hh_activofijo_c66.model.clases.UHFTagsGroup;
import com.example.ps_android_hh_activofijo_c66.view.adapter.ValidacionAdapter;
import com.example.ps_android_hh_activofijo_c66.view.dialog.DetalleValidacionDialog;
import com.example.ps_android_hh_activofijo_c66.view.dialog.DetalleValidacionDialogListener;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.TagsTipo;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ValidacionFragment extends Fragment {
    private TextView cantidadEsperados;
    private TextView cantidadOk;
    private TextView cantidadSobrante;
    private static ArrayList<UHFTagsGroup> tagsGroups = null;
    private ValidacionAdapter mAdapter;
    private LinearLayout padre;
    private ValidacionFragmentListener listener;
    private int posRemoved;
    private UHFTagsGroup tagsGroupRemove;
    private boolean filterTags=false;
    private final ArrayList<SortHolder> sortHolderArrayList = new ArrayList<>();
    private RecyclerView mList;
    public ValidacionFragment() {
    }

    public void addValidacionFragmentListener(ValidacionFragmentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_validacion, container, false);
        cantidadEsperados = v.findViewById(R.id.cantFaltanteval);
        cantidadOk = v.findViewById(R.id.cantLeidosval);
        cantidadSobrante = v.findViewById(R.id.cantSobranteval);

        SortHolder sh = new SortHolder(v.findViewById(R.id.layoutCorrectos),
                v.findViewById(R.id.sortCorrectos),
                v.findViewById(R.id.clickCorrectos), 0);
        sh.addSortHolderListener(id -> {
            for(int i=0; i<sortHolderArrayList.size(); i++) {
                if(i!=id) {
                    sortHolderArrayList.get(i).unsort(getContext());
                }
            }
            tagsGroups.sort((t1, t2) -> t1.compareTo(t2, id));
            mAdapter.notifyItemRangeChanged(0, tagsGroups.size());
            mList.scrollToPosition(0);
        });
        sortHolderArrayList.add(sh);
        sh = new SortHolder(v.findViewById(R.id.layoutFaltantes),
                v.findViewById(R.id.sortFaltantes),
                v.findViewById(R.id.clickFaltantes), 1);
        sh.addSortHolderListener(id -> {
            for(int i=0; i<sortHolderArrayList.size(); i++) {
                if(i!=id) {
                    sortHolderArrayList.get(i).unsort(getContext());
                }
            }
            tagsGroups.sort((t1, t2) -> t1.compareTo(t2, id));
            mAdapter.notifyItemRangeChanged(0, tagsGroups.size());
            mList.scrollToPosition(0);
        });
        sortHolderArrayList.add(sh);
        sh = new SortHolder(v.findViewById(R.id.layoutSobrantes),
                v.findViewById(R.id.sortSobrantes),
                v.findViewById(R.id.clickSobrantes), 2);
        sh.addSortHolderListener(id -> {
            for(int i=0; i<sortHolderArrayList.size(); i++) {
                if(i!=id) {
                    sortHolderArrayList.get(i).unsort(getContext());
                }
            }
            tagsGroups.sort((t1, t2) -> t1.compareTo(t2, id));
            mAdapter.notifyItemRangeChanged(0, tagsGroups.size());
            mList.scrollToPosition(0);
        });
        sortHolderArrayList.add(sh);

        tagsGroups = new ArrayList<>();

        padre = v.findViewById(R.id.padreval);
        mList = v.findViewById(R.id.menu1Listval);
        mAdapter = new ValidacionAdapter(getContext(), tagsGroups);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setAdapter(mAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mList);

        return v;
    }

    public void clearList() {
        borrarLeidosSobrantes();
        obtenerCantidades();
    }

    public void cleanList() {
        borrarSobrantes();
        obtenerCantidades();
    }

    public void setFilterTags(boolean b) {
        filterTags=b;
    }

    private void borrarSobrantes() {
        for(int i=tagsGroups.size()-1; i>=0; i--) {
            if(tagsGroups.get(i).borrarSobrantes()==0) {
                tagsGroups.remove(i);
                mAdapter.notifyItemRemoved(i);
            } else {
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    private void borrarLeidosSobrantes() {
        for(int i=tagsGroups.size()-1; i>=0; i--) {
            if(tagsGroups.get(i).borrarLeidosSobrantes()==0) {
                tagsGroups.remove(i);
                mAdapter.notifyItemRemoved(i);
            } else {
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    public boolean sendTagsAlt(UHFTagsRead uhfTagsRead) {
        boolean temp = processTag(uhfTagsRead);
        if(temp) {
            obtenerCantidades();
        }
        return temp;
    }

    private void obtenerCantidades() {
        cantidadEsperados.setText(String.format(Locale.getDefault(), "%d", getCount(0)));
        cantidadOk.setText(String.format(Locale.getDefault(), "%d", getCount(1)));
        cantidadSobrante.setText(String.format(Locale.getDefault(), "%d", getCount(-1)));
    }

    private int getCount(int tipo) {
        int ret=0;
        for(UHFTagsGroup tg: tagsGroups) {
            ret+=tg.getCount(tipo);
        }
        return ret;
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean processTag(UHFTagsRead uhfTagsRead) {
        boolean beep = false;
        boolean found = false;
        uhfTagsRead.compileTipo();
        int i=0;
        for(UHFTagsGroup tr: tagsGroups) {
            if (uhfTagsRead.getTipo() == TagsTipo.sgtin96) {
                if (tr.getDetail().compareTo(uhfTagsRead.getDetail()) == 0) {
                    found = true;
                    beep = tr.addLectura(uhfTagsRead, filterTags);
                    if(beep) {
                        mAdapter.notifyItemChanged(i);
                    }
                    break;
                }
            } else {
                if (uhfTagsRead.getEpc().compareTo(tr.getEpcAt(0)) == 0) {
                    found = true;
                    if (tr.getInventariadoAt(0) == 0) {
                        tr.setInventariadoAt(0, 1);
                        beep=true;
                        mAdapter.notifyItemChanged(i);
                    }
                    break;
                }
            }
            i++;
        }
        if(!found && !filterTags) {
            uhfTagsRead.setInventariado(-1);
            tagsGroups.add(new UHFTagsGroup(uhfTagsRead));
            mAdapter.notifyItemInserted(tagsGroups.size()-1);
            beep=true;
        }
        return beep;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int init = viewHolder.getAbsoluteAdapterPosition();
            int fin = target.getAbsoluteAdapterPosition();

            Collections.swap(tagsGroups, init, fin);

            mAdapter.notifyItemMoved(init, fin);

            return false;
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            posRemoved = viewHolder.getAdapterPosition();
            if(direction==ItemTouchHelper.LEFT) {
                switch (tagsGroups.get(posRemoved).checkStatus()) {
                    case 0:
                        if(tagsGroups.get(posRemoved).getTagsTipo()==TagsTipo.sgtin96 && tagsGroups.get(posRemoved).getLeidos()>0) {
                            tagsGroups.get(posRemoved).borrarLeidosSobrantes();
                            mAdapter.notifyItemChanged(posRemoved);
                            obtenerCantidades();
                        }
                        break;
                    case -1:
                        if(tagsGroups.get(posRemoved).getTagsTipo()==TagsTipo.sgtin96) {
                            if(tagsGroups.get(posRemoved).getCount(1)>0 || tagsGroups.get(posRemoved).getCount(0)>0) {
                                tagsGroups.get(posRemoved).borrarLeidosSobrantes();
                                mAdapter.notifyItemChanged(posRemoved);
                                obtenerCantidades();
                            } else {
                                tagsGroupRemove = tagsGroups.remove(posRemoved);
                                mAdapter.notifyItemRemoved(posRemoved);
                                obtenerCantidades();
                                showSnackBar();
                            }
                        } else {
                            tagsGroupRemove = tagsGroups.remove(posRemoved);
                            mAdapter.notifyItemRemoved(posRemoved);
                            obtenerCantidades();
                            showSnackBar();
                        }
                        break;
                    case 1:
                        tagsGroups.get(posRemoved).borrarLeidosSobrantes();
                        mAdapter.notifyItemChanged(posRemoved);
                        obtenerCantidades();
                        break;
                }
            } else {
                if(tagsGroups.get(posRemoved).getTagsTipo()==TagsTipo.sgtin96) {
                    mostrarDialogoSgitin(posRemoved);
                } else {
                    buscarTag(tagsGroups.get(posRemoved).getEpcAt(0));
                }
                mAdapter.notifyItemChanged(posRemoved);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int p = viewHolder.getAbsoluteAdapterPosition();
            if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE) {
                if(dX<0.0) {
                    if (p >= 0 && p < tagsGroups.size()) {
                        switch (tagsGroups.get(p).checkStatus()) {
                            case 0:
                                if (tagsGroups.get(p).getTagsTipo() == TagsTipo.sgtin96 && tagsGroups.get(p).getLeidos() > 0) {
                                    drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart, ColorEnum.status_blue.getCode(), R.drawable.ic_search);
                                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                }
                                break;
                            case -1:
                                if (tagsGroups.get(p).getTagsTipo() == TagsTipo.sgtin96) {
                                    if (tagsGroups.get(p).getCount(1) > 0 || tagsGroups.get(p).getCount(0) > 0) {
                                        drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart, ColorEnum.status_blue.getCode(), R.drawable.ic_search);
                                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                    } else {
                                        drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.sobrante.getCode(), R.drawable.ic_trash, ColorEnum.status_blue.getCode(), R.drawable.ic_search);
                                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                    }
                                } else {
                                    drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.sobrante.getCode(), R.drawable.ic_trash, ColorEnum.status_blue.getCode(), R.drawable.ic_search);
                                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                }
                                break;
                            case 1:
                                drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart, ColorEnum.status_blue.getCode(), R.drawable.ic_search);
                                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                break;
                        }
                    }
                } else {
                    if(tagsGroups.get(p).getTagsTipo()==TagsTipo.sgtin96) {
                        drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart, ColorEnum.status_purple.getCode(), R.drawable.baseline_list_alt_24);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    } else {
                        drawSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, ColorEnum.faltante.getCode(), R.drawable.ic_restart, ColorEnum.status_blue.getCode(), R.drawable.ic_search);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                }
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @SuppressWarnings("ConstantConditions")
        private void drawSwipeDecorator(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive, int colorL, int drawableL, int colorR, int drawableR) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), colorL))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), colorR))
                    .addSwipeLeftActionIcon(drawableL)
                    .addSwipeRightActionIcon(drawableR)
                    .create()
                    .decorate();
        }
    };

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(padre, "Tag Borrado!", Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);
        snackbar.setAction(R.string.deshacer, view -> {
            tagsGroups.add(posRemoved, tagsGroupRemove);
            mAdapter.notifyItemInserted(posRemoved);
            obtenerCantidades();
        });
        snackbar.show();
    }

    @SuppressWarnings("ConstantConditions")
    private void mostrarDialogoSgitin(int pos) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DetalleValidacionDialog validacionDialog = DetalleValidacionDialog.newInstance(getContext(),
                tagsGroups.get(pos).getList(), pos);
        validacionDialog.setCancelable(false);
        validacionDialog.addDetalleValidacionDialogListener(new DetalleValidacionDialogListener() {
            @Override
            public void closeDialog(int pos) {
                validacionDialog.dismiss();
                mAdapter.notifyItemChanged(pos);
                obtenerCantidades();
            }

            @Override
            public void buscarTag(String epc, int pos) {
                validacionDialog.dismiss();
                mAdapter.notifyItemChanged(pos);
                obtenerCantidades();
                if(listener!=null) {
                    listener.buscarTag(epc);
                }
            }

            @Override
            public void itemRemoved(int pos) {
                validacionDialog.dismiss();
                posRemoved = pos;
                tagsGroupRemove = tagsGroups.remove(pos);
                mAdapter.notifyItemRemoved(pos);
                obtenerCantidades();
                showSnackBar();
            }
        });
        validacionDialog.show(fm, "dialog");
    }

    private void buscarTag(String epc) {
        if(listener!=null) {
            listener.buscarTag(epc);
        }
    }

    public ArrayList<UHFTagsGroup> getList() {
        return tagsGroups;
    }

    public void detallesDeTabla(ArrayList<UHFTagsGroup> uhfTagsGroups) {
        int range = tagsGroups.size();
        tagsGroups.clear();
        mAdapter.notifyItemRangeRemoved(0, range);
        tagsGroups.addAll(uhfTagsGroups);
        mAdapter.notifyItemRangeInserted(0, tagsGroups.size());
        obtenerCantidades();
    }

    public void clearSort() {
        for(SortHolder sh: sortHolderArrayList) {
            sh.unsort(getContext());
        }
    }
}
