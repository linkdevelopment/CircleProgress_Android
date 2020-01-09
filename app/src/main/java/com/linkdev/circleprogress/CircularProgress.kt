package com.linkdev.circleprogress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginTop

class CircularProgress(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
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
    private var progress = 0
    private var diameter: Float = 0f
    private var circlePadding: Int = 0
    private val textPaint = Paint()
    private val backGroundPaint = Paint()
    private val progressPaint = Paint()
    private var defaultDiameter: Float = 0f
    private var defaultCirclePadding: Float = 0f
    private var defaultStrokeWidth: Float = 0f
    private var defaultTextSize: Float = 0f

    private var textRect: Rect? = null
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
        textPaint.style = Paint.Style.STROKE
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()

        backGroundPaint.color = bgColor
        backGroundPaint.isAntiAlias = true
        backGroundPaint.style = Paint.Style.STROKE
        backGroundPaint.strokeWidth = bgStrokeWidth.toFloat()

        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeJoin = Paint.Join.ROUND
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.strokeWidth = progressStrokeWidth.toFloat()

        attributes?.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (progressRect == null) {
            progressRect = RectF(
                marginLeft.toFloat() + circlePadding
                , marginTop.toFloat() + circlePadding,
                (marginLeft + diameter + circlePadding),
                marginTop + diameter + circlePadding
            )
        }

        val centerPoint = diameter / 2 + circlePadding
        canvas?.drawCircle(centerPoint, centerPoint, diameter / 2, backGroundPaint)

        if (showText) {
            if (textRect == null) {
                textRect = Rect()
                textPaint.getTextBounds("0", 0, 1, textRect)
                canvas?.drawText("50%", centerPoint, centerPoint , textPaint)
            }

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = this.diameter.toInt() + this.circlePadding * 2
        this.setMeasuredDimension(size, size)
    }


}