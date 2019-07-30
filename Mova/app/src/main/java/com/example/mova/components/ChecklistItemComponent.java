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

    protected DelegatedResultActivity activity;
    protected static int viewLayoutRes = R.layout.item_checklist;
    protected ViewHolder holder;
    protected View view;

    protected ComponentManager componentManager;

    public ChecklistItemComponent(T item, int checkedColor, int uncheckedColor, boolean applyColorToggleToText,
                                  AsyncUtils.ItemReturnCallback<T, String> getTitle,
                                  AsyncUtils.ItemReturnCallback<T, Boolean> getDone) {
        this.item = item;
        this.checkedColor = checkedColor;
        this.uncheckedColor = uncheckedColor;
        this.applyColorToggleToText = applyColorToggleToText;
        this.getTitle = getTitle;
        this.getDone = getDone;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
        holder = new ViewHolder(view);
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
    public View getView() {
        return view;
    }

    @Override
    public void render() {
        holder.cbItem.setText(getTitle.call(item));
        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) ->
                onCheckedChanged(buttonView, isChecked));
        holder.cbItem.setTextColor(uncheckedColor);
        holder.cbItem.setChecked(getDone.call(item));
        // TODO: Handle color changes properly
        // TODO: Use custom layout for checkbox
    }

    public abstract void onCheckedChanged(CompoundButton buttonView, boolean isChecked);

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.cbItem) public CheckBox cbItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public String getName() {
        return "ChecklistItemComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }
}
