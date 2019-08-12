package com.example.mova.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.R;
import com.example.mova.model.Mood;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoodSelectorLayout extends FrameLayout implements AdapterView.OnItemSelectedListener {

    private OnSelectedHandler handler;

    @BindView(R.id.spStatus) protected Spinner spStatus;

    public MoodSelectorLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public MoodSelectorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MoodSelectorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_mood_selector, this);
        ButterKnife.bind(this, this);

        boolean useAlt = false;

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MoodSelectorLayout, 0, 0);
        try {
            useAlt = typedArray.getBoolean(R.styleable.MoodSelectorLayout_useAlt, false);
        } finally {
            typedArray.recycle();
        }

        int bgLayout = (useAlt) ? R.drawable.spinner_alt_bg : R.drawable.spinner_bg;
        int fgLayout = (useAlt) ? R.layout.spinner_alt_item : R.layout.spinner_item;

        ArrayAdapter<Mood.Status> statusAdapter = new ArrayAdapter<>(getContext(), fgLayout, Mood.getStatuses());
        statusAdapter.setDropDownViewResource(fgLayout);
        spStatus.setBackground(getResources().getDrawable(bgLayout));
        spStatus.setAdapter(statusAdapter);
        spStatus.setOnItemSelectedListener(this);

        // Set handler to do nothing temporarily; serves as sentinel to avoid null pointer exceptions
        handler = new OnSelectedHandler() {
            @Override
            public void onSelected(View view, Mood.Status status) {

            }

            @Override
            public void onNothingSelected() {

            }
        };
    }

    public void setHandler(OnSelectedHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Mood.Status status = (Mood.Status) parent.getItemAtPosition(position);
        handler.onSelected(view, status);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        handler.onNothingSelected();
    }

    public Mood.Status getSelectedItem() {
        return (Mood.Status) spStatus.getSelectedItem();
    }

    public void setItem(Mood.Status status) {
        spStatus.setSelection(status.getValue());
    }

    public static abstract class OnSelectedHandler {
        public abstract void onSelected(View view, Mood.Status status);
        public abstract void onNothingSelected();
    }
}
