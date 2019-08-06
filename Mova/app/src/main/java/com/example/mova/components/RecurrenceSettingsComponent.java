package com.example.mova.components;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.mova.model.MonthlyRecurrence;
import com.example.mova.model.Recurrence;
import com.example.mova.model.WeeklyRecurrence;
import com.example.mova.model.YearlyRecurrence;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecurrenceSettingsComponent extends Component {

    private ViewHolder holder;
    private ComponentManager manager;

    private ArrayAdapter<Type> typeAdapter;
    private State state;

    private Spinner spWeek;
    private ArrayAdapter<Day> weekAdapter;

    private AutoCompleteTextView actvMonthDay;
    private String[] monthDays = new String[31];
    private ArrayAdapter<String> monthDayAdapter;

    private DatePicker dpYearDate;

    private static final int LAYOUT_RES = R.layout.component_recurrence_item;

    public RecurrenceSettingsComponent() { }

    public RecurrenceSettingsComponent(Recurrence recurrence) {
        if (WeeklyRecurrence.is(recurrence)) {
            WeeklyRecurrence wR = (WeeklyRecurrence) recurrence;
            String dayStr = wR.key.toString();
            state = new WeekState(Day.valueOf(dayStr));
        } else if (MonthlyRecurrence.is(recurrence)) {
            MonthlyRecurrence mR = (MonthlyRecurrence) recurrence;
            state = new MonthState(mR.day);
        } else if (YearlyRecurrence.is(recurrence)) {
            YearlyRecurrence yR = (YearlyRecurrence) recurrence;
            state = new YearState(yR.month, yR.day);
        } else {
            throw new IllegalArgumentException("Recurrence must be time-based; cannot be a shared or empty recurrence. Recurrence key was " + recurrence.key + ".");
        }
    }

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

        this.holder.ivClose.setOnClickListener((v) -> {
            this.holder.flWhen.removeAllViews();
            onClose(this);
        });

        createWhenViews();
        configureSpinner();
        if (state != null) {
            state.loadState();
            updateWhenOptions((Type) this.holder.spType.getSelectedItem());
        }
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
                Integer moDay = getMonthDay();
                if (moDay == null) return null;
                return Recurrence.makeMonthly(moDay);
            case Year:
                int yMonth = dpYearDate.getMonth();
                int yDay = dpYearDate.getDayOfMonth();
                return Recurrence.makeYearly(yMonth, yDay);
            default:
                return null;
        }
    }

    private Integer getMonthDay() {
        String monthDay = actvMonthDay.getText().toString();
        Pattern re = Pattern.compile("\\d+");
        Matcher matcher = re.matcher(monthDay);
        if (!matcher.find()) return null;
        return Integer.parseInt(matcher.group(0));
    }

    private State makeNewState(Type type) {
        switch (type) {
            case Week:
                return new WeekState();
            case Month:
                return new MonthState();
            case Year:
            default:
                return new YearState();
        }
    }

    private void saveState() {
        if (state != null) state.saveState();
    }

    private void configureSpinner() {
        typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Type.values());
        holder.spType.setAdapter(typeAdapter);

        holder.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type type = Type.values()[position]; // FIXME: Will these ids translate correctly? Are they parallel arrays?
                updateWhenOptions(type);
                saveState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        state = makeNewState(type);
        saveState();
    }

    private void createWhenViews() {
        spWeek = new Spinner(getActivity());
        weekAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Day.values());
        spWeek.setAdapter(weekAdapter);
        spWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actvMonthDay = new AutoCompleteTextView(getActivity());
        for (int i = 1; i <= monthDays.length; i++) {
            String numStr = makeMonthDayText(i);
            monthDays[i - 1] = numStr;
        }
        monthDayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthDays);
        actvMonthDay.setAdapter(monthDayAdapter);
        actvMonthDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dpYearDate = (DatePicker) getActivity().getLayoutInflater().inflate(R.layout.layout_spinner_date_picker, null);
        int year = getActivity().getResources().getIdentifier("android:id/year", null, null);
        dpYearDate.findViewById(year).setVisibility(View.GONE);
        initDatePicker();
    }

    private String makeMonthDayText(int day) {
        String numStr = Integer.toString(day);
        switch (day % 10) {
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
        return numStr;
    }

    private void initDatePicker() {
        Calendar cal = Calendar.getInstance();
        initDatePicker(cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private void initDatePicker(int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        dpYearDate.init(
            cal.get(Calendar.YEAR),
            monthOfYear,
            dayOfMonth,
            new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    saveState();
                }
            }
        );
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

    protected abstract class State {
        protected Type type;

        public abstract void saveState();
        public abstract void loadState();

        protected void saveType() {
            if (holder != null) {
                type = (Type) holder.spType.getSelectedItem();
            }
        }

        protected void loadType() {
            if (holder != null && type != null) {
                List<Type> values = Arrays.asList(Type.values());
                int pos = values.indexOf(type);
                if (pos >= 0) holder.spType.setSelection(pos);
            }
        }
    }

    protected class WeekState extends State {

        protected Day day;

        public WeekState() {}

        public WeekState(Day day) {
            this.type = Type.Week;
            this.day = day;
        }

        @Override
        public void saveState() {
            // FIXME: Does this need a check for type == Week?
            saveType();
            if (spWeek != null) {
                day = (Day) spWeek.getSelectedItem();
            }
        }

        @Override
        public void loadState() {
            loadType();
            if (spWeek != null && day != null) {
                List<Day> values = Arrays.asList(Day.values());
                int pos = values.indexOf(day);
                if (pos >= 0) spWeek.setSelection(pos);
            }
        }
    }

    protected class MonthState extends State {

        protected String dayText;

        public MonthState() {}

        public MonthState(int day) {
            this.type = Type.Month;
            dayText = makeMonthDayText(day);
        }

        @Override
        public void saveState() {
            saveType();
            if (actvMonthDay != null) {
                dayText = actvMonthDay.getText().toString();
            }
        }

        @Override
        public void loadState() {
            loadType();
            if (actvMonthDay != null && dayText != null) {
                actvMonthDay.setText(dayText);
            }
        }
    }

    protected class YearState extends State {

        protected int month, day;

        public YearState() {}

        public YearState(int month, int day) {
            this.type = Type.Year;
            this.month = month;
            this.day = day;
        }

        @Override
        public void saveState() {
            saveType();
            if (dpYearDate != null) {
                month = dpYearDate.getMonth();
                day = dpYearDate.getDayOfMonth();
            }
        }

        @Override
        public void loadState() {
            loadType();
            if (dpYearDate != null) {
                initDatePicker(month, day);
            }
        }
    }
}
