package com.example.mova.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mova.R;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionView extends ConstraintLayout {

//    @BindView(R.id.llRoot)   protected LinearLayout llRoot;
    @BindView(R.id.flToggle) protected FrameLayout flToggle;
    @BindView(R.id.ivToggle) protected ImageView ivToggle;
    @BindView(R.id.tvText)   protected TextView tvText;

    private ColorConfig config;
    private String text;
    private boolean isEnabled;
    private boolean isComplete;

    private Drawable boxCompleted;
    private Drawable boxIncomplete;
    private Drawable boxDisabled;

    public ActionView(Context context) {
        super(context);
        init(context, null);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_action, this);
        ButterKnife.bind(this);

        Resources res = getContext().getResources();
        boxCompleted = res.getDrawable(R.drawable.action_completed);
        boxIncomplete = res.getDrawable(R.drawable.action_incomplete);
        boxDisabled = res.getDrawable(R.drawable.action_disabled);

        // Extract xml attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ActionView, 0, 0);
        config = ColorConfig.defaultFromHue(res, ColorUtils.Hue.Blue);
        try {
            setText(typedArray.getString(R.styleable.ActionView_text));
            setEnabled(typedArray.getBoolean(R.styleable.ActionView_enabled, true));
            setComplete(typedArray.getBoolean(R.styleable.ActionView_completed, false));
            setColors(ColorConfig.fromAttrs(typedArray, config));
        } finally {
            typedArray.recycle();
        }

        int area = res.getDimensionPixelOffset(R.dimen.elementMargin);
        ViewUtils.expandTouchArea(flToggle, new Rect(area, 0, area, 0));

        setOnClickListener((v) -> {});
    }

    public void setColors(ColorConfig config) {
        this.config = config;
        displayColors();
    }

    public ColorConfig getColors() {
        return config;
    }

    public void setText(String text) {
        this.text = text;
        tvText.setText(text);
    }

    public String getText() {
        return text;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
        if (isEnabled) {
            ivToggle.setImageDrawable((complete) ? boxCompleted : boxIncomplete);
            displayColors();
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if (!isEnabled) {
            ivToggle.setImageDrawable(boxDisabled);
            displayColors();
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    private void displayColors() {
        if (!isEnabled) {
            tvText.setTextColor(config.colorTextDisabled);
            ivToggle.setColorFilter(config.colorBoxDisabled);
            return;
        }

        if (isComplete) {
            tvText.setTextColor(config.colorTextComplete);
            ivToggle.setColorFilter(config.colorBoxComplete);
        } else {
            tvText.setTextColor(config.colorTextIncomplete);
            ivToggle.setColorFilter(config.colorBoxIncomplete);
        }
    }

    public void setOnCheckedChangeListener(@Nullable AsyncUtils.ItemCallback<Boolean> listener) {
        // FIXME: May be the wrong type of haptic feedback, and image might not be clickable.
        flToggle.setOnClickListener((v) -> {
            if (isEnabled) {
                setComplete(!isComplete);
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (listener != null) listener.call(isComplete);
            }
        });
    }

    public void setOnTextClickListener(AsyncUtils.EmptyCallback listener) {
        tvText.setOnClickListener((v) -> listener.call());
    }

    public static class ColorConfig {
        public int colorBoxComplete;
        public int colorTextComplete;

        public ColorConfig setCompleteColors(int box, int text) {
            colorBoxComplete = box;
            colorTextComplete = text;
            return this;
        }

        public int colorBoxIncomplete;
        public int colorTextIncomplete;

        public ColorConfig setIncompleteColors(int box, int text) {
            colorBoxIncomplete = box;
            colorTextIncomplete = text;
            return this;
        }

        public int colorBoxDisabled;
        public int colorTextDisabled;

        public ColorConfig setDisabledColors(int box, int text) {
            colorBoxDisabled = box;
            colorTextDisabled = text;
            return this;
        }

        public static ColorConfig defaultFromHue(Resources res, ColorUtils.Hue hue) {
            int color = ColorUtils.getColor(res, hue, ColorUtils.Lightness.Light);
            int textMain = res.getColor(R.color.textMain);
            int textLight = res.getColor(R.color.textLight);

            return new ColorConfig()
                    .setCompleteColors(color, color)
                    .setIncompleteColors(color, textMain)
                    .setDisabledColors(textLight, textLight);
        }

        static ColorConfig fromAttrs(TypedArray typedArray, ColorConfig fallback) {
            ColorConfig config = new ColorConfig();

            config.colorTextComplete = typedArray.getColor(R.styleable.ActionView_colorTextComplete, fallback.colorTextComplete);
            config.colorBoxComplete = typedArray.getColor(R.styleable.ActionView_colorBoxComplete, fallback.colorBoxComplete);

            config.colorTextIncomplete = typedArray.getColor(R.styleable.ActionView_colorTextIncomplete, fallback.colorTextIncomplete);
            config.colorBoxIncomplete = typedArray.getColor(R.styleable.ActionView_colorBoxIncomplete, fallback.colorBoxIncomplete);

            config.colorTextDisabled = typedArray.getColor(R.styleable.ActionView_colorTextDisabled, fallback.colorTextDisabled);
            config.colorBoxDisabled = typedArray.getColor(R.styleable.ActionView_colorBoxDisabled, fallback.colorBoxDisabled);

            return config;
        }
    }
}
