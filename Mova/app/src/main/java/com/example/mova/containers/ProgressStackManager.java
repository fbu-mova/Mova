package com.example.mova.containers;

import android.app.Activity;
import android.widget.FrameLayout;

import com.example.mova.views.ProgressStack;

import java.util.List;

public class ProgressStackManager {
    private List<ProgressStack> stacks;
    private OnClickListener clickListener;

    private int maxValue;

    public ProgressStackManager(Activity activity, List<ProgressStack> stacks) {
        if (stacks.size() <= 0) throw new IllegalArgumentException("Must have at least one stack.");
        maxValue = -1;
        this.stacks = stacks;
    }

    public List<ProgressStack> getStacks() {
        return stacks;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        for (ProgressStack stack : stacks) {
            stack.setMaxValue(maxValue);
        }
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void add(int color) {
        for (int i = 0; i < stacks.size(); i++) {
            final int index = i;
            stacks.get(i).add(color);
            stacks.get(i).setOnClick(color, (c, view) -> clickListener.onClick(index, c, view));
        }
    }

    public void remove(int color) {
        for (ProgressStack stack : stacks) {
            stack.remove(color);
        }
        updateDisplayMax();
    }

    public boolean contains(int color) {
        return stacks.get(0).contains(color);
    }

    public int valueOf(int index, int color) {
        return stacks.get(index).valueOf(color);
    }

    public void setValue(int index, int color, int value) {
        if (!contains(color)) add(color);
        stacks.get(index).setValue(color, value);
        updateDisplayMax();
    }

    public void show(int color) {
        for (ProgressStack stack : stacks) {
            stack.show(color);
        }
    }

    public void showOnly(int color) {
        for (ProgressStack stack : stacks) {
            stack.showOnly(color);
        }
    }

    public void hide(int color) {
        for (ProgressStack stack : stacks) {
            stack.hide(color);
        }
    }

    public void hideAll() {
        for (ProgressStack stack : stacks) {
            stack.hideAll();
        }
    }

    public boolean isShown(int color) {
        return stacks.get(0).isShown(color);
    }

    public void select(int index, int color) {
        stacks.get(index).select(color);
        for (int i = 0; i < stacks.size(); i++) {
            if (i == index) continue;
            stacks.get(i).selectNone();
        }
    }

    public void deselect() {
        for (ProgressStack stack : stacks) {
            stack.deselect();
        }
    }

    public boolean isSelected(int index, int color) {
        return stacks.get(index).isSelected(color);
    }

    public int totalValue(int index) {
        return stacks.get(index).totalValue();
    }

    public int maxTotalValue() {
        int max = 0;
        for (ProgressStack stack : stacks) {
            max = Math.max(max, stack.totalValue());
        }
        return max;
    }

    public int totalValueShown(int index) {
        return stacks.get(index).totalValueShown();
    }

    public int maxTotalValueShown() {
        int max = 0;
        for (ProgressStack stack : stacks) {
            max = Math.max(max, stack.totalValueShown());
        }
        return max;
    }

    public int tallestY() {
        return (maxValue == -1) ? maxTotalValue() : getMaxValue();
    }

    private void updateDisplayMax() {
        int max = (maxValue == -1) ? maxTotalValue() : getMaxValue();
        for (ProgressStack stack : stacks) {
            stack.setMaxValue(max);
        }
    }

    public void setOnClick(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onClick(int index, int color, FrameLayout view);
    }
}
