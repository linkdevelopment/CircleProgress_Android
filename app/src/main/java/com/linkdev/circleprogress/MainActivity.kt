package com.linkdev.circleprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        free font
        https://www.1001freefonts.com/designer-typesetit-fontlisting.php
         */
        progressCircular.setTextFont(R.font.alex_brush_regular)
        submit.setOnClickListener { onSubmitClick() }

    }

    private fun onSubmitClick() {
        if (edtProgress.text.isNotEmpty())
            progressCircular.setProgress(edtProgress.text.toString().toFloat())
    }
}
