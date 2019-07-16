package com.example.mova.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePickerAdapter extends RecyclerView.Adapter<DatePickerAdapter.ViewHolder> {

    private Activity activity;
    private List<Date> dates;

    public DatePickerAdapter(Activity activity, List<Date> dates) {
        this.activity = activity;
        this.dates = dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_date_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = dates.get(position);
        // TODO: Extract these formats into something more reusable
        SimpleDateFormat monthFmt = new SimpleDateFormat("MMM", Locale.US);
        SimpleDateFormat dayFmt = new SimpleDateFormat("d", Locale.US);

        String month = monthFmt.format(date);
        String day = dayFmt.format(date);

        holder.tvMonth.setText(month);
        holder.tvDay.setText(day);

        // TODO: Add onClick or onScroll functionality.
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMonth) protected TextView tvMonth;
        @BindView(R.id.tvDay)   protected TextView tvDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
