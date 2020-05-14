package com.linkdev.circleprogress

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        progressCircular.setTextFont(R.font.alex_brush_regular)


        rdgrpStartAngle.setOnCheckedChangeListener { group, checkedId ->
            onRdgrpStartAngleChecked(
                checkedId
            )
        }
        rdgrpDirection.setOnCheckedChangeListener { group, checkedId ->
            onRdgrpDirectionChecked(
                checkedId
            )
        }
        chkRounded.setOnCheckedChangeListener { buttonView, isChecked ->
            onChkRoundedChange(
                isChecked
            )
        }
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
        edtText.addTextChangedListener(edtTextWatcher)
        /*
        free font
        https://www.1001freefonts.com/designer-typesetit-fontlisting.php
         */
        progressCircular.setTextFont(R.font.alex_brush_regular)

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

    private fun onChkRoundedChange(checked: Boolean) {
        progressCircular.setProgressRoundedEnd(checked)
    }

    private fun onRdgrpDirectionChecked(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnWithClockWise -> {
                progressCircular.setProgressDirection(ProgressDirection.WITH_CLOCK_WISE)
            }
            R.id.rdbtnAntiClokWise -> {
                progressCircular.setProgressDirection(ProgressDirection.ANTI_CLOCK_WISE)
            }
        }
    }

    private fun onRdgrpStartAngleChecked(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnTop -> {
                progressCircular.setStartAngle(StartAngle.TOP)
            }
            R.id.rdbtnRight -> {
                progressCircular.setStartAngle(StartAngle.RIGHT)
            }
            R.id.rdbtnLeft -> {
                progressCircular.setStartAngle(StartAngle.LEFT)
            }
            else -> {
                progressCircular.setStartAngle(StartAngle.BOTTOM)
            }
        }
    }

    private fun onSubmitClick() {
//        if (edtProgress.text.isNotEmpty())
//            progressCircular.setProgress(edtProgress.text.toString().toFloat())
    }
}
