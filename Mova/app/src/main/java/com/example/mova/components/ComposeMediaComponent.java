package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Media;
import com.example.mova.scrolling.EndlessScrollRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ComposeMediaComponent extends Component {

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    public ComposeMediaComponent() {

    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_compose_media, parent, attachToRoot);
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
    public String getName() {
        return "ComposeMedia";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        // TODO
    }

    public abstract void onSelectMedia(Media media);
    public abstract void onBack();
    public abstract void onCancel();

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.llButtons)     public LinearLayout llButtons;
        @BindView(R.id.ivBack)        public ImageView ivBack;
        @BindView(R.id.ivClose)       public ImageView ivClose;

        @BindView(R.id.llPhotos)      public LinearLayout llPhotos;
        @BindView(R.id.cvCamera)      public CardView cvCamera;
        @BindView(R.id.ivCamera)      public ImageView ivCamera;
        @BindView(R.id.cvLibrary)     public CardView cvLibrary;

        @BindView(R.id.esrlScrapbook) public EndlessScrollRefreshLayout<Component.ViewHolder> esrlScrapbook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
