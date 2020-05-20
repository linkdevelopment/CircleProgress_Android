package com.linkdev.circleprogress


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var max: Int = 0
    private var progress: Float = 0f
    val handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initStartAngleSpinner()
        initDirectionSpinner()
//        progressCircular.setTextFont(R.font.alex_brush_regular)

        chkRounded.setOnCheckedChangeListener { buttonView, isChecked ->
            onChkRoundedChange(
                isChecked
            )
        }
        sbFontSize.setOnSeekBarChangeListener(onTextSizeSeekBarChangeListener)
        sbStokeThickness.setOnSeekBarChangeListener(onStrokeSeekBarChangeListener)
        sbProgressThickness.setOnSeekBarChangeListener(onProgressSeekBarChangeListener)
        chkShowDecimal.setOnCheckedChangeListener { buttonView, isChecked ->
            onChkShowDecimal(
                isChecked
            )
        }
        rdgrpProgressColor.setOnCheckedChangeListener { group, checkedId ->
            onRdgrpProgressColor(
                checkedId
            )
        }
        rdgrpStrokeColor.setOnCheckedChangeListener { group, checkedId ->
            onRdgrpStrokeColor(
                checkedId
            )
        }
        rdgrpBackgroundCircle.setOnCheckedChangeListener { group, checkedId ->
            onRdgrpBackgroundCircle(
                checkedId
            )
        }
        rdgrpTextDisplay.setOnCheckedChangeListener { group, checkedId ->
            onrRdgrpTextDisplay(
                checkedId
            )
        }
        edtMax.addTextChangedListener(edtMaxTextWatcher)
        edtProgress.addTextChangedListener(edtProgressTextWatcher)
        edtText.addTextChangedListener(edtTextWatcher)
        /*
        free font
        https://www.1001freefonts.com/designer-typesetit-fontlisting.php
         */
//        progressCircular.setTextFont(R.font.alex_brush_regular)

    }

    private fun initDirectionSpinner() {

        val spDirectionItems = arrayOf(getString(R.string.withClockWise), getString(R.string.antiClockWise))
        val spDirectionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spDirectionItems)
        spDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDirection.adapter = spDirectionAdapter
        spDirection.onItemSelectedListener = spDirectionItemSelectedListener
    }

    private fun initStartAngleSpinner() {
        val spStartAngleItems = arrayOf(getString(R.string.top), getString(R.string.right), getString(R.string.left),getString(R.string.bottom))
        val spStartAngleAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spStartAngleItems)
        spStartAngleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStartAngle.adapter = spStartAngleAdapter
        spStartAngle.onItemSelectedListener = spStartAngleItemSelectedListener
    }

    private var spDirectionItemSelectedListener= object : OnItemSelectedListener {
        override fun onItemSelected(
            parentView: AdapterView<*>?,
            selectedItemView: View,
            position: Int,
            id: Long
        ) {
            onSpDirectionItemSelected(position)
        }

        override fun onNothingSelected(parentView: AdapterView<*>?) { // your code here
        }
    }

    private var spStartAngleItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parentView: AdapterView<*>?,
            selectedItemView: View,
            position: Int,
            id: Long
        ) {
            onSpStartAngleItemSelected(position)
        }

        override fun onNothingSelected(parentView: AdapterView<*>?) { // your code here
        }
    }

    private fun onSpStartAngleItemSelected(position: Int) {
        when (position) {
            0 -> {
                progressCircular.setStartAngle(StartAngle.TOP)
            }
            1 -> {
                progressCircular.setStartAngle(StartAngle.RIGHT)
            }
            2 -> {
                progressCircular.setStartAngle(StartAngle.LEFT)
            }
            else -> {
                progressCircular.setStartAngle(StartAngle.BOTTOM)
            }
        }
    }

    //    private fun onAnimateClick() {
//        max = animatedProgressCircular.getMax()
//        handler.postDelayed(runnable,0)
//    }
//
//    private val runnable: Runnable = object : Runnable {
//        override fun run() {
//            if (progress in 0.0..max.toDouble()) {
//                progress++
//                animatedProgressCircular.setProgress(progress)
//            }else
//                handler.removeCallbacks(this)
//
//            handler.postDelayed(this, 200L)
//        }
//    }
    private var edtMaxTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrEmpty())
                progressCircular.setMax(s.toString().toInt())
            else
                progressCircular.setMax(0)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }
    private var edtProgressTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrEmpty())
                progressCircular.setProgress(s.toString().toFloat())
            else
                progressCircular.setProgress(0f)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }
    private var edtTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            progressCircular.setText(s)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    private fun onrRdgrpTextDisplay(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnNoText -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.NO_TEXT)
            }
            R.id.rdbtnPercentage -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.PROGRESS_PERCENTAGE)
            }
            R.id.rdbtnProgress -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.PROGRESS_VALUE)
            }
            else -> {
                progressCircular.setTextDisplay(TextDisplay.PROVIDED_TEXT)
                edtText.visibility = View.VISIBLE
            }
        }
    }

    private fun onRdgrpBackgroundCircle(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnBlue -> {
                progressCircular.setInnerCircleBackground(Color.BLUE)
            }
            R.id.rdbtnMagneta -> {
                progressCircular.setInnerCircleBackground(Color.MAGENTA)
            }
        }
    }

    private fun onRdgrpStrokeColor(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnYellow -> {
                progressCircular.setOuterStrokeColor(Color.YELLOW)
            }
            R.id.rdbtnCyan -> {
                progressCircular.setOuterStrokeColor(Color.parseColor("#00ffff"))
            }
        }
    }

    private fun onRdgrpProgressColor(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnRed -> {
                progressCircular.setProgressStrokeColor(Color.RED)
            }
            R.id.rdbtnGreen -> {
                progressCircular.setProgressStrokeColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
            }
        }
    }

    private fun onChkShowDecimal(checked: Boolean) {
        progressCircular.setShowDecimalZero(checked)
    }

    private var onProgressSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            progressCircular.setProgressStrokeThickness(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }
    private var onStrokeSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            progressCircular.setOuterStrokeThickness(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }
    private var onTextSizeSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            progressCircular.setTextSize(progress.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    private fun onChkRoundedChange(checked: Boolean) {
        progressCircular.setProgressRoundedEnd(checked)
    }

    private fun onSpDirectionItemSelected(position: Int) {
        when (position) {
            0-> {
                progressCircular.setProgressDirection(ProgressDirection.CLOCKWISE)
            }
            else-> {
                progressCircular.setProgressDirection(ProgressDirection.ANTICLOCKWISE)
            }
        }
    }



    private fun onSubmitClick() {
//        if (edtProgress.text.isNotEmpty())
//            progressCircular.setProgress(edtProgress.text.toString().toFloat())
    }
}
