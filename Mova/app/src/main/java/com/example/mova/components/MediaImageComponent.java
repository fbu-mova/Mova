package com.example.mova.components;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.utils.ImageUtils;
import com.parse.ParseFile;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaImageComponent extends Component {

    private Bitmap bmp;
    private ParseFile parseFile;

    private ViewHolder holder;
    private ComponentManager manager;

    public MediaImageComponent(Bitmap bmp) {
        this.bmp = bmp;
    }

    public MediaImageComponent(File imageFile) {
        this.bmp = ImageUtils.fileToBitmap(imageFile);
    }

    public MediaImageComponent(ParseFile parseFile) {
        this.parseFile = parseFile;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
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

        if (parseFile == null) {
            Glide.with(getActivity()).load(bmp).into(this.holder.iv);
        } else {
            Glide.with(getActivity()).load(parseFile.getUrl()).into(this.holder.iv);
        }
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.iv) public ImageView iv;

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
