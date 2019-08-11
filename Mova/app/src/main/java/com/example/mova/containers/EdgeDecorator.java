package com.example.mova.containers;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.utils.AsyncUtils;

/**
 * Created in part by anthonykiniyalocts on 12/8/16.
 * Credit: https://gist.github.com/AKiniyalocts/5a00d66f03f1c3393c1302bea73749b2
 *
 * Quick way to add padding to first and last item in recyclerview via decorators
 */

public class EdgeDecorator extends RecyclerView.ItemDecoration {

    private final Config config;

    public EdgeDecorator() {
        this(new Config());
    }

    public EdgeDecorator(int margin) {
        this(new Config(margin));
    }

    public EdgeDecorator(int left, int top, int right, int bottom) {
        this(new Config(left, top, right, bottom));
    }

    public EdgeDecorator(Config config) {
        this.config = config;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        View useView = config.getViewToDecorate.call(view);

        final int itemPosition = parent.getChildAdapterPosition(view);

        // No position, leave it alone
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        int left = useView.getPaddingLeft() + config.leftMargin;
        int top = useView.getPaddingTop() + config.topMargin;
        int right = useView.getPaddingRight() + config.rightMargin;
        int bottom = useView.getPaddingBottom() + config.bottomMargin;
        outRect.set(left, top, right, bottom);

        // First item (keep start and end padding)
        if (itemPosition == 0) {
            if (config.useFirstMargin) {
                addTo(useView, outRect, getStart(), -1 * getMargin(getStart()));
                addTo(useView, outRect, getStart(), config.firstMargin);
            }
            return;
        }

        // Last item
        if (itemPosition == state.getItemCount() - 1) {
            if (config.useLastMargin) {
                addTo(useView, outRect, getEnd(), -1 * getMargin(getEnd()));
                addTo(useView, outRect, getEnd(), config.lastMargin);
            }
            return;
        }

        // For all other items, remove the correct start padding (only keep end padding)
        addTo(useView, outRect, getStart(), -1 * getMargin(getStart()));
    }

    private Side getStart() {
        if (config.orientation == Orientation.Vertical) {
            if (config.start == Start.Natural) return Side.Top;
            return Side.Bottom;
        } else {
            if (config.start == Start.Natural) return Side.Left;
            return Side.Right;
        }
    }

    private Side getEnd() {
        return getStart().opposite();
    }

    private int getMargin(Side side) {
        switch (side) {
            case Left: return config.leftMargin;
            case Top: return config.topMargin;
            case Right: return config.rightMargin;
            case Bottom:
            default:
                return config.bottomMargin;
        }
    }

    private void addTo(View view, Rect rect, Side side, int value) {
        switch (side) {
            case Left:
                rect.left += view.getPaddingLeft() + value;
                break;
            case Top:
                rect.top += view.getPaddingTop() + value;
                break;
            case Right:
                rect.right += view.getPaddingRight() + value;
                break;
            case Bottom:
            default:
                rect.bottom += view.getPaddingBottom() + value;
                break;
        }
    }

    private enum Side {
        Left, Top, Right, Bottom;

        public Side opposite() {
            switch (this) {
                case Left: return Right;
                case Top: return Bottom;
                case Right: return Left;
                case Bottom:
                default:
                    return Top;
            }
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

    public static class Config {
        private int topMargin = 0, bottomMargin = 0, leftMargin = 0, rightMargin = 0;

        private int firstMargin = 0, lastMargin = 0;
        private boolean useFirstMargin = false, useLastMargin = false;

        private Orientation orientation = Orientation.Vertical;
        private Start start = Start.Natural;

        private AsyncUtils.ItemReturnCallback<View, View> getViewToDecorate = view -> view;

        public Config() {}

        public Config(int margin) {
            this.topMargin = margin;
            this.bottomMargin = margin;
            this.leftMargin = margin;
            this.rightMargin = margin;
        }

        public Config(int horizontal, int vertical) {
            this.topMargin = vertical;
            this.bottomMargin = vertical;
            this.leftMargin = horizontal;
            this.rightMargin = horizontal;
        }

        public Config(int left, int top, int right, int bottom) {
            this.topMargin = top;
            this.bottomMargin = bottom;
            this.leftMargin = left;
            this.rightMargin = right;
        }

        public Config setFirstMargin(int margin) {
            this.useFirstMargin = true;
            this.firstMargin = margin;
            return this;
        }

        public Config setLastMargin(int margin) {
            this.useLastMargin = true;
            this.lastMargin = margin;
            return this;
        }

        public Config setOrientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Config setStart(Start start) {
            this.start = start;
            return this;
        }

        public Config setGetViewToDecorate(AsyncUtils.ItemReturnCallback<View, View> getViewToDecorate) {
            this.getViewToDecorate = getViewToDecorate;
            return this;
        }
    }
}
