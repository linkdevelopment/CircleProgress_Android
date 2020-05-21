package com.linkdev.circleprogress

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.linkdev.circleprogress.Utilities.sp2px
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
        const val TWO = 2
    }

    private var mProgressStrokeColor: Int = Color.BLACK

    private var mOuterStrokeColor: Int = Color.GRAY

    private var textColor: Int = Color.BLACK


    private var mOuterStrokeThickness: Int =
        ZERO

    private var mProgressStrokeThickness: Int =
        ZERO

    private var mTextSize: Float =
        ZERO.toFloat()

    private var mMax: Int =
        DEFAULT_MAX
    private var mProgress: Float = ZERO.toFloat()
    private var circlePadding: Int =
        ZERO

    private var calculatedDefaultCirclePadding: Float = ZERO.toFloat()
    private var calculatedDefaultStrokeWidth: Float = ZERO.toFloat()
    private var calculatedDefaultTextSize: Float = ZERO.toFloat()
    private var mStartAngle: Int = ZERO
    private var mProgressDirection: Int = ProgressDirection.CLOCKWISE.value
    private var mProgressRoundedEnd: Boolean = false
    private var mShowDecimal: Boolean = false
    private var mInnerCircleBackground: Int = Color.TRANSPARENT
    private var textFont: Typeface? = Typeface.DEFAULT
    private var mTextDisplay: Int = TextDisplay.PROGRESS_PERCENTAGE.value

    private var mText: String? = ""

    private val textRect by lazy { RectF() }
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

        initPainters()

        attributes?.recycle()
    }

    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    private fun initPainters() {
        initTextPaint()

        initBackGroundPaint()

        initCircleBackGroundPaint()

        initProgressPaint()
    }

    private fun initAttributes(attributes: TypedArray?) {
        attributes?.let {
            mTextDisplay = it.getInt(
                R.styleable.CircularProgress_textDisplay,
                TextDisplay.PROGRESS_PERCENTAGE.value
            )
            mStartAngle = it.getInt(
                R.styleable.CircularProgress_startAngle,
                StartAngle.TOP.value
            )
            mProgressDirection =
                it.getInt(
                    R.styleable.CircularProgress_progressDirection,
                    ProgressDirection.CLOCKWISE.value
                )
            setMaxValue(
                it.getInteger(
                    R.styleable.CircularProgress_max,
                    DEFAULT_MAX
                )
            )
            setProgressValue(it.getFloat(R.styleable.CircularProgress_progress, ZERO.toFloat()))
            mOuterStrokeColor = it.getInt(
                R.styleable.CircularProgress_outerStrokeColor, ResourcesCompat.getColor(
                    resources, R.color.gray, null
                )
            )
            mOuterStrokeThickness =
                it.getDimensionPixelSize(
                    R.styleable.CircularProgress_outerStrokeThickness,
                    calculatedDefaultStrokeWidth.toInt()
                )
            mProgressStrokeColor =
                it.getColor(
                    R.styleable.CircularProgress_progressStrokeColor, ResourcesCompat.getColor(
                        resources, R.color.black, null
                    )
                )
            mProgressStrokeThickness = it.getDimensionPixelSize(
                R.styleable.CircularProgress_progressStrokeThickness,
                calculatedDefaultStrokeWidth.toInt()
            )
            textColor = it.getColor(
                R.styleable.CircularProgress_textColor, ResourcesCompat.getColor(
                    resources, R.color.black, null
                )
            )
            mTextSize = it.getDimension(
                R.styleable.CircularProgress_textSize,
                calculatedDefaultTextSize
            )

            mProgressRoundedEnd =
                it.getBoolean(R.styleable.CircularProgress_progressRoundedEnd, false)

            mShowDecimal = it.getBoolean(R.styleable.CircularProgress_showDecimalZero, false)

            mText = it.getString(R.styleable.CircularProgress_text)
            if (!mText.isNullOrEmpty())
                mTextDisplay = TextDisplay.PROVIDED_TEXT.value
            mInnerCircleBackground = it.getColor(
                R.styleable.CircularProgress_innerCircleBackground, ResourcesCompat.getColor(
                    resources, R.color.transparent, null
                )
            )
        }
    }

    private fun initDefaultValues() {
        calculatedDefaultCirclePadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_CIRCLE_PADDING,
            resources.displayMetrics
        )

        calculatedDefaultStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_STROKE_THICKNESS,
            resources.displayMetrics
        )

        calculatedDefaultTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE,
            resources.displayMetrics
        )
        circlePadding =
            calculatedDefaultCirclePadding
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
        progressPaint.strokeCap = if (!mProgressRoundedEnd) Paint.Cap.SQUARE else Paint.Cap.ROUND
        progressPaint.strokeWidth = mProgressStrokeThickness.toFloat()
    }

    private fun initBackGroundPaint() {
        backGroundPaint.color = mOuterStrokeColor
        backGroundPaint.isAntiAlias = true
        backGroundPaint.style = Paint.Style.STROKE
        backGroundPaint.strokeWidth = mOuterStrokeThickness.toFloat()
    }

    private fun initTextPaint() {
        textPaint.textSize = mTextSize
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
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

        val width: Int = textRect.width().toInt()

        val txt =
            TextUtils.ellipsize(getText(), textPaint, width.toFloat(), TextUtils.TruncateAt.END)
        canvas.drawText(txt, ZERO, txt.length, xPos, yPos, textPaint)

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
        (mProgress / mMax) * (FULL_CIRCLE_DEGREES * mProgressDirection.toFloat())

    private fun setCircleBgRect(maxStroke: Int) {
        circleBgRect.set(
            progressRect.left + (mOuterStrokeThickness / TWO),
            progressRect.top + (mOuterStrokeThickness / TWO),
            width.toFloat().minus(circlePadding + paddingRight + (maxStroke / TWO) + (mOuterStrokeThickness / TWO)),
            height.toFloat().minus(circlePadding + paddingBottom + (maxStroke / TWO) + (mOuterStrokeThickness / TWO))
        )
        textRect.set(circleBgRect)
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
                if (!mText.isNullOrEmpty())
                    mText!!
                else
                    ""
            }
            TextDisplay.PROGRESS_VALUE.value -> {
                if (mShowDecimal) {
                    String.format(NON_PERCENTAGE_VALUE, mProgress.toString())
                } else {
                    if (mProgress.minus(mProgress.toInt()) == ZERO.toFloat()) {
                        String.format(
                            NON_PERCENTAGE_VALUE,
                            mProgress.toInt().toString()
                        )
                    } else {
                        String.format(
                            NON_PERCENTAGE_VALUE,
                            mProgress.toString()
                        )
                    }
                }
            }
            TextDisplay.PROGRESS_PERCENTAGE.value -> {
                if (mShowDecimal)
                    String.format(
                        PERCENTAGE_VALUE,
                        ((mProgress.div(mMax)) * ONE_HUNDRED).toString()
                    )
                else {
                    if (mProgress.minus(mProgress.toInt()) == ZERO.toFloat()) {
                        String.format(
                            PERCENTAGE_VALUE,
                            ((mProgress.div(mMax)) * ONE_HUNDRED).toInt().toString()
                        )
                    } else {
                        String.format(
                            PERCENTAGE_VALUE,
                            ((mProgress.div(mMax)) * ONE_HUNDRED).toString()
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

    private fun setProgressValue(progress: Float?) {
        if (progress != null)
            mProgress = progress
        else
            mProgress = 0f
        if (mProgress > mMax) {
            mProgress = mMax.toFloat()
        }
    }

    fun setProgress(progress: Float?) {
        setProgressValue(progress)
        postInvalidate()
    }

    fun setOuterStrokeColor(outerStrokeColor: Int) {
        mOuterStrokeColor = outerStrokeColor
        postInvalidate()
    }

    fun setOuterStrokeThickness(outerStrokeThickness: Int) {
        mOuterStrokeThickness =
            Utilities.dpToPx(context.resources, outerStrokeThickness.toFloat()).toInt()
        postInvalidate()
    }

    fun setProgressStrokeColor(progressStrokesColor: Int) {
        mProgressStrokeColor = progressStrokesColor
        postInvalidate()
    }

    fun setProgressStrokeThickness(progressStrokeThickness: Int) {
        mProgressStrokeThickness =
            Utilities.dpToPx(context.resources, progressStrokeThickness.toFloat()).toInt()
        postInvalidate()
    }

    fun setTextColor(mTextColor: Int) {
        textColor = mTextColor
        postInvalidate()
    }

    fun setTextSize(textSize: Float) {
        mTextSize = sp2px(context.resources, textSize)
        postInvalidate()
    }

    private fun setMaxValue(max: Int?) {
        if (max != null) {
            if (max > ZERO) {
                mMax = max
            }
        } else
            mMax = 0
        setProgressValue(mProgress)
    }

    fun setMax(max: Int?) {
        setMaxValue(max)
        postInvalidate()
    }

    fun setShowDecimalZero(showDecimal: Boolean) {
        mShowDecimal = showDecimal
        postInvalidate()
    }

    fun setProgressRoundedEnd(progressRoundedEnd: Boolean) {
        mProgressRoundedEnd = progressRoundedEnd
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

    fun setText(text: CharSequence?) {
        mText = if (!text.isNullOrEmpty()) {
            mTextDisplay = TextDisplay.PROVIDED_TEXT.value
            text.toString()
        } else
            ""
        postInvalidate()
    }

    fun getMax(): Int {
        return mMax
    }

    fun getProgress(): Float {
        return mProgress
    }
}

enum class TextDisplay(val value: Int) {
    NO_TEXT(0), PROVIDED_TEXT(1), PROGRESS_VALUE(2), PROGRESS_PERCENTAGE(3)
}

enum class ProgressDirection(val value: Int) {
    CLOCKWISE(1), ANTICLOCKWISE(-1)
}

enum class StartAngle(val value: Int) {
    RIGHT(0), BOTTOM(90), LEFT(180), TOP(270)
}