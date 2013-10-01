package ru.yandex.yac13.example.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PavementLayout extends ViewGroup {

    private final int DEFAULT_ROW_HEIGHT = 48;
    private final int DEFAULT_COLUMN_WIDTH = 48;

    private float rowHeight = DEFAULT_ROW_HEIGHT;
    private float columnWidth;

    float spacingVertical = 0;
    float spacingHorizontal = 0;

    public PavementLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PavementLayout);
        spacingVertical = a.getDimension(R.styleable.PavementLayout_spacing_vertical, 0f);
        spacingHorizontal = a.getDimension(R.styleable.PavementLayout_spacing_horizontal, 0f);
        rowHeight = a.getDimension(R.styleable.PavementLayout_row_height, DEFAULT_ROW_HEIGHT);

        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            LayoutParams p = (LayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int childLeft = (int) (p.left * (columnWidth + spacingHorizontal) + getPaddingLeft());
            int childTop = (int) (p.top * (rowHeight + spacingVertical) + getPaddingTop());

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int measuredWidth = 0;
        int measuredHeight = 0;

        boolean measureWidthByChildren = false;
        boolean measureHeightByChildren = false;

        int columns = 0;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                LayoutParams p = (LayoutParams) child.getLayoutParams();
                columns = Math.max(p.left + p.width, columns);
            }

            measuredWidth = width;

            columnWidth = (float) measuredWidth / columns;
        } else {
            measureWidthByChildren = true;
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = height;
        } else {
            measureHeightByChildren = true;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            LayoutParams p = (LayoutParams) child.getLayoutParams();

            int childWidth = (int) (p.width * columnWidth + (p.width - 1) * spacingHorizontal);
            int childHeight = (int) (p.height * rowHeight + (p.height - 1) * spacingVertical);

            int widthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

            child.measure(widthSpec, heightSpec);

            if (measureWidthByChildren) {
                int childLeft = (int) (p.left * (columnWidth + spacingHorizontal) + spacingHorizontal);
                measuredWidth = Math.max(measuredWidth, childLeft + childWidth);
            }

            if (measureHeightByChildren) {
                int childTop = (int) (p.top * (rowHeight + spacingVertical) + spacingVertical);
                measuredHeight = Math.max(measuredHeight, childTop + childHeight);
            }

        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        int left = 0;
        int top = 0;
        int width = 1;
        int height = 1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(WRAP_CONTENT, WRAP_CONTENT);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PavementLayout_Layout);
            left = a.getInt(R.styleable.PavementLayout_Layout_left, 0);
            top = a.getInt(R.styleable.PavementLayout_Layout_top, 0);
            width = a.getInt(R.styleable.PavementLayout_Layout_width, 1);
            height = a.getInt(R.styleable.PavementLayout_Layout_height, 1);
            a.recycle();

        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);

            if (source instanceof LayoutParams) {
                LayoutParams p = (LayoutParams) source;

                this.left = p.left;
                this.top = p.top;
                this.width = p.width;
                this.height = p.height;
            }
        }
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

}
