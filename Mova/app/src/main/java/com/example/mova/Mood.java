package com.example.mova;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Mood {
    public static enum Status {
        Happy(0),
        Sad(1),
        Excited(2),
        Angry(3),
        Frustrated(4),
        Disappointed(5),
        Confident(6),
        Determined(7),
        Pensive(8),
        Relaxed(9),
        Fulfilled(10),
        Focused(11),
        Bored(12),
        Annoyed(13),
        Tired(14),
        Concerned(15),
        Inspired(16),
        Empowered(17),
        Supported(18);

        private final int value;
        private Status(int value) {
            this.value = value;
        }

        // Kept package private because the emotion itself, not the index in the list, should be a unique identifier
        int getValue() {
            return value;
        }
    }

    public static class SelectorLayout extends FrameLayout implements AdapterView.OnItemSelectedListener {

        private OnSelectedHandler handler;

        @BindView(R.id.spStatus) protected Spinner spStatus;

        public SelectorLayout(@NonNull Context context) {
            super(context);
            init();
        }

        public SelectorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public SelectorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            inflate(getContext(), R.layout.layout_mood_selector, this);
            ButterKnife.bind(this, this);

            ArrayAdapter<Status> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Arrays.asList(Status.values()));
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStatus.setAdapter(statusAdapter);
            spStatus.setOnItemSelectedListener(this);

            // Set handler to do nothing temporarily; serves as sentinel to avoid null pointer exceptions
            handler = new OnSelectedHandler() {
                @Override
                public void onSelected(View view, Status status) {

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
            Status status = (Status) parent.getItemAtPosition(position);
            handler.onSelected(view, status);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            handler.onNothingSelected();
        }

        public Status getSelectedItem() {
            return (Status) spStatus.getSelectedItem();
        }

        public void setItem(Status status) {
            spStatus.setSelection(status.getValue());
        }

        public static abstract class OnSelectedHandler {
            public abstract void onSelected(View view, Status status);
            public abstract void onNothingSelected();
        }
    }
}
