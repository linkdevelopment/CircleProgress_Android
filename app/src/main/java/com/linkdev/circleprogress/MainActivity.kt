package com.linkdev.circleprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress_circular.setTextFont(R.font.montserrat_bold)
        submit.setOnClickListener { onSubmitClick() }

    }

    private fun onSubmitClick() {
        if (edtProgress.text.isNotEmpty())
            progress_circular.setProgress(edtProgress.text.toString().toFloat())
    }
}
