package com.example.mova.components;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.DataEvent;
import com.example.mova.utils.Event;
import com.example.mova.utils.Wrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePickerComponent extends Component {

    private Date date;
    private boolean isSelected;
    private OnItemClickListener clickListener;

    private ViewHolder holder;
    private ComponentManager manager;

    public DatePickerComponent(Date date, DataEvent<Date> event, OnItemClickListener clickListener) {
        this.date = date;
        this.isSelected = false;
        this.clickListener = clickListener;

        // On select date, display this date as selected or not
        event.addListener((selectedDate) -> {
            isSelected = this.date.equals(selectedDate);
            if (holder != null) displaySelected();
        });
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
        this.holder.cvDateButton.setOnClickListener((clickedView) -> clickListener.call(clickedView, date));

        displaySelected();
    }

    @Override
    protected void onDestroy() {

    }

    private void displaySelected() {
        int bgColor, fgColor;
        Resources res = getActivity().getResources();
        if (isSelected) {
            bgColor = res.getColor(R.color.blueDark);
            fgColor = res.getColor(R.color.blueUltraLight);
        } else {
            bgColor = res.getColor(R.color.blueLight);
            fgColor = res.getColor(R.color.blueDark);
        }
        setColors(bgColor, fgColor);
        displayShadow(isSelected);
    }

    private void setColors(int bg, int fg) {
        holder.cvDateButton.setCardBackgroundColor(bg);
        holder.tvMonth.setTextColor(fg);
        holder.tvDay.setTextColor(fg);
    }

    private void displayShadow(boolean shouldDisplay) {
        Resources res = getActivity().getResources();
        int elevation;
        if (shouldDisplay) {
            elevation = (int) res.getDimension(R.dimen.shallowCardElevation);
        } else {
            elevation = 0;
        }
        holder.cvDateButton.setCardElevation(elevation);
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvMonth)      protected TextView tvMonth;
        @BindView(R.id.tvDay)        protected TextView tvDay;
        @BindView(R.id.cvDateButton) protected CardView cvDateButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_date_picker, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }

    public static interface OnItemClickListener {
        void call(View v, Date date);
    }
}
