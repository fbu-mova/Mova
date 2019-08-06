package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Recurrence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecurrenceSettingsComponent extends Component {

    private ViewHolder holder;
    private ComponentManager manager;

    private ArrayAdapter<Type> typeAdapter;

    private Spinner spWeek;
    private ArrayAdapter<Day> weekAdapter;

    private AutoCompleteTextView actvMonthDay;
    private String[] monthDays = new String[31];
    private ArrayAdapter<String> monthDayAdapter;

    private DatePicker dpYearDate;

    private static final int LAYOUT_RES = R.layout.component_recurrence_item;

    @Override
    public ViewHolder getViewHolder() {
        return null;
    }

    @Override
    public String getName() {
        return "RecurrenceSettings";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        this.holder.ivClose.setOnClickListener((v) -> onClose(this));

        createWhenViews();
        configureSpinner();
        hideWhenOptions();
    }

    @Override
    protected void onDestroy() {

    }

    protected abstract void onClose(RecurrenceSettingsComponent component);

    public Recurrence makeRecurrence() {
        Object selectedItem = holder.spType.getSelectedItem();
        if (selectedItem == null || selectedItem.getClass() != Type.class) return null;
        Type type = (Type) selectedItem;

        switch (type) {
            case Week:
                Object objDay = spWeek.getSelectedItem();
                if (objDay == null || objDay.getClass() != Day.class) return null;
                Day day = (Day) objDay;
                Recurrence.Key key = Recurrence.Key.valueOf(day.toString());
                return Recurrence.makeWeekly(key);
            case Month:
                String monthDay = actvMonthDay.getText().toString();
                Pattern re = Pattern.compile("\\d+");
                Matcher matcher = re.matcher(monthDay);
                if (!matcher.find()) return null;
                int moDay = Integer.parseInt(matcher.group(0));
                return Recurrence.makeMonthly(moDay);
            case Year:
                int yMonth = dpYearDate.getMonth();
                int yDay = dpYearDate.getDayOfMonth();
                return Recurrence.makeYearly(yMonth, yDay);
            default:
                return null;
        }
    }

    private void configureSpinner() {
        typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Type.values());
        holder.spType.setAdapter(typeAdapter);

        holder.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type type = Type.values()[position]; // FIXME: Will these ids translate correctly? Are they parallel arrays?
                updateWhenOptions(type);
                showWhenOptions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hideWhenOptions();
            }
        });
    }

    private void updateWhenOptions(Type type) {
        holder.flWhen.removeAllViews();
        switch (type) {
            case Week:
                holder.flWhen.addView(spWeek);
                holder.tvWhen.setText("on");
                break;
            case Month:
                holder.flWhen.addView(actvMonthDay);
                holder.tvWhen.setText("on the");
                break;
            case Year:
            default:
                holder.flWhen.addView(dpYearDate);
                holder.tvWhen.setText("on");
                break;
        }
    }

    private void createWhenViews() {
        spWeek = new Spinner(getActivity());
        weekAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Day.values());
        spWeek.setAdapter(weekAdapter);

        actvMonthDay = new AutoCompleteTextView(getActivity());
        for (int i = 1; i <= monthDays.length; i++) {
            String numStr = Integer.toString(i);
            switch (i % 10) {
                case 1:
                    numStr += "st";
                    break;
                case 2:
                    numStr += "nd";
                    break;
                case 3:
                    numStr += "rd";
                    break;
                default:
                    numStr += "th";
            }
            monthDays[i - 1] = numStr;
        }
        monthDayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthDays);
        actvMonthDay.setAdapter(monthDayAdapter);

        dpYearDate = new DatePicker(getActivity());
//        int year = getActivity().getResources().getIdentifier("android:id/year", null, null);
//        dpYearDate.findViewById(year).setVisibility(View.GONE);
    }

    private void showWhenOptions() {
        holder.tvWhen.setVisibility(View.VISIBLE);
        holder.flWhen.setVisibility(View.VISIBLE);
    }

    private void hideWhenOptions() {
        holder.tvWhen.setVisibility(View.GONE);
        holder.flWhen.setVisibility(View.GONE);
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.ivClose)  public ImageView ivClose;
        @BindView(R.id.spType)   public Spinner spType;
        @BindView(R.id.flWhen)   public FrameLayout flWhen;
        @BindView(R.id.tvRepeat) public TextView tvRepeat;
        @BindView(R.id.tvWhen)   public TextView tvWhen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(LAYOUT_RES, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }

    protected enum Type {
        Week,
        Month,
        Year;
    }

    protected enum Day {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }
}
