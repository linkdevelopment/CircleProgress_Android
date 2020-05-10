package com.linkdev.circleprogress

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import kotlin.math.max
import kotlin.math.min


class CircularProgress(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    companion object {
        const val DEFAULT_TEXT_SIZE = 10f
        const val DEFAULT_STROKE_THICKNESS = 4f
        const val DEFAULT_CIRCLE_PADDING = 5f
        const val DEFAULT_MAX = 100
        const val PERCENTAGE_VALUE = "%s%%"
        const val NON_PERCENTAGE_VALUE = "%s"
        const val ONE_HUNDRED = 100
        const val DEFAULT_DESIRED_SIZE = 275
        const val FULL_CIRCLE_DEGREES = 360
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
    }

    private var mProgressStrokeColor: Int = Color.BLACK

    private var mOuterStrokeColor: Int = Color.GRAY

    private var textColor: Int = Color.BLACK

    @DimenRes
    private var mOuterStrokeThickness: Int =
        ZERO
    @DimenRes
    private var mProgressStrokeThickness: Int =
        ZERO
    @DimenRes
    private var textSize: Int =
        ZERO

    private var max: Int =
        DEFAULT_MAX
    private var progress: Float = ZERO.toFloat()
    private var circlePadding: Int =
        ZERO

    private var defaultCirclePadding: Float = ZERO.toFloat()
    private var defaultStrokeWidth: Float = ZERO.toFloat()
    private var defaultTextSize: Float = ZERO.toFloat()
    private var mStartAngle: Int = ZERO
    private var mProgressDirection: Int = ONE
    private var mProgressRounded: Boolean = false
    private var showDecimals: Boolean = false
    private var mInnerCircleBackground: Int = Color.TRANSPARENT
    private var textFont: Typeface? = Typeface.DEFAULT
    private var mTextDisplay: Int = ZERO

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

        initDefaultValues()

        initAttributes(attributes)

        initTextPaint()

        initBackGroundPaint()

        initCircleBackGroundPaint()

        initProgressPaint()

        attributes?.recycle()
    }

    private fun initAttributes(attributes: TypedArray?) {
        attributes?.let {
            mTextDisplay = it.getInt(R.styleable.CircularProgress_textDisplay, ZERO)
            mStartAngle = it.getInt(
                R.styleable.CircularProgress_startAngle,
                ZERO
            )
            mProgressDirection =
                it.getInt(
                    R.styleable.CircularProgress_progressDirection,
                    ONE
                )
            setMax(
                it.getInteger(
                    R.styleable.CircularProgress_max,
                    DEFAULT_MAX
                )
            )
            setProgress(it.getFloat(R.styleable.CircularProgress_progress, ZERO.toFloat()))
            mOuterStrokeColor = it.getInt(
                R.styleable.CircularProgress_outerStrokeColor, ResourcesCompat.getColor(
                    resources, R.color.gray, null
                )
            )
            mOuterStrokeThickness =
                it.getDimensionPixelSize(
                    R.styleable.CircularProgress_outerStrokeThickness,
                    defaultStrokeWidth.toInt()
                )
            mProgressStrokeColor =
                it.getColor(
                    R.styleable.CircularProgress_progressStrokeColor, ResourcesCompat.getColor(
                        resources, R.color.black, null
                    )
                )
            mProgressStrokeThickness = it.getDimensionPixelSize(
                R.styleable.CircularProgress_progressStrokeThickness,
                defaultStrokeWidth.toInt()
            )
            textColor = it.getColor(
                R.styleable.CircularProgress_textColor, ResourcesCompat.getColor(
                    resources, R.color.black, null
                )
            )
            textSize = it.getDimensionPixelSize(
                R.styleable.CircularProgress_textSize,
                defaultTextSize.toInt()
            )

            mProgressRounded = it.getBoolean(R.styleable.CircularProgress_progressRounded, false)

            showDecimals = it.getBoolean(R.styleable.CircularProgress_showDecimals, false)

            text = it.getString(R.styleable.CircularProgress_text)
            mInnerCircleBackground = it.getColor(
                R.styleable.CircularProgress_innerCircleBackground, ResourcesCompat.getColor(
                    resources, R.color.transparent, null
                )
            )
        }
    }

    private fun initDefaultValues() {
        defaultCirclePadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_CIRCLE_PADDING,
            resources.displayMetrics
        )

        defaultStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_STROKE_THICKNESS,
            resources.displayMetrics
        )

        defaultTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE,
            resources.displayMetrics
        )
        circlePadding =
            defaultCirclePadding
                .toInt()
    }

    private fun initCircleBackGroundPaint() {
        circleBGPaint.color = mInnerCircleBackground
        circleBGPaint.isAntiAlias = true
        circleBGPaint.style = Paint.Style.FILL
    }

    private fun initProgressPaint() {
        progressPaint.color = mProgressStrokeColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = if (!mProgressRounded) Paint.Cap.SQUARE else Paint.Cap.ROUND
        progressPaint.strokeWidth = mProgressStrokeThickness.toFloat()
    }

    private fun initBackGroundPaint() {
        backGroundPaint.color = mOuterStrokeColor
        backGroundPaint.isAntiAlias = true
        backGroundPaint.style = Paint.Style.STROKE
        backGroundPaint.strokeWidth = mOuterStrokeThickness.toFloat()
    }

    private fun initTextPaint() {
        textPaint.typeface = textFont
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()
    }

    fun setTextFont(@FontRes font: Int) {
        textFont = ResourcesCompat.getFont(context, font)
        textPaint.typeface = textFont
        postInvalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val maxStroke = max(mOuterStrokeThickness, mProgressStrokeThickness)
        setRects(maxStroke)
        drawRects(canvas)
    }

    private fun drawRects(canvas: Canvas?) {
        drawStroke(canvas)
        drawCircleBackGround(canvas)
        drawProgressArc(canvas)
        drawText(canvas)
    }

    private fun setRects(maxStroke: Int) {
        setProgressRect(maxStroke)
        setStrokeRect()
        setCircleBgRect(maxStroke)
    }

    private fun drawText(canvas: Canvas?) {
        val xPos: Float = (canvas!!.width / TWO).toFloat()
        val yPos: Float =
            canvas.height / TWO - (textPaint.descent() + textPaint.ascent()) / TWO
        canvas.drawText(
            getText(),
            xPos,
            yPos,
            textPaint
        )
    }

    private fun drawStroke(canvas: Canvas?) {
        canvas?.drawOval(
            bgStrokeRect, backGroundPaint
        )
    }

    private fun drawCircleBackGround(canvas: Canvas?) {
        canvas?.drawOval(
            circleBgRect, circleBGPaint
        )
    }

    private fun drawProgressArc(canvas: Canvas?) {
        canvas?.drawArc(
            progressRect, mStartAngle.toFloat(), getSweepAngle(), false,
            progressPaint
        )
    }

    private fun getSweepAngle(): Float =
        (progress / max) * (FULL_CIRCLE_DEGREES * mProgressDirection.toFloat())

    private fun setCircleBgRect(maxStroke: Int) {
        circleBgRect.set(
            progressRect.left + (mOuterStrokeThickness / TWO),
            progressRect.top + (mOuterStrokeThickness / TWO),
            width.toFloat().minus(circlePadding + paddingRight + (maxStroke / TWO) + (mOuterStrokeThickness / TWO)),
            height.toFloat().minus(circlePadding + paddingBottom + (maxStroke / TWO) + (mOuterStrokeThickness / TWO))
        )
    }

    private fun setStrokeRect() {
        bgStrokeRect.set(
            progressRect
        )
    }

    private fun setProgressRect(maxStroke: Int) {
        progressRect.set(
            circlePadding.toFloat() + paddingLeft + (maxStroke / TWO)
            , circlePadding.toFloat() + paddingTop + (maxStroke / TWO),
            width.toFloat().minus(circlePadding + paddingRight + (maxStroke / TWO)),
            height.toFloat().minus(circlePadding + paddingBottom + (maxStroke / TWO))
        )
    }


    private fun getText(): String {
        return when (mTextDisplay) {
            TextDisplay.NO_TEXT.value -> {
                ""
            }
            TextDisplay.PROVIDED_TEXT.value -> {
                if (!text.isNullOrEmpty())
                    text!!
                else
                    ""
            }
            TextDisplay.PROGRESS_VALUE.value -> {
                if (showDecimals) {
                    String.format(NON_PERCENTAGE_VALUE, progress.toString())
                } else {
                    if (progress.minus(progress.toInt()) == ZERO.toFloat()) {
                        String.format(
                            NON_PERCENTAGE_VALUE,
                            progress.toInt().toString()
                        )
                    } else {
                        String.format(
                            NON_PERCENTAGE_VALUE,
                            progress.toString()
                        )
                    }
                }
            }
            TextDisplay.PROGRESS_PERCENTAGE.value -> {
                if (showDecimals)
                    String.format(PERCENTAGE_VALUE, ((progress.div(max)) * ONE_HUNDRED).toString())
                else {
                    if (progress.minus(progress.toInt()) == ZERO.toFloat()) {
                        String.format(
                            PERCENTAGE_VALUE,
                            ((progress.div(max)) * ONE_HUNDRED).toInt().toString()
                        )
                    } else {
                        String.format(
                            PERCENTAGE_VALUE,
                            ((progress.div(max)) * ONE_HUNDRED).toString()
                        )
                    }
                }
            }
            else -> {
                return ""
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth =
            DEFAULT_DESIRED_SIZE
        val desiredHeight =
            DEFAULT_DESIRED_SIZE

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

    fun setProgress(mProgress: Float) {
        progress = mProgress
        if (mProgress > max) {
            progress = max.toFloat()
        }
        postInvalidate()
    }

    fun setOuterStrokeColor(outerStrokeColor: Int) {
        mOuterStrokeColor = outerStrokeColor
        postInvalidate()
    }

    fun setOuterStrokeThickness(outerStrokeThickness: Int) {
        mOuterStrokeThickness = outerStrokeThickness
        postInvalidate()
    }

    fun setProgressStrokesColor(progressStrokesColor: Int) {
        mProgressStrokeColor = progressStrokesColor
        postInvalidate()
    }

    fun setProgressStrokeThickness(progressStrokeThickness: Int) {
        mProgressStrokeThickness = progressStrokeThickness
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
        if (mMax > ZERO) {
            max = mMax
        }
        postInvalidate()
    }

    fun setProgressRounded(progressRounded: Boolean) {
        mProgressRounded = progressRounded
        progressPaint.strokeCap = if (!mProgressRounded) Paint.Cap.SQUARE else Paint.Cap.ROUND
        postInvalidate()
    }

    fun setInnerCircleBackground(innerCircleBackground: Int) {
        mInnerCircleBackground = innerCircleBackground
        postInvalidate()
    }

    fun setTextDisplay(textDisplay: TextDisplay) {
        mTextDisplay = textDisplay.value
        postInvalidate()
    }

    fun setProgressDirection(progressDirection: ProgressDirection) {
        mProgressDirection = progressDirection.value
        postInvalidate()
    }

    fun setStartAngle(startAngle: StartAngle) {
        mStartAngle = startAngle.value
        postInvalidate()
    }
}

enum class TextDisplay(val value: Int) {
    NO_TEXT(0), PROVIDED_TEXT(1), PROGRESS_VALUE(2), PROGRESS_PERCENTAGE(3)
}

enum class ProgressDirection(val value: Int) {
    WITH_CLOCK_WISE(1), ANTI_CLOCK_WISE(-1)
}

enum class StartAngle(val value: Int) {
    RIGHT(0), BOTTOM(90), LEFT(180), TOP(270)
}