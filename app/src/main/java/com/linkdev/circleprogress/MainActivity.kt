package com.linkdev.circleprogress


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    companion object {
        const val ZERO_INDEX = 0
        const val ONE_INDEX = 1
        const val TWO_INDEX = 2
    }

    private var max: Int = 0
    private var progress: Float = 0f
    val handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initStartAngleSpinner()
        initDirectionSpinner()
        initTextDisplaySpinner()
        /*
       free font
       https://www.1001freefonts.com/designer-typesetit-fontlisting.php
        */
        progressCircular.setTextFont(R.font.alex_brush_regular)

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
        rdgrpTextColor.setOnCheckedChangeListener { group, checkedId ->
            onRdgrpTextColor(
                checkedId
            )
        }
        edtMax.setOnEditorActionListener(edtMaxActionListener)
        edtProgress.setOnEditorActionListener(edtProgressActionListener)
        edtText.addTextChangedListener(edtTextWatcher)
        edtProgressColor.setOnEditorActionListener(edtProgressColorActionListener)
        edtStrokeColor.setOnEditorActionListener(edtStrokeColorActionListener)
        edtBackgroundCircle.setOnEditorActionListener(edtBackgroundCircleColorActionListener)
        edtTextColor.setOnEditorActionListener(edtTextColorActionListener)
        btnAnimate.setOnClickListener { onAnimateClick() }
    }

    private fun onRdgrpTextColor(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnMaize -> {
                progressCircular.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.maize,
                        null
                    )
                )
            }
            R.id.rdbtnCharcoal -> {
                progressCircular.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.charcoal,
                        null
                    )
                )
            }
        }

    }

    private var edtTextColorActionListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    progressCircular.setTextColor(Color.parseColor(edtTextColor.text.toString()))
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
                return true
            }
            return false
        }
    }
    private var edtBackgroundCircleColorActionListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    progressCircular.setInnerCircleBackground(Color.parseColor(edtBackgroundCircle.text.toString()))
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
                return true
            }
            return false
        }
    }

    private var edtStrokeColorActionListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    progressCircular.setOuterStrokeColor(Color.parseColor(edtStrokeColor.text.toString()))
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
                return true
            }
            return false
        }
    }

    private var edtProgressColorActionListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    progressCircular.setProgressStrokeColor(Color.parseColor(edtProgressColor.text.toString()))
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
                return true
            }
            return false
        }
    }

    private fun initTextDisplaySpinner() {
        val spTextDisplayItems = arrayOf(
            getString(R.string.noText), getString(R.string.percentage)
            , getString(R.string.progress)
            , getString(R.string.providedText)
        )
        val spTextDisplayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spTextDisplayItems)
        spTextDisplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTextDisplay.adapter = spTextDisplayAdapter
        spTextDisplay.setSelection(ONE_INDEX)
        spTextDisplay.onItemSelectedListener = spTextDisplayItemSelectedListener
    }

    private fun initDirectionSpinner() {
        val spDirectionItems =
            arrayOf(getString(R.string.withClockWise), getString(R.string.antiClockWise))
        val spDirectionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spDirectionItems)
        spDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDirection.adapter = spDirectionAdapter
        spDirection.onItemSelectedListener = spDirectionItemSelectedListener
    }

    private fun initStartAngleSpinner() {
        val spStartAngleItems = arrayOf(
            getString(R.string.top),
            getString(R.string.right),
            getString(R.string.left),
            getString(R.string.bottom)
        )
        val spStartAngleAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spStartAngleItems)
        spStartAngleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStartAngle.adapter = spStartAngleAdapter
        spStartAngle.onItemSelectedListener = spStartAngleItemSelectedListener
    }

    private var spTextDisplayItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parentView: AdapterView<*>?,
            selectedItemView: View,
            position: Int,
            id: Long
        ) {
            onSpTextDisplayItemSelected(position)
        }

        override fun onNothingSelected(parentView: AdapterView<*>?) {
        }
    }


    private var spDirectionItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parentView: AdapterView<*>?,
            selectedItemView: View,
            position: Int,
            id: Long
        ) {
            onSpDirectionItemSelected(position)
        }

        override fun onNothingSelected(parentView: AdapterView<*>?) {
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

        override fun onNothingSelected(parentView: AdapterView<*>?) {
        }
    }

    private fun onSpStartAngleItemSelected(position: Int) {
        when (position) {
            ZERO_INDEX -> {
                progressCircular.setStartAngle(StartAngle.TOP)
            }
            ONE_INDEX -> {
                progressCircular.setStartAngle(StartAngle.RIGHT)
            }
            TWO_INDEX -> {
                progressCircular.setStartAngle(StartAngle.LEFT)
            }
            else -> {
                progressCircular.setStartAngle(StartAngle.BOTTOM)
            }
        }
    }

    private fun onAnimateClick() {
        if (!edtProgress.text.toString().isNullOrEmpty()) {
            max = edtProgress.text.toString().toInt()
        } else
            max = progressCircular.getMax()

        handler.postDelayed(runnable, 0)
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {

            if (progress in 0.0..max.toDouble()) {
                progressCircular.setProgress(progress)
                progress++
                handler.postDelayed(this, 50L)
            } else{
                handler.removeCallbacks(this)
                progress = 0f
            }
        }
    }

    private var edtMaxActionListener= object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!edtMax.text.toString().isNullOrEmpty())
                    progressCircular.setMax(edtMax.text.toString().toInt())
                else
                    progressCircular.setMax(0)
                return true
            }
            return false
        }
    }

    private var edtProgressActionListener= object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!edtProgress.text.toString().isNullOrEmpty())
                    progressCircular.setProgress(edtProgress.text.toString().toFloat())
                else
                    progressCircular.setProgress(0f)
                return true
            }
            return false
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

    private fun onSpTextDisplayItemSelected(position: Int) {
        when (position) {
            ZERO_INDEX -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.NO_TEXT)
            }
            ONE_INDEX -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.PROGRESS_PERCENTAGE)
            }
            TWO_INDEX -> {
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
            R.id.rdbtnEarthYellow -> {
                progressCircular.setInnerCircleBackground(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.earthYellow,
                        null
                    )
                )
            }
            R.id.rdbtnSkobeloff -> {
                progressCircular.setInnerCircleBackground(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.skobeloff,
                        null
                    )
                )
            }
        }
    }

    private fun onRdgrpStrokeColor(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnSandyBrown -> {
                progressCircular.setOuterStrokeColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.sandyBrown,
                        null
                    )
                )
            }
            R.id.rdbtnPersianGreen -> {
                progressCircular.setOuterStrokeColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.persianGreen,
                        null
                    )
                )
            }
        }
    }

    private fun onRdgrpProgressColor(checkedId: Int) {
        when (checkedId) {
            R.id.rdbtnTerraCotta -> {
                progressCircular.setProgressStrokeColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.terraCotta,
                        null
                    )
                )
            }
            R.id.rdbtnDarkSeaGreen -> {
                progressCircular.setProgressStrokeColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.darkSeaGreen,
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
            ZERO_INDEX -> {
                progressCircular.setProgressDirection(ProgressDirection.CLOCKWISE)
            }
            else -> {
                progressCircular.setProgressDirection(ProgressDirection.ANTICLOCKWISE)
            }
        }
    }

}
