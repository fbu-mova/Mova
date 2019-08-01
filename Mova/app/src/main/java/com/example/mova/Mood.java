package com.example.mova;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

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
        Supported(18),
        Empty(19);

        private final int value;
        private Status(int value) {
            this.value = value;
        }

        // Kept package private because the emotion itself, not the index in the list, should be a unique identifier
        int getValue() {
            return value;
        }
    }

    public static List<Status> getStatuses() {
        List<Status> statuses = Arrays.asList(Status.values());
        statuses.remove(Status.Empty);
        return statuses;
    }

    public static int getColor(Status status){

        switch (status){
            case Happy:
                return Color.RED;
            case Sad:
                return Color.RED;
            case Excited:
                return Color.RED;
            case Angry:
                return Color.RED;
            case Frustrated:
                return Color.RED;
            case Disappointed:
                return Color.RED;
            case Confident:
                return Color.RED;
            case Determined:
                return Color.RED;
            case Pensive:
                return Color.RED;
            case Relaxed:
                return Color.RED;
            case Fulfilled:
                return Color.RED;
            case Focused:
                return Color.RED;
            case Bored:
                return Color.RED;
            case Annoyed:
                return Color.RED;
            case Tired:
                return Color.RED;
            case Concerned:
                return Color.RED;
            case Inspired:
                return Color.RED;
            case Empowered:
                return Color.RED;
            case Supported:
                return Color.RED;
            case Empty:
                return Color.WHITE;
            default:
                return Color.BLACK;
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

            ArrayAdapter<Status> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Mood.getStatuses());
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
