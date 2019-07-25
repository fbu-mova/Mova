package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePickerComponent extends Component {

    private Date date;
    private OnItemClickListener clickListener;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    public DatePickerComponent(Date date, OnItemClickListener clickListener) {
        this.date = date;
        this.clickListener = clickListener;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.item_date_picker, parent, attachToRoot);
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
        return "DatePicker_" + date.getTime();
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        SimpleDateFormat monthFmt = new SimpleDateFormat("MMM", Locale.US);
        SimpleDateFormat dayFmt = new SimpleDateFormat("d", Locale.US);

        String month = monthFmt.format(date);
        String day = dayFmt.format(date);

        holder.tvMonth.setText(month);
        holder.tvDay.setText(day);
        holder.llDateButton.setOnClickListener((clickedView) -> clickListener.call(clickedView, date));
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

    public static interface OnItemClickListener {
        void call(View v, Date date);
    }
}
