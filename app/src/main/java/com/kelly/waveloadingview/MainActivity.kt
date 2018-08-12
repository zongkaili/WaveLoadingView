package com.kelly.waveloadingview

import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var currentPos = 10
    private val maxProgress = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        waveCircle.radius = 200
        waveCircle.maxProgress = maxProgress
        waveCircle.currentProgress = currentPos

        btnStart.setOnClickListener {
            currentPos = 0
            val objectAnimator = ObjectAnimator.ofInt(waveCircle, "currentProgress",0, 100)
            objectAnimator.duration = 10000
            objectAnimator.start()
            waveCircle.start()
        }
    }
}
