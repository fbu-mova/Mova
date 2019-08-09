package com.example.mova;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.utils.AsyncUtils;

import butterknife.BindView;

public class PersonalToggle extends FrameLayout {

    @BindView(R.id.flPersonal) protected FrameLayout flPersonal;
    @BindView(R.id.tvPersonal) protected TextView tvPersonal;
    @BindView(R.id.ivGradPP)   protected ImageView ivGradPP;
    @BindView(R.id.ivGradPS)   protected ImageView ivGradPS;

    @BindView(R.id.flSocial)   protected FrameLayout flSocial;
    @BindView(R.id.tvSocial)   protected TextView tvSocial;
    @BindView(R.id.ivGradSS)   protected ImageView ivGradSS;
    @BindView(R.id.ivGradSP)   protected ImageView ivGradSP;

    private static int DURATION = 300;
    private static int SELECTED_WEIGHT = 2;
    private static int UNSELECTED_WEIGHT = 1;

    private boolean isPersonal = true;
    private AsyncUtils.ItemCallback<Boolean> onToggle = (toPersonal) -> {};

    public PersonalToggle(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public PersonalToggle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PersonalToggle(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_personal_toggle, this);

        ivGradPP.setAlpha(1f);
        ivGradPS.setAlpha(0f);
        ivGradSP.setAlpha(1f);
        ivGradSS.setAlpha(0f);

        configureClicks();
        setState(true);
    }

    public void setOnToggle(AsyncUtils.ItemCallback<Boolean> onToggle) {
        this.onToggle = onToggle;
    }

    private void configureClicks() {
        flPersonal.setOnClickListener((v) -> setState(true));
        flSocial.setOnClickListener((v) -> setState(false));
    }

    private void setState(boolean toPersonal) {
        if (toPersonal != isPersonal) {
            isPersonal = toPersonal;
            onToggle.call(toPersonal);

            TransitionDrawable personalTransition = (TransitionDrawable) flPersonal.getBackground();
            TransitionDrawable socialTransition = (TransitionDrawable) flSocial.getBackground();

            if (toPersonal) {
                weightAnimate(flPersonal, SELECTED_WEIGHT);
                weightAnimate(flSocial, UNSELECTED_WEIGHT);

                alphaAnimate(ivGradPP, 1f);
                alphaAnimate(ivGradPS, 0f);
                alphaAnimate(ivGradSP, 1f);
                alphaAnimate(ivGradSS, 0f);

                personalTransition.reverseTransition(DURATION);
                socialTransition.reverseTransition(DURATION);
            } else {
                weightAnimate(flPersonal, UNSELECTED_WEIGHT);
                weightAnimate(flSocial, SELECTED_WEIGHT);

                alphaAnimate(ivGradPP, 0f);
                alphaAnimate(ivGradPS, 1f);
                alphaAnimate(ivGradSP, 0f);
                alphaAnimate(ivGradSS, 1f);

                personalTransition.startTransition(DURATION);
                socialTransition.startTransition(DURATION);
            }
        }
    }

    private void alphaAnimate(View view, float alpha) {
        view.animate()
            .alpha(alpha)
            .setDuration(DURATION);
    }

    private void weightAnimate(View view, int weight) {
        ViewWeightAnimationWrapper animationWrapper = new ViewWeightAnimationWrapper(view);
        ObjectAnimator anim = ObjectAnimator.ofFloat(animationWrapper,
                "weight",
                animationWrapper.getWeight(),
                weight);
        anim.setDuration(DURATION);
        anim.start();
    }

    private class ViewWeightAnimationWrapper {
        private View view;

        public ViewWeightAnimationWrapper(View view) {
            if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                this.view = view;
            } else {
                throw new IllegalArgumentException("The view should have LinearLayout as parent");
            }
        }

        public void setWeight(float weight) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.weight = weight;
            view.getParent().requestLayout();
        }

        public float getWeight() {
            return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
        }
    }
}
