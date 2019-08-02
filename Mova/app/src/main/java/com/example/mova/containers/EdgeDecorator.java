package com.example.mova.containers;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by anthonykiniyalocts on 12/8/16.
 * Credit: https://gist.github.com/AKiniyalocts/5a00d66f03f1c3393c1302bea73749b2
 *
 * Quick way to add padding to first and last item in recyclerview via decorators
 */

public class EdgeDecorator extends RecyclerView.ItemDecoration {

    private final int topMargin, bottomMargin, leftMargin, rightMargin;
    private final Orientation orientation;
    private final Start start;

    public EdgeDecorator(int margin) {
        this(margin, Orientation.Vertical);
    }

    public EdgeDecorator(int margin, Orientation orientation) {
        this(margin, orientation, Start.Natural);
    }

    public EdgeDecorator(int margin, Orientation orientation, Start start) {
        this(margin, margin, margin, margin, orientation, start);
    }

    public EdgeDecorator(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        this(leftMargin, topMargin, rightMargin, bottomMargin, Orientation.Vertical);
    }

    public EdgeDecorator(int leftMargin, int topMargin, int rightMargin, int bottomMargin, Orientation orientation) {
        this(leftMargin, topMargin, rightMargin, bottomMargin, orientation, Start.Natural);
    }

    public EdgeDecorator(int leftMargin, int topMargin, int rightMargin, int bottomMargin, Orientation orientation, Start start) {
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.orientation = orientation;
        this.start = start;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = state.getItemCount();

        final int itemPosition = parent.getChildAdapterPosition(view);

        // no position, leave it alone
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // first item (start and end padding)
        if (itemPosition == 0) {
            outRect.set(view.getPaddingLeft() + leftMargin, view.getPaddingTop() + topMargin, view.getPaddingRight() + rightMargin, view.getPaddingBottom() + bottomMargin);
        }
        // every other item (only end padding)
        else {
            // Remove the correct start padding based on start and orientation
            int left = view.getPaddingLeft() + leftMargin;
            int top = view.getPaddingTop() + topMargin;
            int right = view.getPaddingRight() + rightMargin;
            int bottom = view.getPaddingBottom() + bottomMargin;

            if (orientation == Orientation.Vertical) {
                if (start == Start.Natural) top -= topMargin;
                else                        bottom -= bottomMargin;
            } else {
                if (start == Start.Natural) left -= leftMargin;
                else                        right -= rightMargin;
            }

            outRect.set(left, top, right, bottom);
        }
    }

    public enum Orientation {
        Vertical,
        Horizontal
    }

    public enum Start {
        Natural,
        Reverse
    }
}
