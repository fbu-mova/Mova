package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class SimpleListAddComponent extends Component {

    private static int LAYOUT_RES = R.layout.component_simple_list_add;

    private ViewHolder holder;
    private ComponentManager manager;

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public String getName() {
        return "SimpleListAdd";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        this.holder.llRoot.setOnClickListener((v) -> onClick(v));
    }

    @Override
    protected void onDestroy() {

    }

    protected abstract void onClick(View view);

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.llRoot) public LinearLayout llRoot;
        @BindView(R.id.tvAdd)  public TextView tvAdd;
        @BindView(R.id.ivAdd)  public ImageView ivAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(LAYOUT_RES, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
