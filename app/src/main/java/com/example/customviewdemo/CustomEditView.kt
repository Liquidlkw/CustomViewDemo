package com.example.customviewdemo

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat

class CustomEditView @kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {

    private var iconDrawable: Drawable? = null
    private val TAG = "CustomEditView"


    init {
        context.theme.obtainStyledAttributes(attrs,R.styleable.CustomEditView,0,0)
                .apply {
                    try {
                    val iconId = getResourceId(R.styleable.CustomEditView_clearIcon,0)
                    if (iconId!=0){
                        iconDrawable = ContextCompat.getDrawable(context,iconId);
                    }else{
                        iconDrawable = ContextCompat.getDrawable(context,R.drawable.ic_baseline_clear_24)
                    }
                    }finally {
                        recycle()
                    }
                }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        toggleClearIcon()
    }

    private fun toggleClearIcon() {
        val icon = if (isFocused && text?.isNotEmpty() == true) iconDrawable else null
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: " + event?.x)
        event?.let { e ->
            iconDrawable?.let {
                if (e.action == MotionEvent.ACTION_UP
                        && e.x > width - it.intrinsicWidth
                        && e.y > height / 2 - it.intrinsicHeight / 2
                        && e.y < height / 2 + it.intrinsicHeight / 2
                ) text?.clear()
            }
        }
        performClick()
        return super.onTouchEvent(event)
    }


    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        toggleClearIcon()
    }
}