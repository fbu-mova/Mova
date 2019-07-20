package com.example.mova.components;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Post;

public class PostComponent extends Component<Post> {

    public PostComponent(Post item) {
        super(item);
    }

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
    public void render() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
