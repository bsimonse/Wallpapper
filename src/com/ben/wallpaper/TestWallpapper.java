package com.ben.wallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class TestWallpapper extends WallpaperService {

    private final Handler mHandler = new Handler();

    @Override
    public Engine onCreateEngine() {
        return new PlayEngine();
    }

    class PlayEngine extends Engine {
    	
    	private final Paint bgPaint = new Paint();
        private final Paint whitePaint = new Paint();
        private final Paint circlePaint = new Paint();
        private final Paint circleAltPaint = new Paint();
        
        private int mBaseRadius = 100;
        private int mWidth;
        private int mHeight;
        private long  mTimeout = 10;
        private int sparkleFrame = -1;
        private int sparkleFrameMax = 100;
        private float frame = 0;
        
        private int circleRadius = 25;
        private int mainLoopRadius = 170;
        private int subLoopRadius = 60;
        
        private int[] circ1Colors = {0xff, 0xff, 0, 0};
        private int[] circ2Colors = {0xff, 0, 0, 0xff};
        
        private Canvas helperCanvas = new Canvas();
        private Bitmap mBitmap;
        
        private final Runnable mDrawingLoop = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        private boolean mVisible;

        private int buildColor(int[] colors)
        {
        	int builder = colors[0];
        	builder = builder << 8;
        	builder += colors[1];
        	builder = builder << 8;
        	builder += colors[2];
        	builder = builder << 8;
        	builder += colors[3];
        	
        	return builder;
        }
        
        private int getIntermediate(int start, int end, float percent)
        {
        	float dif = end-start;
        	return (int)(start+percent*dif);
        }
        
        PlayEngine() {
        	
        	
            Paint paint = whitePaint;
            paint.setColor(0xffffffff);
            paint.setAntiAlias(false);
            paint.setStrokeWidth(2);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.FILL);
            
            paint = circlePaint;
            paint.setColor(buildColor(circ1Colors));
            paint.setAntiAlias(true);
            paint.setStrokeWidth(1);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            
            paint = circleAltPaint;
            paint.setColor(buildColor(circ2Colors));
            paint.setAntiAlias(true);
            paint.setStrokeWidth(1);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            
            paint = bgPaint;
            paint.setColor(0x0a000000);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawingLoop);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawingLoop);
            }
        }
        
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mWidth = width;
            mHeight = height;
            if(mBitmap == null)
            {
            	mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            	helperCanvas.setBitmap(mBitmap);
            }
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawingLoop);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            float pixels = -xPixels;
            
            //0, 135, 270, 405, 540
            if(pixels < 135)
            {
            	float percent = pixels/135;
            	//0xffff00
	            circ1Colors[1] = 0xff;
	            circ1Colors[2] = getIntermediate(0xff, 0x99, percent);
	            circ1Colors[3] = 0;
	            
	            //0xff00ff
	            circ2Colors[1] = getIntermediate(0xff, 0x99, percent);
	            circ2Colors[2] = getIntermediate(0x00, 0x99, percent);
	            circ2Colors[3] = getIntermediate(0xff, 0x99, percent);
            }
            else if(pixels < 270)
            {
            	float percent = (pixels-135)/135;
            	
            	//0xff9900
	            circ1Colors[1] = 0xff;
	            circ1Colors[2] = getIntermediate(0x99, 0x00, percent);
	            circ1Colors[3] = 0;
	            
            	//0x999999
	            circ2Colors[1] = getIntermediate(0x99, 0x00, percent);
	            circ2Colors[2] = getIntermediate(0x99, 0x00, percent);
	            circ2Colors[3] = getIntermediate(0x99, 0xff, percent);
            }
            else if(pixels < 405)
            {
            	float percent = (pixels-270)/135;
            	
            	//0xff0000
	            circ1Colors[1] = 0xff;
	            circ1Colors[2] = getIntermediate(0, 0xff, percent);
	            circ1Colors[3] = getIntermediate(0, 0xff, percent);
	            
            	//0x0000ff
	            circ2Colors[1] = getIntermediate(0x00, 0x33, percent);
	            circ2Colors[2] = getIntermediate(0x00, 0xcc, percent);
	            circ2Colors[3] = getIntermediate(0xff, 0xcc, percent);
            }
            else if(pixels < 540)
            {
            	float percent = (pixels-405)/135;
            	//0xffffff
	            circ1Colors[1] = getIntermediate(0xff, 0x00, percent);
	            circ1Colors[2] = getIntermediate(0xff, 0x33, percent);
	            circ1Colors[3] = getIntermediate(0xff, 0x66, percent);
	            
            	//0x33cccc
	            circ2Colors[1] = getIntermediate(0x33, 0x00, percent);
	            circ2Colors[2] = getIntermediate(0xcc, 0xff, percent);
	            circ2Colors[3] = getIntermediate(0xcc, 0x00, percent);
            }
            else
            {
            	//0x003366
	            circ1Colors[1] = 0;
	            circ1Colors[2] = 0x33;
	            circ1Colors[3] = 0x66;
	            
            	//0x00ff00
	            circ2Colors[1] = 0;
	            circ2Colors[2] = 0xff;
	            circ2Colors[3] = 0;
            }
            
            Paint paint = circlePaint;
            paint.setColor(buildColor(circ1Colors));
            
            paint = circleAltPaint;
            paint.setColor(buildColor(circ2Colors));
            
            drawFrame();
        }

        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && sparkleFrame == -1) {
            	sparkleFrame = 0;
            }
            
            super.onTouchEvent(event);
        }
        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable. You can do any drawing you want in
         * here. This example draws a wireframe cube.
         */
        void drawFrame() {
        	
        	if(mBitmap==null){return;}
        	
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // draw something
                    drawDat(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
            
            // Reschedule the next redraw
            mHandler.removeCallbacks(mDrawingLoop);
            if (mVisible) {
                mHandler.postDelayed(mDrawingLoop, mTimeout);
            }
        }
        
        void drawDat(Canvas c)
        {
        	drawBG(helperCanvas);
        	drawCircle(helperCanvas);
        	//drawSparkles(c);
        	
        	c.drawBitmap(mBitmap, new Matrix(), null);
        }
        
        void drawBG(Canvas c)
        {
        	c.drawRect(new Rect(0,0,mWidth,mHeight), bgPaint);
        }
        
        void drawCircle(Canvas c)
        {
        	float fullCircle = 360.0f;
			float bigRotateRate = 0.55f;
			float smallRotateRate = 1.55f;
			
			float commonFactor = 10.0f*bigRotateRate*smallRotateRate*fullCircle;
			frame += 3.0f;
			while(frame > commonFactor){frame-=commonFactor;}
     
        	c.save();
        	
        	c.translate(mWidth/2, mHeight/2);
        	c.rotate(frame*bigRotateRate);
        	c.translate(mainLoopRadius, 0);
        	
        	c.rotate(frame*smallRotateRate);
        	
        	c.translate(subLoopRadius, 0);
        	c.drawArc(new RectF(-circleRadius, -circleRadius, circleRadius, circleRadius), 0, 360, false, circlePaint);
        	
        	c.translate(-2*subLoopRadius, 0);
        	c.drawArc(new RectF(-circleRadius, -circleRadius, circleRadius, circleRadius), 0, 360, false, circleAltPaint);
        	
        	c.restore();
        	
        }
        
        void drawSparkles(Canvas c)
        {
        	if(sparkleFrame < 0){return;}
        	int centerX = mWidth/2;
        	int centerY = mHeight/2;
        	int newRadius = mBaseRadius + 50;
        	RectF center = new RectF(centerX-newRadius, centerY-newRadius, centerX+newRadius, centerY+newRadius);
        	c.drawArc(center, 0, 360.0f/100.0f * sparkleFrame, false, circlePaint);
        	sparkleFrame++;
        	if(sparkleFrame > sparkleFrameMax)
        	{sparkleFrame = -1;}
        }
    }
}
