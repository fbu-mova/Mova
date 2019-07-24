package com.example.mova.components;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalMemoryComponent extends Component {

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

        @BindView(R.id.flAccentColor) public FrameLayout flAccentColor;
        @BindView(R.id.tvDate)        public TextView tvDate;
        @BindView(R.id.tvMood)        public TextView tvMood;
        @BindView(R.id.tvExcerpt)     public TextView tvExcerpt;
        @BindView(R.id.tvPrompt)      public TextView tvPrompt;
        @BindView(R.id.bReflect)      public Button bReflect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
