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

public class ImageComponent extends Component {

    private Bitmap bmp;
    private ParseFile parseFile;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    public ImageComponent(Bitmap bmp) {
        this.bmp = bmp;
    }

    public ImageComponent(File imageFile) {
        this.bmp = ImageUtils.fileToBitmap(imageFile);
    }

    public ImageComponent(ParseFile parseFile) {
        this.parseFile = parseFile;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_image, parent, attachToRoot);
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
        return "Image";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        if (parseFile == null) {
            Glide.with(activity).load(bmp).into(holder.iv);
        } else {
            Glide.with(activity).load(parseFile.getUrl()).into(holder.iv);
        }
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.iv) public ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
