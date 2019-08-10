package com.example.mova.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.mova.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EdgeFloatingActionButton extends FrameLayout {

    @BindView(R.id.flRoot)   protected FrameLayout flRoot;
    @BindView(R.id.cvMask)   protected CardView cvMask;
    @BindView(R.id.flImage)  protected FrameLayout flImage;
    @BindView(R.id.iv)       protected ImageView iv;

    @BindView(R.id.clRoot)   protected ConstraintLayout clRoot;
    @BindView(R.id.flPadded) protected FrameLayout flPadded;
    @BindView(R.id.space)    protected Space space;

    private int cornerRadius;
    private Drawable image;
    private int imageTint, backgroundTint;
    private int edge;
    private int padding;

    private enum Edge {
        Left(1),
        Top(2),
        Right(4),
        Bottom(8);

        private final int value;
        Edge(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public boolean and(int edge) {
            return (value & edge) == value;
        }

        public static Edge valueOf(int value) {
            if (value == Left.value)   return Left;
            if (value == Top.value)    return Top;
            if (value == Right.value)  return Right;
            if (value == Bottom.value) return Bottom;
            return null;
        }

        public int constraintSide() {
            switch (this) {
                case Left:
                    return ConstraintSet.LEFT;
                case Top:
                    return ConstraintSet.TOP;
                case Right:
                    return ConstraintSet.RIGHT;
                case Bottom:
                default:
                    return ConstraintSet.BOTTOM;
            }
        }

        public Edge opposite() {
            switch (this) {
                case Left:
                    return Right;
                case Top:
                    return Bottom;
                case Right:
                    return Left;
                case Bottom:
                default:
                    return Top;
            }
        }
    }

    public EdgeFloatingActionButton(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public EdgeFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EdgeFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_edge_floating_action_button, this);
        ButterKnife.bind(this);

        // Extract xml attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.EdgeFloatingActionButton, 0, 0);
        Resources res = getContext().getResources();
        try {
            cornerRadius = typedArray.getDimensionPixelOffset(
                    R.styleable.EdgeFloatingActionButton_cornerRadius,
                    res.getDimensionPixelOffset(R.dimen.borderRadius));

            image = typedArray.getDrawable(R.styleable.EdgeFloatingActionButton_image);
            imageTint = typedArray.getColor(R.styleable.EdgeFloatingActionButton_imageTint, res.getColor(R.color.blueUltraLight));
            backgroundTint = typedArray.getColor(R.styleable.EdgeFloatingActionButton_backgroundTint, res.getColor(R.color.blueMid));

            edge = typedArray.getInt(R.styleable.EdgeFloatingActionButton_edge, Edge.Right.getValue());
        } finally {
            typedArray.recycle();
        }

        padding = res.getDimensionPixelOffset(R.dimen.elementMargin); // FIXME: Likely temp until fetched live

        offsetEdge();
        constrainOffsetEdge();
        cvMask.setRadius(cornerRadius);

        iv.setImageDrawable(image);
        iv.setColorFilter(imageTint);
        cvMask.setCardBackgroundColor(backgroundTint);
    }

    private void offsetEdge() {
        // Offset cardview to hide bottom corners
        CardView.LayoutParams cvParams = (CardView.LayoutParams) cvMask.getLayoutParams();
        FrameLayout.LayoutParams flParams = (FrameLayout.LayoutParams) flImage.getLayoutParams();

        if (Edge.Left.and(edge)) {
            cvParams.leftMargin = -1 * cornerRadius;
            flParams.leftMargin = cornerRadius;
        }

        if (Edge.Top.and(edge)) {
            cvParams.topMargin = -1 * cornerRadius;
            flParams.topMargin = cornerRadius;
        }

        if (Edge.Right.and(edge)) {
            cvParams.rightMargin = -1 * cornerRadius;
            flParams.rightMargin = cornerRadius;
        }

        if (Edge.Bottom.and(edge)) {
            cvParams.bottomMargin = -1 * cornerRadius;
            flParams.bottomMargin = cornerRadius;
        }

        cvMask.setLayoutParams(cvParams);
        flImage.setLayoutParams(flParams);
    }

    private void constrainOffsetEdge() {
        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(clRoot);

        if (Edge.Left.and(edge))   constrainTo(constraints, Edge.Left);
        if (Edge.Top.and(edge))    constrainTo(constraints, Edge.Top);
        if (Edge.Right.and(edge))  constrainTo(constraints, Edge.Right);
        if (Edge.Bottom.and(edge)) constrainTo(constraints, Edge.Bottom);

        constraints.applyTo(clRoot);
    }

    private void constrainTo(ConstraintSet constraints, Edge side) {
        constraints.clear(space.getId(), side.opposite().constraintSide());
        constraints.clear(flPadded.getId(), side.constraintSide());
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        flImage.setOnClickListener(l);
    }
}
