package com.example.mova.components;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.model.Post;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalPromptComponent extends Component<Post> {
    protected ViewHolder viewHolder;

    public JournalPromptComponent(Post item) {
        super(item);
    }

    @Override
    public void makeViewHolder(Activity activity, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.component_journal_prompt, parent, false);
        viewHolder = new ViewHolder(view);
    }

    @Override
    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    @Override
    public void render() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvGreeting)   public TextView tvGreeting;
        @BindView(R.id.moodSelector) public Mood.SelectorLayout moodSelector;
        @BindView(R.id.bCompose)     public Button bCompose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
