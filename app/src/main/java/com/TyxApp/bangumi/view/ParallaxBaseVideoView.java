package com.TyxApp.bangumi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.TyxApp.bangumi.R;
import com.kk.taurus.playerbase.widget.BaseVideoView;

public class ParallaxBaseVideoView extends BaseVideoView {
    private int videoOffset;
    private int minOffset;

    public ParallaxBaseVideoView(Context context) {
        this(context, null);
    }

    public ParallaxBaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOfferSet(int offset) {
        offset = Math.max(minOffset, offset);
        if (offset != getTranslationY()) {
            setTranslationY(offset);
            videoOffset = (int) (offset * 0.6);
            postInvalidateOnAnimation();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h > getMinimumHeight()) {
            minOffset = getMinimumHeight() - h;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (videoOffset != 0) {
            int saveCount = canvas.getSaveCount();
            canvas.translate(0, -videoOffset);
            super.onDraw(canvas);
            canvas.restoreToCount(saveCount);
        } else {
            super.onDraw(canvas);
        }
    }

    public int getMinOffset() {
        return minOffset;
    }
}
