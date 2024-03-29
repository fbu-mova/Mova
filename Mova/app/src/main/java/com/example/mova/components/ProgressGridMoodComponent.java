package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.model.Mood;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Post;
import com.example.mova.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ProgressGridMoodComponent extends Component {

    private static final String TAG = "ProgressMoodComponent";
    private static final int viewLayoutRes = R.layout.item_grid_mood;

    private ProgressGridViewHolder viewHolder;
    private ComponentManager componentManager;

    private Mood.Status mood;
    private Date date;

    public ProgressGridMoodComponent(Mood.Status mood, Date date) {
        this.mood = mood;
        this.date = date;
    }

    @Override
    public ViewHolder getViewHolder() {
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflated");
        return null;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    public String getName() {
        return "ProgressGridMood_" + mood.toString() + "_" + date;
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    public abstract void onClick();

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        checkViewHolderClass(holder, ProgressGridViewHolder.class);
        viewHolder = (ProgressGridViewHolder) holder;

        viewHolder.cvMood.setCardBackgroundColor(Mood.getColor(mood));
        viewHolder.cvMood.setOnClickListener((v) -> onClick());

        if (date == null) {
            viewHolder.tvDate.setVisibility(View.GONE);
        } else {
            viewHolder.tvDate.setVisibility(View.VISIBLE);
            viewHolder.tvDate.setText(TimeUtils.toShortWeekdayString(date));
        }
    }

    @Override
    protected void onDestroy() {

    }

    public static class ProgressGridViewHolder extends Component.ViewHolder{

        @BindView(R.id.cvMood) public CardView cvMood;
        @BindView(R.id.tvDate) public TextView tvDate;

        public ProgressGridViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ProgressGridViewHolder(view);
        }
    }
}
