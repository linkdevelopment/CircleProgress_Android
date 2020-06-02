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
package com.linkdev.circleprogress_sample


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
import com.linkdev.circleprogress.ProgressDirection
import com.linkdev.circleprogress.StartAngle
import com.linkdev.circleprogress.TextDisplay
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var max: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSpinners()
        setFont()
        setListeners()
    }

    private fun initSpinners() {
        initStartAngleSpinner()
        initDirectionSpinner()
        initTextDisplaySpinner()
    }

    private fun setFont() {
        progressCircular.setTextFont(R.font.times_new_roman_italique_400)
    }

    private fun setListeners() {
        setMaxAndProgressTextWatchers()
        setStrokeAndProgressWidthListeners()
        setColorRadioGroupsListeners()
        setColorEditTextsListeners()
        setTextWatcherForCircleText()
        setAnimateClickListener()
        setFontSizeChangeListener()
        setRoundCornersAndShowDecimalsListener()
    }

    private fun setRoundCornersAndShowDecimalsListener() {
        chkRounded.setOnCheckedChangeListener { buttonView, isChecked ->
            onChkRoundedChange(
                isChecked
            )
        }
    }

    private fun setFontSizeChangeListener() {
        sbFontSize.setOnSeekBarChangeListener(onTextSizeSeekBarChangeListener)
    }

    private fun setAnimateClickListener() {
        btnAnimate.setOnClickListener { onAnimateClick() }
    }

    private fun setTextWatcherForCircleText() {
        edtText.addTextChangedListener(edtTextWatcher)
    }

    private fun setColorEditTextsListeners() {
        edtProgressColor.setOnEditorActionListener(edtProgressColorActionListener)
        edtStrokeColor.setOnEditorActionListener(edtStrokeColorActionListener)
        edtBackgroundCircle.setOnEditorActionListener(edtBackgroundCircleColorActionListener)
        edtTextColor.setOnEditorActionListener(edtTextColorActionListener)
    }

    private fun setColorRadioGroupsListeners() {
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
    }

    private fun setStrokeAndProgressWidthListeners() {
        sbStokeThickness.setOnSeekBarChangeListener(onStrokeSeekBarChangeListener)
        sbProgressThickness.setOnSeekBarChangeListener(onProgressSeekBarChangeListener)
    }

    private fun setMaxAndProgressTextWatchers() {
        edtMax.addTextChangedListener(edtMaxTextWatcher)
        edtProgress.addTextChangedListener(edtProgressTextWatcher)
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
        spTextDisplay.setSelection(1)
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
            getString(R.string.bottom),
            getString(R.string.left),
            getString(R.string.right)
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
            0 -> {
                progressCircular.setStartAngle(StartAngle.TOP)
            }
            1 -> {
                progressCircular.setStartAngle(StartAngle.BOTTOM)

            }
            2 -> {
                progressCircular.setStartAngle(StartAngle.LEFT)
            }
            else -> {
                progressCircular.setStartAngle(StartAngle.RIGHT)
            }
        }
    }

    private fun onAnimateClick() {
        if (!edtProgress.text.toString().isNullOrEmpty()) {
            max = edtProgress.text.toString().toInt()
        } else
            max = progressCircular.getMax()

        progressCircular.setProgressWithAnimation(max,50L)
    }


    private var edtMaxTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrEmpty())
                if (s.toString().equals("."))
                    progressCircular.setMax(0)
                else
                    progressCircular.setMax(s.toString().toInt())
            else
                progressCircular.setMax(0)
        }
    }

    private var edtProgressTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrEmpty())
                if (s.toString().equals("."))
                    progressCircular.setProgress(0f)
                else
                    progressCircular.setProgress(s.toString().toFloat())
            else
                progressCircular.setProgress(0f)
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
            0 -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.NO_TEXT)
            }
            1 -> {
                edtText.visibility = View.GONE
                progressCircular.setTextDisplay(TextDisplay.PROGRESS_PERCENTAGE)
            }
            2 -> {
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

    private var onProgressSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            progressCircular.setProgressStrokeWidth(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }
    private var onStrokeSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            progressCircular.setOuterStrokeWidth(progress)
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
        progressCircular.setProgressRoundedCorners(checked)
    }

    private fun onSpDirectionItemSelected(position: Int) {
        when (position) {
            0 -> {
                progressCircular.setProgressDirection(ProgressDirection.CLOCKWISE)
            }
            else -> {
                progressCircular.setProgressDirection(ProgressDirection.ANTICLOCKWISE)
            }
        }
    }

}
