package se.umu.cs.c12msr.imagetimer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Mattias-stationary on 23-Aug-16.
 */
public class ResizableImageView extends ImageView {

    private Context mContext;

    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
