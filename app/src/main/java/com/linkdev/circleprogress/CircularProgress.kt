package com.linkdev.circleprogress

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import java.lang.Exception

class CircularProgress(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    companion object {
        const val DEFAULT_TEXT_SIZE = 14f
        const val DEFAULT_DIAMETER = 64f
        const val DEFAULT_STROKE_WIDTH = 4f
        const val DEFAULT_MARGIN = 5f
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

    private val textPaint = Paint()
    private val backGroundPaint = Paint()
    private val progressPaint = Paint()
    private var diameter: Float = 0f
    private var margin: Int = 0
    private var strokeWidth: Float = 0f
    private var defaultTextSize: Float = 0f

    private lateinit var textRect: Rect
    private lateinit var progressRect: RectF

    init {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.CircularProgress)
        diameter = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_DIAMETER,
            resources.displayMetrics
        )

        margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_MARGIN,
            resources.displayMetrics
        ).toInt()

        strokeWidth = TypedValue.applyDimension(
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
            showText = attributes.getBoolean(R.styleable.CircularProgress_showText, true)
            bgColor = attributes.getInt(
                R.styleable.CircularProgress_bgColor, ResourcesCompat.getColor(
                    resources, R.color.gray, null
                )
            )
            bgStrokeWidth =
                attributes.getDimensionPixelSize(R.styleable.CircularProgress_bgStrokeWidth, strokeWidth.toInt())
            progressColor =
                attributes.getColor(
                    R.styleable.CircularProgress_progressColor, ResourcesCompat.getColor(
                        resources, R.color.black, null
                    )
                )
            progressStrokeWidth = attributes.getDimensionPixelSize(
                R.styleable.CircularProgress_progressStrokeWidth,
                strokeWidth.toInt()
            )
            textColor = attributes.getColor(
                R.styleable.CircularProgress_android_textColor, ResourcesCompat.getColor(
                    resources, R.color.black, null
                )
            )
            textSize = attributes.getDimensionPixelSize(
                R.styleable.CircularProgress_android_textSize,
                defaultTextSize.toInt()
            )
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
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = this.diameter.toInt() + this.margin * 2
        this.setMeasuredDimension(size, size)
    }
}