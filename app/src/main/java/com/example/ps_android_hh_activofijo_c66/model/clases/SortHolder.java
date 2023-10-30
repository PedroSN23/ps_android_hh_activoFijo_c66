package com.example.ps_android_hh_activofijo_c66.model.clases;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.view.clases.IconGeneric;

public class SortHolder {
    private final int id;
    private final RelativeLayout hilightLayout;
    private final IconGeneric sortIcon;
    private SortHolderListener listener;
    private  boolean selected = false;

    public SortHolder(RelativeLayout hilightLayout, IconGeneric sortIcon, LinearLayout clickLayout, int id) {
        this.hilightLayout = hilightLayout;
        this.sortIcon = sortIcon;
        this.id = id;

        clickLayout.setOnClickListener(view -> {
            if(!selected) {
                selected=true;
                sortIcon.setVisibility(View.VISIBLE);
                hilightLayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), ColorEnum.grisAstlix2.getCode()));
                if (listener != null) {
                    listener.sortBy(id);
                }
            }
        });
    }

    public void addSortHolderListener(SortHolderListener listener) {
        this.listener = listener;
    }

    public void unsort(Context context) {
        Log.d("UNSORT", ""+id);
        if(selected) {
            Log.d("UNSORT", "ssss"+id);
            selected=false;
            sortIcon.setVisibility(View.GONE);
            hilightLayout.setBackgroundColor(ContextCompat.getColor(context, ColorEnum.grisAstlix.getCode()));
        }
    }
}
