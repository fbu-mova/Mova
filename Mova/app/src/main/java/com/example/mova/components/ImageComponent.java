package com.example.mova.components;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ImageUtils;
import com.parse.ParseFile;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageComponent extends Component {

    private String url;
    private Bitmap bmp;
    private ParseFile parseFile;
    private float borderRadius;

    private ViewHolder holder;
    private ComponentManager manager;

    private AsyncUtils.EmptyCallback onClick = () -> {};

    public ImageComponent(String url) {
        this(url, 0);
    }

    public ImageComponent(String url, float borderRadius) {
        this.url = url;
        this.borderRadius = borderRadius;
    }

    public ImageComponent(Bitmap bmp) {
        this(bmp, 0);
    }

    public ImageComponent(Bitmap bmp, float borderRadius) {
        this.bmp = bmp;
        this.borderRadius = borderRadius;
    }

    public ImageComponent(File imageFile) {
        this(imageFile, 0);
    }

    public ImageComponent(File imageFile, float borderRadius) {
        this.bmp = ImageUtils.fileToBitmap(imageFile);
        this.borderRadius = borderRadius;
    }

    public ImageComponent(ParseFile parseFile) {
        this(parseFile, 0);
    }

    public ImageComponent(ParseFile parseFile, float borderRadius) {
        this.parseFile = parseFile;
        this.borderRadius = borderRadius;
    }

    public void setOnClick(AsyncUtils.EmptyCallback onClick) {
        this.onClick = onClick;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    public String getName() {
        return "Image";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        this.holder.card.setRadius(borderRadius);
        this.holder.card.setOnClickListener((v) -> onClick.call());
        loadImage();
    }

    protected void loadImage() {
        if (bmp != null) {
            Glide.with(getActivity()).load(bmp).into(this.holder.iv);
        } else if (parseFile != null) {
            Glide.with(getActivity()).load(parseFile.getUrl()).into(this.holder.iv);
        } else {
            Glide.with(getActivity()).load(url).into(this.holder.iv);
        }
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.card) public CardView card;
        @BindView(R.id.iv)   public ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_image, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
