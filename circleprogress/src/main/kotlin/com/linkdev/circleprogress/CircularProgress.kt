/**
Copyright (C) 2020 Link Development

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
        const val DECIMAL_FORMAT = "%.2f"
        const val DEFAULT_DIMENS = 275
        const val FULL_CIRCLE_DEGREES = 360
    }

    private var animating: Boolean = false
    private var timer = Timer()
    private var mAnimationTempProgress: Float = 0f
    private var mAnimationProgress: Float = 0f
    private var mAnimationSpeed: Long = 0
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
     * called when the circle will be redrawn so we must initialize painters
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

    /**
     * initialize the attributes
     */
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
                    resources, R.color.circleProgressGray, null
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
                        resources, R.color.circleProgressBlack, null
                    )
                )
            mProgressStrokeWidth = it.getDimensionPixelSize(
                R.styleable.CircularProgress_progressStrokeWidth,
                calculatedDefaultStrokeWidth.toInt()
            )
            textColor = it.getColor(
                R.styleable.CircularProgress_textColor, ResourcesCompat.getColor(
                    resources, R.color.circleProgressBlack, null
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
                    resources, R.color.circleProgressTransparent, null
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
     * Sets values of the circle background paint
     */
    private fun initCircleBackGroundPaint() {
        circleBGPaint.color = mInnerCircleBackground
        circleBGPaint.style = Paint.Style.FILL
    }

    /**
     * Sets values of the progress paint
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
     * Sets values of the background paint
     */
    private fun initOuterStrokePaint() {
        outerStrokePaint.color = mOuterStrokeColor
        outerStrokePaint.isAntiAlias = true
        outerStrokePaint.style = Paint.Style.STROKE
        outerStrokePaint.strokeWidth = mOuterStrokeWidth.toFloat()
    }

    /**
     * Sets values of the text paint
     */
    private fun initTextPaint() {
        textPaint.textSize = mTextSize
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
    }

    /**
     * Sets custom font family to the circle progress by using the sent font resource id
     */
    fun setTextFont(@FontRes font: Int) {
        textFont = ResourcesCompat.getFont(context, font)
        textPaint.typeface = textFont
        postInvalidate()
    }

    /**
     * draws the circle,progress,background and text
     * calculate the max value between the 2 strokes (progress and outer line) which will be used in draw circles
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val maxStroke = max(mOuterStrokeWidth, mProgressStrokeWidth)
        setRects(maxStroke)
        drawComponents(canvas)
    }

    /**
     * draw the 4 components of the circle progress view
     * the outer stroke ,the circle background,the progress arc & text
     */
    private fun drawComponents(canvas: Canvas?) {
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
        val txt =
            TextUtils.ellipsize(getText(), textPaint, width.toFloat(), TextUtils.TruncateAt.END)
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
     * Sets the progress inner circle bounds, using max stroke between progress and the outer stroke
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
     * sets the bounds of the rectangle of the background stroke (unfinished part) which should be the same as progress rectangle
     */
    private fun setStrokeRect() {
        bgStrokeRect.set(
            progressRect
        )
    }

    /**
     * Sets the bounds of the progress rectangle
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
     * retrieve the text to be displayed based on the selected textdisplay type
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
                        DECIMAL_FORMAT.format(mProgress)
                    )
                }
            }
            TextDisplay.PROGRESS_PERCENTAGE.value -> {
                val progressPercentage: Float = ((mProgress.div(mMax)) * 100)
                if (progressPercentage.minus(progressPercentage.toInt()) == 0f) {
                    String.format(
                        PERCENTAGE_VALUE,
                        progressPercentage.toInt().toString()
                    )
                } else {
                    String.format(
                        PERCENTAGE_VALUE,
                        DECIMAL_FORMAT.format(progressPercentage)
                    )
                }
            }
            else -> {
                return ""
            }
        }
    }

    /**
     * Measure the view and its content to determine the measured width and the
     * measured height.
     * If user provide width or height it will be considered
     * If the width or height is set as Wrap_Content the DEFAULT_DIMENS value will be considered
     */
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

    /**
     * Sets the progress value
     * by making sure that the provided progress is not null and not out of the bounds
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

    /**
     * Sets the progress value
     */
    fun setProgress(progress: Float?) {
        setProgressValue(progress)
        postInvalidate()
    }

    /**
     * Uses animation to set the progress value
     * @param progress is the progress value for the circle
     * @param animationSpeed is the time in milli seconds between each increment in the progress
     */
    fun startProgressAnimation(progress: Float, animationSpeed: Long) {
        mAnimationProgress = progress
        if (animationSpeed > 0)
            mAnimationSpeed = animationSpeed
        else
            mAnimationSpeed = 1L
        if (!animating) {
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (mAnimationTempProgress in 0.0..mAnimationProgress.toDouble()) {
                        setProgress(mAnimationTempProgress)
                        mAnimationTempProgress++
                        animating = true
                    } else {
                        animating = false
                        mAnimationTempProgress = 0f
                        timer.cancel()
                    }
                }
            }, 0, mAnimationSpeed)
        }
    }

    /**
     * This method is used to pause the animation
     */
    fun pauseProgressAnimation() {
        if (animating) {
            timer.cancel()
            animating = false
        }
    }

    /**
     * This method is used to stop the animation
     */
    fun stopProgressAnimation() {
        timer.cancel()
        animating = false
        mAnimationTempProgress = 0f
        setProgress(mAnimationTempProgress)
    }

    /**
     * Sets the color of the outer stroke
     */
    fun setOuterStrokeColor(outerStrokeColor: Int) {
        mOuterStrokeColor = outerStrokeColor
        postInvalidate()
    }

    /**
     * Sets the width of the outer stroke
     */
    fun setOuterStrokeWidth(outerStrokeWidth: Int) {
        mOuterStrokeWidth =
            Utilities.dpToPx(context.resources, outerStrokeWidth.toFloat()).toInt()
        postInvalidate()
    }

    /**
     * Sets the color of the progress stroke
     */
    fun setProgressStrokeColor(progressStrokesColor: Int) {
        mProgressStrokeColor = progressStrokesColor
        postInvalidate()
    }

    /**
     * Sets the width of the progress stroke
     */
    fun setProgressStrokeWidth(progressStrokeWidth: Int) {
        mProgressStrokeWidth =
            Utilities.dpToPx(context.resources, progressStrokeWidth.toFloat()).toInt()
        postInvalidate()
    }

    /**
     * Sets the color of the text
     */
    fun setTextColor(mTextColor: Int) {
        textColor = mTextColor
        postInvalidate()
    }

    /**
     * Sets the size of the text
     */
    fun setTextSize(textSize: Float) {
        mTextSize = spToPx(context.resources, textSize)
        postInvalidate()
    }

    /**
     * Sets the maximum value for the progress
     */
    private fun setMaxValue(max: Int?) {
        if (max != null) {
            if (max > 0) {
                mMax = max
            }
        } else
            mMax = 0
    }

    /**
     * Sets the maximum value for the progress
     */
    fun setMax(max: Int?) {
        setMaxValue(max)
        postInvalidate()
    }

    /**
     * Sets if the progress wil be rounded corners or not
     */
    fun setProgressRoundedCorners(progressRoundedCorners: Boolean) {
        mProgressRoundedCorners = progressRoundedCorners
        postInvalidate()
    }

    /**
     * Sets the background color of the inner circle
     */
    fun setInnerCircleBackground(innerCircleBackground: Int) {
        mInnerCircleBackground = innerCircleBackground
        postInvalidate()
    }

    /**
     * Sets the text display type
     * @param textDisplay will be either NO_TEXT, PROVIDED_TEXT, PROGRESS_VALUE or PROGRESS_PERCENTAGE
     */
    fun setTextDisplay(textDisplay: TextDisplay) {
        mTextDisplay = textDisplay.value
        postInvalidate()
    }

    /**
     * Sets the direction of drawing progress
     * @param progressDirection will be either CLOCKWISE or ANTICLOCKWISE
     */
    fun setProgressDirection(progressDirection: ProgressDirection) {
        mProgressDirection = progressDirection.value
        postInvalidate()
    }

    /**
     * Sets the start angle of drawing progress
     * @param startAngle will be either RIGHT, BOTTOM, LEFT or TOP
     */
    fun setStartAngle(startAngle: StartAngle) {
        mStartAngle = startAngle.value
        postInvalidate()
    }

    /**
     * Sets text to the circle progress and automatically set the text display to PROVIDED_TEXT
     */
    fun setText(text: CharSequence?) {
        mText = if (!text.isNullOrEmpty()) {
            mTextDisplay = TextDisplay.PROVIDED_TEXT.value
            text.toString()
        } else
            ""
        postInvalidate()
    }

    /**
     * Retrieves the max value
     */
    fun getMax(): Int {
        return mMax
    }

    /**
     * Retrieves the progress value
     */
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
