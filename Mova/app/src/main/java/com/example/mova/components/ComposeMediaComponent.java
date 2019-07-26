package com.example.mova.components;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.mova.activities.DelegatedResultActivity;

public class ComposeMediaComponent extends Component {
    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {

    }

    @Override
    public ViewHolder getViewHolder() {
        return null;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setManager(ComponentManager manager) {

    }

    @Override
    public void render() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
