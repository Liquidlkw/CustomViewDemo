package com.example.customviewdemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), LifecycleObserver {
    private val TAG = "MyView"
    private var mWidth = 0f
    private var mHeight = 0f
    private var mRadius = 0f
    private var mAngle = 10f
    private var rotatingJob: Job? = null
    private var sinWaveSamplePath = Path()

    val fillCirclePaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.white)

    }

    val solidLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = ContextCompat.getColor(context, R.color.white)
    }

    val vectorLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = ContextCompat.getColor(context, R.color.purple_200)
    }

    val dashedLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        color = ContextCompat.getColor(context, R.color.yellow)

    }

    val textPaint = Paint().apply {
        textSize = 50F
        typeface = Typeface.DEFAULT_BOLD
        color = ContextCompat.getColor(context, R.color.white)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        //xml 加载完毕后调用 代码创建布局不会调用
        Log.d(TAG, "onFinishInflate: ")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow: ")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure: ")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged: ")
        mHeight = h.toFloat()
        mWidth = w.toFloat()
        mRadius = if (mWidth < mHeight / 2) mWidth / 2 else mHeight / 4
        mRadius -= 20f
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG, "onLayout: ")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw: ")
        if (canvas != null) {
            drawAxises(canvas)
            drawLabel(canvas)
            drawDashCircle(canvas)
            drawVector(canvas)
            drawProjections(canvas)
            drawSinWave(canvas)
        }
    }

    private fun drawAxises(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 2) {
            drawLine(-mWidth / 2, 0f, mWidth / 2, 0f, solidLinePaint)
            drawLine(0f, -mHeight / 2, 0f, mHeight / 2, solidLinePaint)
        }

        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            drawLine(-mWidth / 2, 0f, mWidth / 2, 0f, solidLinePaint)
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "onDetachedFromWindow: ")
    }

    private fun drawLabel(canvas: Canvas) {
        canvas.apply {
            drawRect(100f, 100f, 600f, 250f, solidLinePaint)
            drawText("三角函数与旋转矢量", 120f, 195f, textPaint)

        }
    }

    private fun drawDashCircle(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            drawCircle(0f, 0f, mRadius, dashedLinePaint)
        }
    }

    //在圆中选择的线
    private fun drawVector(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            withRotation(-mAngle) {
                drawLine(0f, 0f, mRadius, 0f, vectorLinePaint)

            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startRotating() {
        rotatingJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(100)
                mAngle += 5f
                invalidate()
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseRotating() {
        rotatingJob?.cancel()
    }


    private fun drawProjections(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 2) {
            drawCircle(mRadius * cos(mAngle.toRadians()), 0F, 10F, fillCirclePaint)
        }

        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            drawCircle(mRadius * cos(mAngle.toRadians()), 0F, 10F, fillCirclePaint)
        }

        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            val x = mRadius * cos(mAngle.toRadians())
            val y = mRadius * sin(mAngle.toRadians())
            withTranslation(x, -y) {
                drawLine(0f, 0f, 0f, y, solidLinePaint)
                drawLine(0f, 0f, 0f, -mHeight / 4 + y, dashedLinePaint)
            }
        }
    }

    private fun Float.toRadians() = this / 180 * PI.toFloat()

    private fun drawSinWave(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 2) {
            val sampleCount = 50
            val dy = mHeight / 2 / sampleCount
            sinWaveSamplePath.reset()
            sinWaveSamplePath.moveTo(mRadius * cos(mAngle.toRadians()), 0f);
            repeat(sampleCount) {
                val x = mRadius * cos(it * -0.15 + mAngle.toRadians())
                val y = -dy * it
                sinWaveSamplePath.quadTo(x.toFloat(),y.toFloat(),x.toFloat(),y.toFloat())
            }

            drawPath(sinWaveSamplePath,vectorLinePaint)
            drawTextOnPath("猴哥读书",sinWaveSamplePath,0f,100f,textPaint)
        }
    }

}


































