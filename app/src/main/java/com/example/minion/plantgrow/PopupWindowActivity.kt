package com.example.minion.plantgrow

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView

class PopupWindowActivity : AppCompatActivity() {
    private var index = 0
    private lateinit var mainLayout: RelativeLayout
    private var dayIndex = 0
    private var displayMetrics = DisplayMetrics()
    private var height = 0
    private var width = 0
    private var dayString = ""
    private lateinit var ledInfo: LedInfo
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
        ledInfo = LedMainInfo.getInstance().getLedInfo(index)
    }

    private fun setPopupStyle() {
        window.setLayout(width*9/10,height/3)
        var gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = 50F
        gradientDrawable.setColor(Color.DKGRAY)
        gradientDrawable.setStroke(3, Color.WHITE)
        gradientDrawable.setSize(width*9/10, height/3)
        window.setBackgroundDrawable(gradientDrawable)
        mainLayout.background = gradientDrawable
    }
    private fun createSliderLayout(isBlue:Boolean):LinearLayout{
        var root = LinearLayout(applicationContext)
        var relativeLayout = RelativeLayout(applicationContext)
        var seekBar : SeekBar = SeekBar(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.min = 0
        }
        seekBar.max = 1000
        seekBar.progress = ledInfo.getBlueLedPower(dayIndex).toInt()
        seekBar.minimumWidth = width*3/4
        var ledName = if (isBlue) createTextView("Modrá LED: ") else createTextView("Červená LED: ")



return root

    }
private fun createTextView(text:String):TextView{
    var textView = TextView(applicationContext)
    textView.text = text
    textView.gravity = Gravity.LEFT
    textView.textSize = 20f
    textView.setTextColor(Color.BLACK)
    return textView
}



}