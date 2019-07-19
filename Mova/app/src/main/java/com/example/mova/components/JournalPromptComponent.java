package com.example.mova.components;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.mova.model.Post;

public class JournalPromptComponent extends Component<Post> {

    public JournalPromptComponent(Post item) {
        super(item);
    }

    @Override
    public void makeViewHolder(Activity activity, ViewGroup parent) {

    }

    @Override
    public ViewHolder getViewHolder() {
        return null;
    }

    @Override
    public void bind() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
