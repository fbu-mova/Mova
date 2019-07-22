package com.example.mova.components;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.utils.AsyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ChecklistItemComponent<T> extends Component<T> {

    private T item;
    private int checkedColor, uncheckedColor;
    private AsyncUtils.ItemReturnCallback<T, String> getTitle;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;

    public ChecklistItemComponent(T item, int checkedColor, int uncheckedColor, AsyncUtils.ItemReturnCallback<T, String> getTitle) {
        this.item = item;
        this.checkedColor = checkedColor;
        this.uncheckedColor = uncheckedColor;
        this.getTitle = getTitle;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.item_checklist, parent, attachToRoot);
        holder = new ViewHolder(view);
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void render() {
        holder.cbItem.setText(getTitle.call(item));
        holder.cbItem.setOnClickListener((view) -> onClick(view));
        holder.cbItem.setTextColor(uncheckedColor);
        holder.cbItem.setBackgroundColor(uncheckedColor);
        // TODO: Handle color changes properly
        // TODO: Use custom layout for checkbox
    }

    public abstract void onClick(View view);

    public class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.cbItem) public CheckBox cbItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
