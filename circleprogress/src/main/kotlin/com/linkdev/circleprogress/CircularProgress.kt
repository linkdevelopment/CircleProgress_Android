/**
Copyright 2020 Link Development

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/
package com.linkdev.circleprogress

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.linkdev.circleprogress.Utilities.spToPx
import java.util.*
import kotlin.math.max
import kotlin.math.min


class CircularProgress(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    companion object {
        const val DEFAULT_TEXT_SIZE = 10f
        const val DEFAULT_STROKE_WIDTH = 4f
        const val DEFAULT_CIRCLE_PADDING = 5f
        const val DEFAULT_MAX = 100
        const val PERCENTAGE_VALUE = "%s%%"
        const val NON_PERCENTAGE_VALUE = "%s"
        const val DEFAULT_DIMENS = 275
        const val FULL_CIRCLE_DEGREES = 360
    }

    private var animating: Boolean = false
    private var timer = Timer()
    private var mAnimationProgress: Float = 0f
    private var mAnimationMaxProgress: Float = 0f
    private var mAnimationDelayMillis: Long = 0
    private var mProgressStrokeColor: Int = Color.BLACK

    private var mOuterStrokeColor: Int = Color.GRAY

    private var textColor: Int = Color.BLACK


    private var mOuterStrokeWidth: Int = 0

    private var mProgressStrokeWidth: Int = 0

    private var mTextSize: Float = 0f

    private var mMax: Int = DEFAULT_MAX

    private var mProgress: Float = 0f

    private var circlePadding: Int = 0

    private var calculatedDefaultCirclePadding: Float = 0f
    private var calculatedDefaultStrokeWidth: Float = 0f
    private var calculatedDefaultTextSize: Float = 0f
    private var mStartAngle: Int = 0
    private var mProgressDirection: Int = ProgressDirection.CLOCKWISE.value
    private var mProgressRoundedCorners: Boolean = false
    private var mInnerCircleBackground: Int = Color.TRANSPARENT
    private var textFont: Typeface? = Typeface.DEFAULT
    private var mTextDisplay: Int = TextDisplay.PROGRESS_PERCENTAGE.value

    private var mText: String? = ""

    private val textRect by lazy { RectF() }
    private val progressRect by lazy { RectF() }
    private val circleBgRect by lazy { RectF() }
    private val bgStrokeRect by lazy { RectF() }

    private val textPaint = TextPaint()
    private val outerStrokePaint = Paint()
    private val progressPaint = Paint()
    private val circleBGPaint = Paint()

    init {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.CircularProgress)

        initDefaultValues()

        initAttributes(attributes)

        initPainters()

        attributes?.recycle()
    }

    /**
     * called when the circle will be redrawn so we must init painters
     */
    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    /**
     * Initialize all paints objects
     */
    private fun initPainters() {
        initTextPaint()

        initOuterStrokePaint()

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
            setProgressValue(it.getFloat(R.styleable.CircularProgress_progress, 0f))
            mOuterStrokeColor = it.getInt(
                R.styleable.CircularProgress_outerStrokeColor, ResourcesCompat.getColor(
                    resources, R.color.gray, null
                )
            )
            mOuterStrokeWidth =
                it.getDimensionPixelSize(
                    R.styleable.CircularProgress_outerStrokeWidth,
                    calculatedDefaultStrokeWidth.toInt()
                )
            mProgressStrokeColor =
                it.getColor(
                    R.styleable.CircularProgress_progressStrokeColor, ResourcesCompat.getColor(
                        resources, R.color.black, null
                    )
                )
            mProgressStrokeWidth = it.getDimensionPixelSize(
                R.styleable.CircularProgress_progressStrokeWidth,
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

            mProgressRoundedCorners =
                it.getBoolean(R.styleable.CircularProgress_roundedCorners, false)

            mText = it.getString(R.styleable.CircularProgress_text)
            if (!mText.isNullOrEmpty())// if user provide text from the xml and automatically set the text display to PROVIDED_TEXT
                mTextDisplay = TextDisplay.PROVIDED_TEXT.value
            mInnerCircleBackground = it.getColor(
                R.styleable.CircularProgress_innerCircleBackground, ResourcesCompat.getColor(
                    resources, R.color.transparent, null
                )
            )
        }
    }

    /**
     * initialize the default values of the view (default padding - default text size - default stroke width )
     */
    private fun initDefaultValues() {
        calculatedDefaultCirclePadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_CIRCLE_PADDING,
            resources.displayMetrics
        )

        calculatedDefaultStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_STROKE_WIDTH,
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

    /**
     * set values of the circle background paint
     */
    private fun initCircleBackGroundPaint() {
        circleBGPaint.color = mInnerCircleBackground
        circleBGPaint.style = Paint.Style.FILL
    }

    /**
     * set values of the progress paint
     */
    private fun initProgressPaint() {
        progressPaint.color = mProgressStrokeColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap =
            if (!mProgressRoundedCorners) Paint.Cap.SQUARE else Paint.Cap.ROUND
        progressPaint.strokeWidth = mProgressStrokeWidth.toFloat()
    }

    /**
     * set values of the background paint
     */
    private fun initOuterStrokePaint() {
        outerStrokePaint.color = mOuterStrokeColor
        outerStrokePaint.isAntiAlias = true
        outerStrokePaint.style = Paint.Style.STROKE
        outerStrokePaint.strokeWidth = mOuterStrokeWidth.toFloat()
    }

    /**
     * set values of the text paint
     */
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

    /**
     * draw the circle,progress,background and text
     * we must get the max value between 2 strokes (progress and outer line) which will be used in draw circles
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val maxStroke = max(mOuterStrokeWidth, mProgressStrokeWidth)
        setRects(maxStroke)
        drawRects(canvas)
    }

    private fun drawRects(canvas: Canvas?) {
        drawOuterStroke(canvas)
        drawCircleBackGround(canvas)
        drawProgressArc(canvas)
        drawText(canvas)
    }

    /**
     * Sets the bounds to the different rectangles needed for the progress bar to draw
     */
    private fun setRects(maxStroke: Int) {
        setProgressRect(maxStroke)
        setStrokeRect()
        setCircleBgRect(maxStroke)
        setTextRect(maxStroke)
    }

    /**
     * Draws text inside the progress bar, if the text is larger than the bounds of the circle progress, the text will be truncated
     */
    private fun drawText(canvas: Canvas?) {

        val xPos: Float = (canvas!!.width / 2).toFloat()
        val yPos: Float =
            canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2

        val width: Int = textRect.width().toInt()
        val txt = TextUtils.ellipsize(getText(), textPaint, width.toFloat(), TextUtils.TruncateAt.END)
        canvas.drawText(txt, 0, txt.length, xPos, yPos, textPaint)

    }

    /**
     * Draws the progress outer circle
     */
    private fun drawOuterStroke(canvas: Canvas?) {
        canvas?.drawOval(
            bgStrokeRect, outerStrokePaint
        )
    }

    /**
     * Draws the progress circle background
     */
    private fun drawCircleBackGround(canvas: Canvas?) {
        canvas?.drawOval(
            circleBgRect, circleBGPaint
        )
    }

    /**
     * Draws the progress arc
     */
    private fun drawProgressArc(canvas: Canvas?) {
        canvas?.drawArc(
            progressRect, mStartAngle.toFloat(), getSweepAngle(), false,
            progressPaint
        )
    }

    /**
     * Used to retrieved the angle used to draw the arc of the progress
     */
    private fun getSweepAngle(): Float =
        (mProgress / mMax) * (FULL_CIRCLE_DEGREES * mProgressDirection.toFloat())


    /**
     * set rect for draw inner circle background which its border must be calculated by the max stroke between progress and the outer stroke
     */
    private fun setCircleBgRect(maxStroke: Int) {
        circleBgRect.set(
            progressRect.left + (mOuterStrokeWidth / 2),
            progressRect.top + (mOuterStrokeWidth / 2),
            width.toFloat()
                .minus(circlePadding + paddingRight + (maxStroke / 2) + (mOuterStrokeWidth / 2)),
            height.toFloat()
                .minus(circlePadding + paddingBottom + (maxStroke / 2) + (mOuterStrokeWidth / 2))
        )
    }

    /**
     * Sets the text bounds.
     * Text bounds should be smaller than the progress circle by circle padding to avoid overlapping between the text and the progress borders.
     */
    private fun setTextRect(maxStroke: Int) {
        textRect.set(
            circleBgRect.left + circlePadding.toFloat(),
            circleBgRect.top,
            width.toFloat()
                .minus((2 * circlePadding) + paddingRight + (maxStroke / 2) + (mOuterStrokeWidth / 2)),
            circleBgRect.bottom
        )
    }

    /**
     * set the bounds of the rect which the background stroke (unfinished part) will be drown in it which should be the same as progress rect
     */
    private fun setStrokeRect() {
        bgStrokeRect.set(
            progressRect
        )
    }

    /**
     * set the bounds of the rect which the progress will be drown in it
     */
    private fun setProgressRect(maxStroke: Int) {
        progressRect.set(
            circlePadding.toFloat() + paddingLeft + (maxStroke / 2)
            , circlePadding.toFloat() + paddingTop + (maxStroke / 2),
            width.toFloat().minus(circlePadding + paddingRight + (maxStroke / 2)),
            height.toFloat().minus(circlePadding + paddingBottom + (maxStroke / 2))
        )
    }

    /**
     * return the text which will displayed by check on the textdisplay type
     */
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
                if (mProgress.minus(mProgress.toInt()) == 0f) {
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
            TextDisplay.PROGRESS_PERCENTAGE.value -> {
                if (mProgress.minus(mProgress.toInt()) == 0f) {
                    String.format(
                        PERCENTAGE_VALUE,
                        ((mProgress.div(mMax)) * 100).toInt().toString()
                    )
                } else {
                    String.format(
                        PERCENTAGE_VALUE,
                        ((mProgress.div(mMax)) * 100f).toString()
                    )
                }
            }
            else -> {
                return ""
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth =
            DEFAULT_DIMENS
        val desiredHeight =
            DEFAULT_DIMENS

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

    /***
     * the method make sure that the provided progress is not null and not out of the bounds
     */
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
        setMaxValue(mMax)
        postInvalidate()
    }

    /**
     * fill the circle progress with the provided value but in incremental way by increase progress value by 1 each
     * provided delayMillis until reach the final value
     */
    fun startProgressAnimation(maxProgress: Float, delayMillis: Long) {
        mAnimationMaxProgress = maxProgress
        mAnimationDelayMillis = delayMillis
        if (!animating) {
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (mAnimationProgress in 0.0..mAnimationMaxProgress.toDouble()) {
                        setProgress(mAnimationProgress)
                        mAnimationProgress++
                        animating = true
                    } else {
                        animating = false
                        mAnimationProgress = 0f
                        timer.cancel()
                    }
                }
            }, 0, delayMillis)
        }
    }

    fun pauseProgressAnimation() {
        if (animating) {
            timer.cancel()
            animating = false
        }
    }

    fun stopProgressAnimation() {
        timer.cancel()
        animating = false
        mAnimationProgress = 0f
        setProgress(mAnimationProgress)
    }

    fun setOuterStrokeColor(outerStrokeColor: Int) {
        mOuterStrokeColor = outerStrokeColor
        postInvalidate()
    }

    fun setOuterStrokeWidth(outerStrokeWidth: Int) {
        mOuterStrokeWidth =
            Utilities.dpToPx(context.resources, outerStrokeWidth.toFloat()).toInt()
        postInvalidate()
    }

    fun setProgressStrokeColor(progressStrokesColor: Int) {
        mProgressStrokeColor = progressStrokesColor
        postInvalidate()
    }

    fun setProgressStrokeWidth(progressStrokeWidth: Int) {
        mProgressStrokeWidth =
            Utilities.dpToPx(context.resources, progressStrokeWidth.toFloat()).toInt()
        postInvalidate()
    }

    fun setTextColor(mTextColor: Int) {
        textColor = mTextColor
        postInvalidate()
    }

    fun setTextSize(textSize: Float) {
        mTextSize = spToPx(context.resources, textSize)
        postInvalidate()
    }

    /***
     * the method make sure that the provided max is not null and greater than zero and then check on the previous progress to make sure that
     * new max not less than it
     */
    private fun setMaxValue(max: Int?) {
        if (max != null) {
            if (max > 0) {
                mMax = max
            }
        } else
            mMax = 0
    }

    fun setMax(max: Int?) {
        setMaxValue(max)
        setProgressValue(mProgress)
        postInvalidate()
    }


    fun setProgressRoundedCorners(progressRoundedCorners: Boolean) {
        mProgressRoundedCorners = progressRoundedCorners
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

    /**
     * set text to the circle progress and automatically set the text display to PROVIDED_TEXT
     */
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
