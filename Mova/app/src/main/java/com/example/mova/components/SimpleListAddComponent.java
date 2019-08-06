package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;

import butterknife.ButterKnife;

public class SimpleListAddComponent extends Component {

    private static int LAYOUT_RES; // TODO

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

    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

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
