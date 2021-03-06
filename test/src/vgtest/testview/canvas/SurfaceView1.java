//! \file SurfaceView1.java
//! \brief Drawing view class based on cached bitmap and SurfaceView.
// Copyright (c) 2012-2015, https://github.com/rhcad/vgandroid-demo, BSD license

package vgtest.testview.canvas;

import rhcad.touchvg.core.TestCanvas;
import rhcad.touchvg.view.CanvasAdapter;
import vgtest.app.R;
import vgtest.testview.TestFlags;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

//! Drawing view class based on cached bitmap and SurfaceView.
public class SurfaceView1 extends SurfaceView {
    private static final String TAG = "vgtest";
    protected CanvasAdapter mCanvas;
    protected int mCreateFlags;
    private long mDrawnTime;

    public SurfaceView1(Context context) {
        super(context);
        mCanvas = new CanvasAdapter(this);
        mCreateFlags = ((Activity) context).getIntent().getExtras().getInt("flags");
        initCanvas();
        getHolder().addCallback(new SurfaceCallback());

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        this.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                showTime(mDrawnTime);
                return true;
            }
        });
    }

    private void initCanvas() {
        CanvasAdapter.setHandleImageIDs(new int[] { R.drawable.vgdot1, R.drawable.vgdot2,
                R.drawable.vgdot3, R.drawable.vg_lock, R.drawable.vg_unlock });
    }

    @Override
    public void setBackgroundColor(int color) {
        mCanvas.setBackgroundColor(color);
    }

    protected void showTime(long ms) {
        Activity activity = (Activity) this.getContext();
        String title = activity.getTitle().toString();
        int pos = title.indexOf(' ');
        if (pos >= 0) {
            title = title.substring(0, pos);
        }
        activity.setTitle(title + " - " + Long.toString(ms) + " ms");
    }

    protected void doDraw(SurfaceHolder holder) {
        long ms = SystemClock.currentThreadTimeMillis();
        Canvas canvas = holder.lockCanvas();

        if (canvas != null) {
            if (mCanvas.beginPaint(canvas)) {
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                if ((mCreateFlags & TestFlags.INSCROLLVIEW) != 0) {
                    TestCanvas.test(mCanvas, mCreateFlags, 400);
                } else {
                    TestCanvas.test(mCanvas, mCreateFlags);
                }
                mCanvas.endPaint();
            }
            holder.unlockCanvasAndPost(canvas);
        }
        mDrawnTime = SystemClock.currentThreadTimeMillis() - ms;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCanvas != null) {
            mCanvas.delete();
            mCanvas = null;
        }
        super.onDetachedFromWindow();
    }

    class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceCreated(SurfaceHolder holder) {
            doDraw(holder);
            showTime(mDrawnTime);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged");
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
        }
    }
}
