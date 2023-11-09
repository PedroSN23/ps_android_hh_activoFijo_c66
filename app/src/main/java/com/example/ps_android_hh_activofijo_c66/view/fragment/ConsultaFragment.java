package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.view.adapter.RFIDAdapter;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.TagsTipo;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.view.clases.IconGeneric;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ConsultaFragment extends Fragment {
    private TextView cantidadTotal;
    private static ArrayList<UHFTagsRead> tagsReadArr = null;
    private ArrayList<UHFTagsRead> tagsFiltered;
    private RFIDAdapter mAdapter;
    private LinearLayout padre;
    private PopupMenu popupMenu;
    private final boolean[] selfilt = new boolean[TagsTipo.values().length];
    private boolean repuveFiltered=false;
    public ConsultaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_lectura_ciega, container, false);
        tagsReadArr = new ArrayList<>();
        tagsFiltered = new ArrayList<>();
        padre = v.findViewById(R.id.padre);
        RecyclerView mList = v.findViewById(R.id.menu3List);
        mAdapter = new RFIDAdapter(getContext(), tagsFiltered);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setAdapter(mAdapter);
        cantidadTotal = v.findViewById(R.id.menu3CantLecturasRfid);
        IconGeneric filtro = v.findViewById(R.id.filtro);

        Arrays.fill(selfilt, true);
        selfilt[TagsTipo.repuve.getIndex()]=false; //filtro repuve false

        filtro.setOnClickListener(view -> popupMenu.show());

        popupMenu = new PopupMenu(getContext(), filtro);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            menuItem.setChecked(!menuItem.isChecked());
            boolean repuve=false;
            if(menuItem.getTitle().toString().compareTo("REPUVE")==0) {
                repuve = true;
                repuveFiltered = menuItem.isChecked();
            }

            for(int i=0; i<popupMenu.getMenu().size(); i++) {
                if(popupMenu.getMenu().getItem(i).equals(menuItem)) {
                    selfilt[i]=menuItem.isChecked();
                    if(!repuve) {
                        if (selfilt[i]) {
                            for (UHFTagsRead tr : tagsReadArr) {
                                if (tr.getTipo().getIndex() == i) {
                                    tagsFiltered.add(tr);
                                    mAdapter.notifyItemInserted(tagsFiltered.size() - 1);
                                }
                            }
                        } else {
                            for (int j = tagsFiltered.size() - 1; j >= 0; j--) {
                                if (tagsFiltered.get(j).getTipo().getIndex() == i) {
                                    tagsFiltered.remove(j);
                                    mAdapter.notifyItemRemoved(j);
                                }
                            }
                        }
                    } else {
                        tagsReadArr.clear();
                        int len = tagsFiltered.size();
                        tagsFiltered.clear();
                        mAdapter.notifyItemRangeRemoved(0, len);
                    }
                } else {
                    if(repuve) {
                        if(menuItem.isChecked()) {
                            selfilt[i] = false;
                            popupMenu.getMenu().getItem(i).setChecked(false);
                        } else {
                            selfilt[i]=true;
                            popupMenu.getMenu().getItem(i).setChecked(true);
                        }
                    } else {
                        if(popupMenu.getMenu().getItem(i).getTitle().toString().compareTo("REPUVE")==0) {
                            if(selfilt[i]) {
                                selfilt[i] = false;
                                popupMenu.getMenu().getItem(i).setChecked(false);
                                tagsReadArr.clear();
                                int len = tagsFiltered.size();
                                tagsFiltered.clear();
                                mAdapter.notifyItemRangeRemoved(0, len);
                            }
                        }
                    }
                }
            }
            cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsFiltered.size()));
            return true;
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mList);

        return v;
    }

    public boolean isRepuveFiltered() {
        return repuveFiltered;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearList() {
        tagsReadArr.clear();
        tagsFiltered.clear();
        cantidadTotal.setText(String.format(Locale.getDefault(),"%d", tagsFiltered.size()));
        mAdapter.notifyDataSetChanged();
    }

    public boolean sendTagsAlt(UHFTagsRead uhfTagsRead) {
        boolean temp = processTag(uhfTagsRead);
        cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsFiltered.size()));
        return temp;
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean processTag(UHFTagsRead uhfTagsRead) {
        boolean found = false;
        for(UHFTagsRead tr: tagsReadArr) {
            if(tr.compareTo(uhfTagsRead.getEpc())==0) {
                tr.addCount();
                if(tr.getRssiDouble()<uhfTagsRead.getRssiDouble()) {
                    tr.setRssi(uhfTagsRead.getRssi());
                }
                found = true;
                if(selfilt[tr.getTipo().getIndex()]) {
                    for(int i=0; i<tagsFiltered.size(); i++) {
                        if(tagsFiltered.get(i).equals(tr)) {
                            mAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
                break;
            }
        }
        if(!found) {
            uhfTagsRead.compileTipo();
            tagsReadArr.add(uhfTagsRead);
            if(selfilt[uhfTagsRead.getTipo().getIndex()]) {
                tagsFiltered.add(uhfTagsRead);
                mAdapter.notifyItemInserted(tagsFiltered.size()-1);
            } else {
                found=true;
            }
        }
        return !found;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        int pos;
        UHFTagsRead uhfTagsRead;
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int init = viewHolder.getAbsoluteAdapterPosition();
            int fin = target.getAbsoluteAdapterPosition();

            Collections.swap(tagsFiltered, init, fin);

            mAdapter.notifyItemMoved(init, fin);

            return false;
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            pos = viewHolder.getAdapterPosition();
            if(direction==ItemTouchHelper.LEFT) {
                uhfTagsRead = tagsFiltered.remove(pos);
                mAdapter.notifyItemRemoved(pos);
                cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsFiltered.size()));
                Snackbar snackbar = Snackbar.make(padre, "Tag Borrado!", Snackbar.LENGTH_LONG);
                snackbar.setDuration(4000);
                snackbar.setAction(R.string.deshacer, view -> {
                    tagsFiltered.add(pos, uhfTagsRead);
                    mAdapter.notifyItemInserted(pos);
                    cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsFiltered.size()));
                });
                snackbar.show();
            } else {
                tagsFiltered.get(pos).setCantidad(1);
                mAdapter.notifyItemChanged(pos);
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.sobrante.getCode()))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.faltante.getCode()))
                        .addSwipeLeftActionIcon(R.drawable.ic_trash)
                        .addSwipeRightActionIcon(R.drawable.ic_restart)
                        .create()
                        .decorate();
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public ArrayList<UHFTagsRead> getList() {
        return tagsFiltered;
    }
}
