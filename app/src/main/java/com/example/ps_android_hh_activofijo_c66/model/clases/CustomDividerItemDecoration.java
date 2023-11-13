package com.example.ps_android_hh_activofijo_c66.model.clases;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CustomDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int dividerHeight; // Height of the divider in pixels
    private final Paint dividerPaint;

    public CustomDividerItemDecoration(Context context, int dividerHeight, int dividerColorResId) {
        this.dividerHeight = dividerHeight;
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, dividerColorResId)); // Replace with your desired color resource
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }
}
