package com.linkdev.circleprogress

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CircularProgress(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    companion object {
        const val DEFAULT_TEXT_SIZE = 12f
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

    private var max: Int = MAX
    private var progress = 0f
    private var circlePadding: Int = 0

    private var defaultCirclePadding: Float = 0f
    private var defaultStrokeWidth: Float = 0f
    private var defaultTextSize: Float = 0f
    private var startAngle: Float = 0f
    private var progressDirection: Float = 1f
    private var squareCap: Boolean = false
    private var showDecimals: Boolean = false
    private var showPercentage: Boolean = true
    private var circleBg: Int = Color.TRANSPARENT
    private var textFont: Typeface? = Typeface.DEFAULT

    private var text: String? = ""

    private val textRect by lazy { Rect() }
    private val progressRect by lazy { RectF() }
    private val circleBgRect by lazy { RectF() }
    private val bgStrokeRect by lazy { RectF() }

    private val textPaint = TextPaint()
    private val backGroundPaint = Paint()
    private val progressPaint = Paint()
    private val circleBGPaint = Paint()

    init {

        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.CircularProgress)


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
            startAngle = it.getInt(R.styleable.CircularProgress_startAngle, 0).toFloat()
            progressDirection =
                it.getInt(R.styleable.CircularProgress_progressDirection, 1).toFloat()
            setMax(it.getInteger(R.styleable.CircularProgress_max, MAX))
            setProgress(it.getFloat(R.styleable.CircularProgress_progress, 0f))
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

            squareCap = it.getBoolean(R.styleable.CircularProgress_squareCap, false)

            showDecimals = it.getBoolean(R.styleable.CircularProgress_showDecimals, false)

            showPercentage = it.getBoolean(R.styleable.CircularProgress_showPercentage, true)

            text = it.getString(R.styleable.CircularProgress_android_text)

            circleBg = it.getColor(
                R.styleable.CircularProgress_circleBg, ResourcesCompat.getColor(
                    resources, R.color.transparent, null
                )
            )

        }

        circlePadding =
            defaultCirclePadding
                .toInt()


        textPaint.typeface = textFont
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()

        backGroundPaint.color = bgColor
        backGroundPaint.isAntiAlias = true
        backGroundPaint.style = Paint.Style.STROKE
        backGroundPaint.strokeWidth = bgStrokeWidth.toFloat()

        circleBGPaint.color = circleBg
        circleBGPaint.isAntiAlias = true
        circleBGPaint.style = Paint.Style.FILL

        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = if (squareCap) Paint.Cap.SQUARE else Paint.Cap.ROUND
        progressPaint.strokeWidth = progressStrokeWidth.toFloat()

        attributes?.recycle()
    }

    fun setTextFont(@FontRes font: Int) {
        textFont = ResourcesCompat.getFont(context, font)
        textPaint.typeface = textFont
        postInvalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val maxStroke = max(bgStrokeWidth, progressStrokeWidth)
        progressRect.set(
            circlePadding.toFloat() + paddingLeft + (maxStroke / 2)
            , circlePadding.toFloat() + paddingTop + (maxStroke / 2),
            width.toFloat().minus(circlePadding + paddingRight + (maxStroke / 2)),
            height.toFloat().minus(circlePadding + paddingBottom + (maxStroke / 2))
        )

        bgStrokeRect.set(
            progressRect
        )

        circleBgRect.set(
            progressRect.left + (bgStrokeWidth / 2),
            progressRect.top + (bgStrokeWidth / 2),
            width.toFloat().minus(circlePadding + paddingRight + (maxStroke / 2) + (bgStrokeWidth / 2)),
            height.toFloat().minus(circlePadding + paddingBottom + (maxStroke / 2) + (bgStrokeWidth / 2))
        )

        canvas?.drawOval(
            bgStrokeRect, backGroundPaint
        )

        canvas?.drawOval(
            circleBgRect, circleBGPaint
        )


        canvas?.drawArc(
            progressRect, startAngle, (progress / max) * (360 * progressDirection), false,
            progressPaint
        )

        canvas?.drawLine(circleBgRect.left,circleBgRect.top,circleBgRect.right,circleBgRect.top,backGroundPaint)
        canvas?.drawLine(circleBgRect.left,circleBgRect.top,circleBgRect.left,circleBgRect.bottom,backGroundPaint)
        canvas?.drawLine(circleBgRect.right,circleBgRect.top,circleBgRect.right,circleBgRect.bottom,backGroundPaint)
        canvas?.drawLine(circleBgRect.left,circleBgRect.bottom,circleBgRect.right,circleBgRect.bottom,backGroundPaint)

        if (showText) {
            textPaint.getTextBounds("0", 0, 1, textRect)

            canvas?.drawText(
                getText(),
                width/2f +(textRect.width() shr 1),
                height/2f +(textRect.height() shr 1),
                textPaint
            )
        }

    }

    private fun getText(): String {

        if (!text.isNullOrEmpty())
            return text!!
        val format: String =
            if (showPercentage) {
                "%s%%"
            } else
                "%s"
        return if (showDecimals) {
            String.format(format, progress.toString())
        } else {
            if (progress.minus(progress.toInt()) == 0f) {
                String.format(format, progress.toInt().toString())
            } else
                String.format(format, progress.toString())

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = 275
        val desiredHeight = 275

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int


        width = when (widthMode) {
            MeasureSpec.EXACTLY ->
                widthSize
            MeasureSpec.AT_MOST ->
                min(desiredWidth, widthSize)
            else ->
                desiredWidth
        }


        height = when (heightMode) {
            MeasureSpec.EXACTLY ->
                heightSize
            MeasureSpec.AT_MOST ->
                min(desiredHeight, heightSize)
            else ->
                desiredHeight
        }


        setMeasuredDimension(width, height)
    }

    fun setShowText(mShowText: Boolean) {
        showText = mShowText
        postInvalidate()
    }

    fun setProgress(mProgress: Float) {
        progress = mProgress
        if (mProgress > max) {
            progress = max.toFloat()
        }
        postInvalidate()
    }

    fun setBgColor(mBgColor: Int) {
        bgColor = mBgColor
        postInvalidate()
    }

    fun setBgStrokeWidth(mBgStrokeWidth: Int) {
        bgStrokeWidth = mBgStrokeWidth
        postInvalidate()
    }

    fun setProgressColor(mProgressColor: Int) {
        progressColor = mProgressColor
        postInvalidate()
    }

    fun setProgressStrokeWidth(mProgressStrokeWidth: Int) {
        progressStrokeWidth = mProgressStrokeWidth
        postInvalidate()
    }

    fun setTextColor(mTextColor: Int) {
        textColor = mTextColor
        postInvalidate()
    }

    fun setTextSize(mTextSize: Int) {
        textSize = mTextSize
        postInvalidate()
    }

    fun setMax(mMax: Int) {
        if (mMax > 0) {
            max = mMax
        }
        postInvalidate()
    }

    fun setSquareCap(mSquareCap: Boolean) {
        squareCap = mSquareCap
        progressPaint.strokeCap = if (squareCap) Paint.Cap.SQUARE else Paint.Cap.ROUND
        postInvalidate()
    }

}