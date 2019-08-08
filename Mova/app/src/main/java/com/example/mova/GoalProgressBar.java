package com.example.mova;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class GoalProgressBar extends View {
    // code derived from: https://guides.codepath.org/android/Progress-Bar-Custom-View
        // skipped custom indicator type with enums
        // skipped saving instance state (across screen rotations)

    // Attributes & values

    private int progress;

    /** Bar color for the filled section (progress completed). */
    private int filledColor;
    /** Bar color for the unfilled section (remaining progress). */
    private int unfilledColor;

    /** Thickness of the progress bar. */
    private int thickness;
    /** The maximum length of the progress bar; if the parent container is larger, rounds the bottom. */
    private int maxLength;

    /** Orientation of the progress bar. (0: horizontal, 1: vertical) */
    private int orientation;
    /** The end to round when maxLength is exceeded. */
    private int autoRoundedEnd;
    /** Whether or not to round the end of the progress bar that is not automatically rounded. */
    private boolean roundOtherEnd;
    /** The side of the bar to round when length exceeds maxLength. (0: start, 1: end) */
    private int roundSide;
    /** The end of the bar from which to start drawing progress. (0: start, 1: end) */
    private int drawFrom;


    /** Whether the end of the bar should be rounded. True when the bar's parent length is greater than the maxLength. */
    private boolean shouldAutoRound;

    // Animation & drawing

    private ValueAnimator barAnimator;
    private Paint progressPaint;
    private Paint erasePaint;

    public static final int PROGRESS_MAX = 100;

    // Corners

    private VectorDrawable verticalBottomRightCorner   = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_vbr);
    private VectorDrawable verticalBottomLeftCorner    = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_vbl);
    private VectorDrawable verticalTopRightCorner      = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_vtr);
    private VectorDrawable verticalTopLeftCorner       = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_vtl);

    private VectorDrawable horizontalBottomRightCorner = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_hbr);
    private VectorDrawable horizontalBottomLeftCorner  = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_hbl);
    private VectorDrawable horizontalTopRightCorner    = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_htr);
    private VectorDrawable horizontalTopLeftCorner     = (VectorDrawable) getResources().getDrawable(R.drawable.ic_gpb_corner_htl);

    public GoalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        erasePaint = new Paint();
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // extract custom attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GoalProgressBar, 0, 0);

        try {
            // extract attributes to member variables from typedArray
            setFilledColor(typedArray.getColor(R.styleable.GoalProgressBar_filledColor, getResources().getColor(R.color.blueMid)));
            setUnfilledColor(typedArray.getColor(R.styleable.GoalProgressBar_unfilledColor, getResources().getColor(R.color.blueUltraLight)));

            setThickness(typedArray.getDimensionPixelOffset(R.styleable.GoalProgressBar_thickness, getResources().getDimensionPixelSize(R.dimen.elementMargin)));
            setMaxLength(typedArray.getDimensionPixelOffset(R.styleable.GoalProgressBar_maxLength, Integer.MAX_VALUE));

            setOrientation(typedArray.getInt(R.styleable.GoalProgressBar_barOrientation, 1));
            setAutoRoundedEnd(typedArray.getInt(R.styleable.GoalProgressBar_autoRoundedEnd, 1));
            setRoundOtherEnd(typedArray.getBoolean(R.styleable.GoalProgressBar_roundOtherEnd, false));
            setRoundSide(typedArray.getInt(R.styleable.GoalProgressBar_roundSide, 1));
            setDrawFrom(typedArray.getInt(R.styleable.GoalProgressBar_drawFrom, (orientation == 0) ? 0 : 1));
            shouldAutoRound = false;

            setProgress(typedArray.getInt(R.styleable.GoalProgressBar_progress, 0));
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Handles the sizing of our view, allowing for customization based on the goal indicator.
     * Good explanation of this method: https://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
     * @param widthMeasureSpec The 'instructions'/'constraints' of the width.
     * @param heightMeasureSpec The 'instructions'/'constraints' of the height.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get all relevant information from measure specs
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Set width and height based on measure spec
        int width, height;

        if (orientation == 0) {
            width = Math.min(parentWidth, maxLength);
            height = meetMeasureSpec(heightMode, parentHeight, thickness);
            shouldAutoRound = parentWidth > maxLength;
        } else {
            height = Math.min(parentHeight, maxLength);
            width = meetMeasureSpec(widthMode, parentWidth, thickness);
            shouldAutoRound = parentHeight > maxLength;
        }

        setMeasuredDimension(width, height);
    }

    protected int meetMeasureSpec(int measureSpecMode, int parent, int desired) {
        switch (measureSpecMode) {
            case MeasureSpec.EXACTLY:
                return parent;
            case MeasureSpec.AT_MOST:
                return Math.min(parent, desired);
            case MeasureSpec.UNSPECIFIED:
            default:
                return desired;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // Set line dimensions
        int half = (orientation == 0) ? height / 2 : width / 2;
        int progressLength = (orientation == 0) ? (int) (width * progress / 100f) : (int) (height * progress / 100f);

        // draw the part of the bar that's filled (completed)
        progressPaint.setStrokeWidth(thickness);
        progressPaint.setColor(filledColor);

        if (orientation == 0) {
            canvas.drawLine(
                (drawFrom == 0) ? 0 : width, half,
                (drawFrom == 0) ? progressLength : width - progressLength, half,
                progressPaint
            );
        } else {
            canvas.drawLine(
                half, (drawFrom == 0) ? 0 : height,
                half, (drawFrom == 0) ? progressLength : height - progressLength,
                progressPaint
            );
        }

        // draw the unfilled section
        progressPaint.setColor(unfilledColor);

        if (orientation == 0) {
            canvas.drawLine(
                (drawFrom == 0) ? progressLength : width - progressLength, half,
                (drawFrom == 0) ? width : 0, half,
                progressPaint
            );
        } else {
            canvas.drawLine(
                half, (drawFrom == 0) ? progressLength : height - progressLength,
                half, (drawFrom == 0) ? height : 0,
                progressPaint
            );
        }

        // Cut out corners as necessary

        if (shouldAutoRound) {
            VectorDrawable corner = getCorner(true);
            scaleCorner(corner);
            cutoutCorner(canvas, corner, true);
        }

        if (roundOtherEnd) {
            VectorDrawable corner = getCorner(false);
            scaleCorner(corner);
            cutoutCorner(canvas, corner, false);
        }
    }

    /**
     * Get the corner to cut off for the current configuration.
     * @param forAutoRoundedCorner Whether to retrieve the auto-rounded corner or the other corner.
     * @return The corner cutoff drawable for the specified corner.
     */
    private VectorDrawable getCorner(boolean forAutoRoundedCorner) {
        if (forAutoRoundedCorner) {
            if (autoRoundedEnd == 0) {
                if (orientation == 0) {
                    if (roundSide == 0) {
                        return horizontalTopLeftCorner;
                    } else {
                        return horizontalBottomLeftCorner;
                    }
                } else {
                    if (roundSide == 0) {
                        return verticalTopLeftCorner;
                    } else {
                        return verticalTopRightCorner;
                    }
                }
            } else {
                if (orientation == 0) {
                    if (roundSide == 0) {
                        return horizontalTopRightCorner;
                    } else {
                        return horizontalBottomRightCorner;
                    }
                } else {
                    if (roundSide == 0) {
                        return verticalBottomLeftCorner;
                    } else {
                        return verticalBottomRightCorner;
                    }
                }
            }
        } else { // If not for the auto-rounded corner, then for the other corner
            if (autoRoundedEnd == 0) {
                if (orientation == 0) {
                    if (roundSide == 0) {
                        return horizontalTopRightCorner;
                    } else {
                        return horizontalBottomRightCorner;
                    }
                } else {
                    if (roundSide == 0) {
                        return verticalBottomLeftCorner;
                    } else {
                        return verticalBottomRightCorner;
                    }
                }
            } else {
                if (orientation == 0) {
                    if (roundSide == 0) {
                        return horizontalTopLeftCorner;
                    } else {
                        return horizontalBottomLeftCorner;
                    }
                } else {
                    if (roundSide == 0) {
                        return verticalTopLeftCorner;
                    } else {
                        return verticalTopRightCorner;
                    }
                }
            }
        }
    }

    private void scaleCorner(VectorDrawable corner) {
        float newWidth, newHeight;

        if (orientation == 0) {
            float aspectWH = (float) corner.getIntrinsicWidth() / (float) corner.getIntrinsicHeight();
            newHeight = (float) thickness;
            newWidth = newHeight * aspectWH;
        } else {
            float aspectHW = (float) corner.getIntrinsicHeight() / (float) corner.getIntrinsicWidth();
            newWidth = (float) thickness;
            newHeight = newWidth * aspectHW;
        }

        corner.setBounds(0, 0, Math.round(newWidth), Math.round(newHeight));
    }

    private void cutoutCorner(Canvas canvas, VectorDrawable corner, boolean isAutoRoundedCorner) {
        // Convert corner to bitmap
        Bitmap bmp = Bitmap.createBitmap(corner.getBounds().width(), corner.getBounds().height(), Bitmap.Config.ARGB_8888);
        Canvas cornerCanvas = new Canvas(bmp);
        corner.draw(cornerCanvas);

        // Select correct position on canvas
        Rect rect;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (isAutoRoundedCorner) {
            if (orientation == 0) {
                if (autoRoundedEnd == 0) {
                    rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
                } else {
                    rect = new Rect(width - bmp.getWidth(), 0, width, bmp.getHeight());
                }
            } else {
                if (autoRoundedEnd == 0) {
                    rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
                } else {
                    rect = new Rect(0, height - bmp.getHeight(), bmp.getWidth(), height);
                }
            }
        } else {
            if (orientation == 0) {
                if (autoRoundedEnd == 0) {
                    rect = new Rect(width - bmp.getWidth(), 0, width, bmp.getHeight());
                } else {
                    rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
                }
            } else {
                if (autoRoundedEnd == 0) {
                    rect = new Rect(0, height - bmp.getHeight(), bmp.getWidth(), height);
                } else {
                    rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
                }
            }
        }

        // Erase bitmap from canvas at chosen coordinates
        canvas.drawBitmap(bmp, rect.left, rect.top, erasePaint);
    }

    public void setFilledColor(int filledColor) {
        this.filledColor = filledColor;
        invalidate();
    }

    public void setUnfilledColor(int unfilledColor) {
        this.unfilledColor = unfilledColor;
        invalidate();
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        invalidate();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        invalidate();
    }

    public void setOrientation(int orientation) {
        if (!(orientation == 0 || orientation == 1)) {
            throw new IllegalArgumentException("Orientation must be 0 for horizontal or 1 for vertical; " + orientation + " is not a valid value.");
        }
        this.orientation = orientation;
        invalidate();
    }

    public void setAutoRoundedEnd(int lengthRoundedEnd) {
        this.autoRoundedEnd = lengthRoundedEnd;
        invalidate();
    }

    public void setRoundOtherEnd(boolean roundOtherEnd) {
        this.roundOtherEnd = roundOtherEnd;
        invalidate();
    }

    public void setRoundSide(int roundSide) {
        this.roundSide = roundSide;
        invalidate();
    }

    public void setDrawFrom(int drawFrom) {
        this.drawFrom = drawFrom;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setProgress(progress, false);
    }

    private void setProgress(int progress, boolean animate) {
        if (animate) {
            barAnimator = ValueAnimator.ofFloat(0, 1);

            barAnimator.setDuration(700);

            // reset progress without animating
            setProgress(0, false);

            barAnimator.setInterpolator(new DecelerateInterpolator());

            barAnimator.addUpdateListener((ValueAnimator animation) -> {
                float interpolation = (float) animation.getAnimatedValue();
                setProgress((int) (interpolation * progress), false);
            });

            if (!barAnimator.isStarted()) {
                barAnimator.start();
            }
        } else {
            this.progress = progress;
            postInvalidate();
        }
    }
}
