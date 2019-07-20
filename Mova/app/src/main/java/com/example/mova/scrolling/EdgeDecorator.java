package com.example.mova.scrolling;

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

    private final int topPadding, bottomPadding, leftPadding, rightPadding;

    public EdgeDecorator(int topPadding, int bottomPadding, int leftPadding, int rightPadding) {
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
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

        // first item (bottom and top padding)
        if (itemPosition == 0) {
            outRect.set(view.getPaddingLeft() + leftPadding, view.getPaddingTop() + topPadding, view.getPaddingRight() + rightPadding, view.getPaddingBottom() + bottomPadding);
        }
        // last item
//        else if (itemPosition == itemCount - 1) {
//            outRect.set(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom() + edgePadding);
//        }
        // every other item (only top padding)
        else {
            outRect.set(view.getPaddingLeft() + leftPadding, view.getPaddingTop(), view.getPaddingRight() + rightPadding, view.getPaddingBottom() + bottomPadding);
        }
    }
}
