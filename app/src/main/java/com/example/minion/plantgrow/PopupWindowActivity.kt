package com.example.minion.plantgrow

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.RelativeLayout

class PopupWindowActivity : AppCompatActivity() {
    private var index = 0
    private lateinit var mainLayout: RelativeLayout
    private var dayIndex = 0
    private var displayMetrics = DisplayMetrics()
    private var height = 0
    private var width = 0
    private var dayString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_window_layout)
        mainLayout = findViewById(R.id.popup_layout)
        index = intent.getIntExtra("index", 0)
        dayIndex = intent.getIntExtra("dayIndex", 0)
        dayString = intent.getStringExtra("dayString")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        setPopupStyle()
    }

    private fun setPopupStyle() {
        var root = RelativeLayout(applicationContext)
        window.setLayout(width*9/10,height/3)


        var gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = 50F
        gradientDrawable.setColor(Color.DKGRAY)
        gradientDrawable.setStroke(3, Color.WHITE)
        gradientDrawable.setSize(width*9/10, height/3)
        window.setBackgroundDrawable(gradientDrawable)
        mainLayout.background = gradientDrawable
        root.background = gradientDrawable
        mainLayout.addView(root)
    }
}