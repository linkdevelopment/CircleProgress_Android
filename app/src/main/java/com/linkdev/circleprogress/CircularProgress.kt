package com.linkdev.circleprogress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginTop

class CircularProgress(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    companion object {
        const val DEFAULT_TEXT_SIZE = 14f
        const val DEFAULT_DIAMETER = 64f
        const val DEFAULT_STROKE_WIDTH = 4f
        const val DEFAULT_CIRCLE_PADDING = 5f
        const val MAX = 100
    }

    private var showText: Boolean = true

    private var progressColor: Int = 0

    private var bgColor: Int = 0

    private var textColor: Int = 0


    @DimenRes
    private var bgStrokeWidth: Int = 0
    @DimenRes
    private var progressStrokeWidth: Int = 0
    @DimenRes
    private var textSize: Int = 0

    @IntegerRes
    private var max: Int = MAX
    private var progress = 0f
    private var diameter: Float = 0f
    private var circlePadding: Int = 0
    private val textPaint = TextPaint()
    private val backGroundPaint = Paint()
    private val progressPaint = Paint()
    private var defaultDiameter: Float = 0f
    private var defaultCirclePadding: Float = 0f
    private var defaultStrokeWidth: Float = 0f
    private var defaultTextSize: Float = 0f

    private val textRect by lazy { Rect() }
    private var progressRect: RectF? = null

    init {

        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.CircularProgress)
        defaultDiameter = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_DIAMETER,
            resources.displayMetrics
        )

        defaultCirclePadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_CIRCLE_PADDING,
            resources.displayMetrics
        )

        defaultStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_STROKE_WIDTH,
            resources.displayMetrics
        )

        defaultTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE,
            resources.displayMetrics
        )

        attributes?.let {
            showText = it.getBoolean(R.styleable.CircularProgress_showText, true)
            max = it.getInteger(R.styleable.CircularProgress_max, MAX)
            bgColor = it.getInt(
                R.styleable.CircularProgress_bgColor, ResourcesCompat.getColor(
                    resources, R.color.gray, null
                )
            )
            bgStrokeWidth =
                it.getDimensionPixelSize(
                    R.styleable.CircularProgress_bgStrokeWidth,
                    defaultStrokeWidth.toInt()
                )
            progressColor =
                it.getColor(
                    R.styleable.CircularProgress_progressColor, ResourcesCompat.getColor(
                        resources, R.color.black, null
                    )
                )
            progressStrokeWidth = it.getDimensionPixelSize(
                R.styleable.CircularProgress_progressStrokeWidth,
                defaultStrokeWidth.toInt()
            )
            textColor = it.getColor(
                R.styleable.CircularProgress_android_textColor, ResourcesCompat.getColor(
                    resources, R.color.black, null
                )
            )
            textSize = it.getDimensionPixelSize(
                R.styleable.CircularProgress_android_textSize,
                defaultTextSize.toInt()
            )
            diameter = it.getDimension(R.styleable.CircularProgress_diameter, defaultDiameter)
            circlePadding =
                it.getDimension(R.styleable.CircularProgress_circlePadding, defaultCirclePadding)
                    .toInt()
        }

        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()

        backGroundPaint.color = bgColor
        backGroundPaint.isAntiAlias = true
        backGroundPaint.style = Paint.Style.STROKE
        backGroundPaint.strokeWidth = bgStrokeWidth.toFloat()

        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.strokeWidth = progressStrokeWidth.toFloat()
        progressRect = RectF()

        attributes?.recycle()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        progressRect?.set(
            circlePadding.toFloat()
            , circlePadding.toFloat(),
            width.toFloat().minus(circlePadding),
            height.toFloat().minus(circlePadding)
        )

        val radius = (width.toFloat().minus(circlePadding*2)).div(2)
        val centerPoint = radius  + circlePadding
        canvas?.drawCircle(centerPoint, centerPoint, radius, backGroundPaint)
        canvas?.drawArc(
            progressRect!!, 0f, (75f / max) * 360, false,
            progressPaint
        )
        if (showText) {

            textPaint.getTextBounds("0", 0, 1, textRect)
            canvas?.drawText(
                "${progress.toInt()}%",
                centerPoint,
                centerPoint + (textRect.height() shr 1),
                textPaint
            )

        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = this.diameter.toInt() + this.circlePadding * 2
        this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    fun setProgress(mProgress: Float) {
        progress = mProgress
        invalidate()
    }


}