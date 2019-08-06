package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.utils.AsyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ChecklistItemComponent<T> extends Component {

    protected final String TAG = "checklistItemComp";

    protected T item;
    protected int checkedColor, uncheckedColor;
    protected boolean applyColorToggleToText;
    protected AsyncUtils.ItemReturnCallback<T, String> getTitle;
    protected AsyncUtils.ItemReturnCallback<T, Boolean> getDone;

    protected static int viewLayoutRes = R.layout.item_checklist;
    protected ViewHolder holder;

    protected ComponentManager componentManager;

    protected boolean allowCheckedEvent;

    public ChecklistItemComponent(T item, int checkedColor, int uncheckedColor, boolean applyColorToggleToText,
                                  AsyncUtils.ItemReturnCallback<T, String> getTitle,
                                  AsyncUtils.ItemReturnCallback<T, Boolean> getDone) {
        this.item = item;
        this.checkedColor = checkedColor;
        this.uncheckedColor = uncheckedColor;
        this.applyColorToggleToText = applyColorToggleToText;
        this.getTitle = getTitle;
        this.getDone = getDone;
        this.allowCheckedEvent = true;
    }

    @Override
    public ViewHolder getViewHolder() {
        if (holder != null) {
            return holder;
        }
        Log.e(TAG, "holder null in getViewHolder");
        return null;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        this.holder.cbItem.setText(getTitle.call(item));
        this.holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (allowCheckedEvent) {
                onCheckedChanged(buttonView, isChecked);
            }
        });
        this.holder.cbItem.setTextColor(uncheckedColor);
        setChecked(getDone.call(item), false);
        // TODO: Handle color changes properly
        // TODO: Use custom layout for checkbox
    }

    @Override
    protected void onDestroy() {

    }

    public abstract void onCheckedChanged(CompoundButton buttonView, boolean isChecked);

    public void setChecked(boolean checked, boolean allowCheckedEvent) {
        this.allowCheckedEvent = allowCheckedEvent;
        if (holder != null) {
            holder.cbItem.setChecked(checked);
        }
    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    @Override
    public String getName() {
        return "ChecklistItemComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.cbItem) public CheckBox cbItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
