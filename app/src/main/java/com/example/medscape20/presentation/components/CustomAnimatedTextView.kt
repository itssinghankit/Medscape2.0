package com.example.medscape20.presentation.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

class CustomAnimatedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var animator: ValueAnimator? = null

    fun animateToValue(endValue: Int, duration: Long = 1000) {
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, endValue).apply {
            interpolator = AccelerateDecelerateInterpolator()
            this.duration = duration
            addUpdateListener { animation ->
                text = animation.animatedValue.toString()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}