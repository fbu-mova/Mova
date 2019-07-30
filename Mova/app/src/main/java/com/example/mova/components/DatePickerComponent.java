package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePickerComponent extends Component {

    private Date date;
    private OnItemClickListener clickListener;

    private ViewHolder holder;
    private ComponentManager manager;

    public DatePickerComponent(Date date, OnItemClickListener clickListener) {
        this.date = date;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public String getName() {
        return "DatePicker_" + date.getTime();
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

        SimpleDateFormat monthFmt = new SimpleDateFormat("MMM", Locale.US);
        SimpleDateFormat dayFmt = new SimpleDateFormat("d", Locale.US);

        String month = monthFmt.format(date);
        String day = dayFmt.format(date);

        this.holder.tvMonth.setText(month);
        this.holder.tvDay.setText(day);
        this.holder.llDateButton.setOnClickListener((clickedView) -> clickListener.call(clickedView, date));
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvMonth)      protected TextView tvMonth;
        @BindView(R.id.tvDay)        protected TextView tvDay;
        @BindView(R.id.llDateButton) protected LinearLayout llDateButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.item_date_picker, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }

    public static interface OnItemClickListener {
        void call(View v, Date date);
    }
}
