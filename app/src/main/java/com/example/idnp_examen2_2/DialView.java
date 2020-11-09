package com.example.idnp_examen2_2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class DialView extends View {
    private static int SELECTION_COUNT = 7;

    private static float FONT_SIZE = 40f;
    private static float START = 50;
    private static float FINISH = 50;
    private float mWidth;
    private float mHeight;
    private float mWidthPadded;
    private float mHeightPadded;
    private Paint mTextPaint;
    private Paint mDialPaint;
    private float mRadius;
    private int mActiveSelection;

    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        // Paint styles used for rendering are created here, rather than at render-time. This
        // is a performance optimization, since onDraw() will get called frequently.
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(FONT_SIZE);

        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(Color.parseColor("#FFEAEAEA"));

        // Initialize current selection. This will store where the dial's "indicator" is pointing.
        mActiveSelection = 0;

    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);

        // Detect what type of accessibility event is being passed in.
        int eventType = event.getEventType();

        // Common case: The user has interacted with our view in some way. State may or may not
        // have been changed. Read out the current status of the view.
        //
        // We also set some other metadata which is not used by TalkBack, but could be used by
        // other TTS engines.
        if (eventType == AccessibilityEvent.TYPE_VIEW_SELECTED ||
                eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            event.getText().add("Mode selected: " + Integer.toString(mActiveSelection + 1) + ".");
            event.setItemCount(SELECTION_COUNT);
            event.setCurrentItemIndex(mActiveSelection);
        }

        // When a user first focuses on our view, we'll also read out some simple instructions to
        // make it clear that this is an interactive element.
        if (eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            event.getText().add("Tap to change.");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Account for padding
        float xPadding = (float) (getPaddingLeft() + getPaddingRight());
        float yPadding = (float) (getPaddingTop() + getPaddingBottom());

        // Compute available width/height
        mWidth = w;
        mHeight = h;
        mWidthPadded = w - xPadding;
        mHeightPadded = h - yPadding;
        mRadius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw dial
        //canvas.drawCircle(mWidth / 2, mHeight / 2, (float) mRadius, mDialPaint);
        canvas.drawRect(0,mHeight,mWidth,0,mDialPaint);
        // Draw text labels
        final float labelRadius = mRadius + 10;
        float []weekStats={100,200,300,80,60,94,80};
        float []weekStatsInTable={100,200,300,80,60,94,80};
        String[]week={"lun","mar","mie","jue","vie","sab","dom"};
        float max=maxStat(weekStats);
        float startgraph=0;
        mDialPaint.setColor(Color.parseColor("#000000"));
        for (int i = 0; i < SELECTION_COUNT; i++) {
            float xData = computeXForPosition(i, mWidth);

            canvas.drawText(week[i], xData, mHeight-10, mTextPaint);

            canvas.drawLine(xData,START,xData,mHeight-FINISH,mDialPaint);
            weekStatsInTable[i]=computeXStat(weekStats[i],max);
            canvas.drawText(Float.toString(weekStats[i]), xData, weekStatsInTable[i]+5, mTextPaint);
            if(i>0){
                float xDataPast = computeXForPosition(i-1, mWidth);
                canvas.drawLine(xDataPast,weekStatsInTable[i-1],xData,weekStatsInTable[i],mDialPaint);
            }
        }

        canvas.drawLine(50,START,mWidth-50,START,mDialPaint);
        canvas.drawLine(50,mHeight-FINISH,mWidth-50,mHeight-FINISH,mDialPaint);
    }
    private float computeXForPosition(final int pos, final float width) {
        float espacio=width/(SELECTION_COUNT+1);
        return espacio+espacio*pos;
    }
    private float computeXStat(final float stat,float max) {
        float size=mHeight-START-FINISH;
        return mHeight-(stat*size/max);
    }
    private float maxStat(float[]stats) {
        float max=stats[0];
        for(int i=0;i<SELECTION_COUNT;i++){
            if(stats[i]>max){
                max=stats[i];
            }
        }
        return max;
    }
}