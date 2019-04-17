package com.dreamgyf.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundImageView extends AppCompatImageView {

    private Paint paint = new Paint();

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return; // couldn't resolve the URI
        }
        if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }
        //拉伸图片，使宽高相等，以适应view的大小
        drawable.setBounds(0, 0, getWidth(), getHeight());
        //创建图层，将图层压入栈，canvas的各种绘图操作将在该图层实现
        int layer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //先把原图画出
        drawable.draw(canvas);
        //画一个圆
        Bitmap roundBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas roundBitmapCanvas = new Canvas(roundBitmap);
        paint.setXfermode(null);
        roundBitmapCanvas.drawCircle((float) getWidth() / 2,(float) getHeight() / 2,(getWidth() < getHeight()) ? (float) getWidth() / 2 : (float) getHeight() / 2,paint);
        //设置画笔模式为上一图层显示在交集中
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(roundBitmap,0,0,paint);
        //将图层弹出栈，并将图层的内容提交到最终的canvas上
        canvas.restoreToCount(layer);
    }
}
