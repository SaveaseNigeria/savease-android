package saveaseng.ng.savease.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class VoucherTextView extends android.support.v7.widget.AppCompatTextView  {
    public VoucherTextView(Context context) {
        super(context);
    }

    public VoucherTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoucherTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int heightTextView = getHeight();
        int centerTextView = heightTextView / 2;
        Rect r = new Rect();
        getLineBounds(0, r);
        int lineHeight = r.bottom - r.top +1;
        int centerLine = lineHeight / 2;
        int topDrawable = centerLine - centerTextView;
        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable drawableLeft = compoundDrawables[0];
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, topDrawable, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight() + topDrawable);
        }
        Drawable drawableRight = compoundDrawables[2];
        if (drawableRight != null) {
            drawableRight.setBounds(0, topDrawable, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight() + topDrawable);
        }
    }
}
