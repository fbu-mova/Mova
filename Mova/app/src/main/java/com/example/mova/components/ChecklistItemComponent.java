package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.views.ActionView;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ChecklistItemComponent<T> extends Component {

    protected final String TAG = "checklistItemComp";

    protected T item;
    protected ActionView.ColorConfig config;
    protected AsyncUtils.ItemReturnCallback<T, String> getTitle;
    protected AsyncUtils.ItemReturnCallback<T, Boolean> getDone;

//    protected static int viewLayoutRes = R.layout.item_checklist;
    protected ViewHolder holder;

    protected ComponentManager componentManager;

    protected boolean allowCheckedEvent;

    public ChecklistItemComponent(T item,
                                  AsyncUtils.ItemReturnCallback<T, String> getTitle,
                                  AsyncUtils.ItemReturnCallback<T, Boolean> getDone) {
        this.item = item;
        this.getTitle = getTitle;
        this.getDone = getDone;
        this.allowCheckedEvent = true;
    }

    public void setColors(ActionView.ColorConfig config) {
        this.config = config;
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

        this.holder.avItem.setText(getTitle.call(item));
        this.holder.avItem.setColors(config);
        this.holder.avItem.setOnCheckedChangeListener((isChecked) -> {
            if (allowCheckedEvent) {
                onCheckedChanged(isChecked);
            }
        });
        
        this.allowCheckedEvent = false;
        this.holder.avItem.setComplete(getDone.call(item));
        this.allowCheckedEvent = true;

        // TODO: Handle color changes properly
    }

    @Override
    protected void onDestroy() {

    }

    public abstract void onCheckedChanged(boolean isChecked);

    @Override
    public String getName() {
        return "ChecklistItemComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.avItem)     public ActionView avItem;
        @BindView(R.id.llRoot)     public LinearLayout llRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        protected int layoutId;

        public Inflater() {
            this.layoutId = R.layout.item_checklist;
        }

        public Inflater(int layoutId) {
            this.layoutId = layoutId;
        }

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(layoutId, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
