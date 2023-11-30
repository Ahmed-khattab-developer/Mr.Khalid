package com.elgaban.mrkhalid.utils.fontsmaterialuiux;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

/**
 * for more visit http://materialuiux.com
 */
public class cairoButton  extends AppCompatButton
{

    public cairoButton(Context context)
    {
        super(context);
        init();
    }

    public cairoButton(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.borderlessButtonStyle);
        init();
    }

    public cairoButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
        {
            setTextSize(18);
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Cairo-Regular.ttf");
            setTypeface(tf);
        }
    }
}
