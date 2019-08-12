package com.example.mova.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.R;
import com.example.mova.utils.ColorUtils;

import java.util.ArrayList;
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

        public int getValue() {
            return value;
        }
    }

    public static List<Status> getStatuses() {
        ArrayList<Status> statuses = new ArrayList<>(Arrays.asList(Status.values()));
        statuses.remove(Status.Empty);
        return statuses;
    }

    public static int getColor(Status status){

        switch (status){
            case Happy:
                return Color.parseColor("#FFC85F");
            case Sad:
                return Color.parseColor("#75A1E3");
            case Excited:
                return Color.parseColor("#F58749");
            case Angry:
                return Color.parseColor("#EB6161");
            case Frustrated:
                return Color.parseColor("#FE9191");
            case Disappointed:
                return Color.parseColor("#5D62D8");
            case Confident:
                return Color.parseColor("#F086EC");
            case Determined:
                return Color.parseColor("#F14BCD");
            case Pensive:
                return Color.parseColor("#A6AAFE");
            case Relaxed:
                return Color.parseColor("#93D1FF");
            case Fulfilled:
                return Color.parseColor("#C256DD");
            case Focused:
                return Color.parseColor("#EEF974");
            case Bored:
                return Color.parseColor("#B0C080");
            case Annoyed:
                return Color.parseColor("#83B93F");
            case Tired:
                return Color.parseColor("#3836B0");
            case Concerned:
                return Color.parseColor("#D27272");
            case Inspired:
                return Color.parseColor("#72F0D2");
            case Empowered:
                return Color.parseColor("#AF7CF1");
            case Supported:
                return Color.parseColor("#8934CB");
            case Empty:
            default:
                return Color.parseColor("#D6D6D6");
        }
    }

}
