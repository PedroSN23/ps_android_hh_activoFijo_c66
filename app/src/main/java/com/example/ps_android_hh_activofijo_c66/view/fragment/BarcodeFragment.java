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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.BarcodeData;
import com.example.ps_android_hh_activofijo_c66.view.adapter.BarcodeAdapter;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class BarcodeFragment extends Fragment {
    private TextView cantidadTotal;
    private static ArrayList<BarcodeData> tagsReadArr = null;
    private BarcodeAdapter mAdapter;
    private LinearLayout padre;
    public BarcodeFragment() {
    }
//hola
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_lectura_ciega_bc, container, false);
        tagsReadArr = new ArrayList<>();
        padre = v.findViewById(R.id.padre);
        RecyclerView mList = v.findViewById(R.id.menu3List);
        mAdapter = new BarcodeAdapter(getContext(), tagsReadArr);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setAdapter(mAdapter);
        cantidadTotal = v.findViewById(R.id.menu3CantLecturasRfid);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mList);

        return v;
    }

    public void clearList() {
        int len = tagsReadArr.size();
        tagsReadArr.clear();
        cantidadTotal.setText(String.format(Locale.getDefault(),"%d", tagsReadArr.size()));
        mAdapter.notifyItemRangeRemoved(0, len);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean sendTagsAlt(String b, String t) {
        boolean temp = mAdapter.sentTagsAlt(new BarcodeData(b, t));
        cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsReadArr.size()));
        return temp;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        int pos;
        BarcodeData barcodeData;
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            pos = viewHolder.getAdapterPosition();
            if(direction==ItemTouchHelper.LEFT) {
                barcodeData = tagsReadArr.remove(pos);
                mAdapter.notifyItemRemoved(pos);
                cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsReadArr.size()));
                Snackbar snackbar = Snackbar.make(padre, "Elemento Borrado!", Snackbar.LENGTH_LONG);
                snackbar.setDuration(4000);
                snackbar.setAction(R.string.deshacer, view -> {
                    tagsReadArr.add(pos, barcodeData);
                    mAdapter.notifyItemInserted(pos);
                    cantidadTotal.setText(String.format(Locale.getDefault(), "%d", tagsReadArr.size()));
                });
                snackbar.show();
            } else {
                tagsReadArr.get(pos).setCantidad(1);
                mAdapter.notifyItemChanged(pos);
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.sobrante.getCode()))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.faltante.getCode()))
                    .addSwipeLeftActionIcon(R.drawable.ic_trash)
                    .addSwipeRightActionIcon(R.drawable.ic_restart)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public ArrayList<BarcodeData> getList() {
        return tagsReadArr;
    }
}

