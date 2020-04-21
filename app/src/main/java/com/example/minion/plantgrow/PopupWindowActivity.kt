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
    private lateinit var mainLayout: LinearLayout
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
        mainLayout.addView(createTitle())
        mainLayout.addView(createSliderLayout(false))
        mainLayout.addView(createSliderLayout(true))


    }

    private fun setPopupStyle() {
        window.setLayout(width * 9 / 10, height / 3)
        var gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = 50F
        gradientDrawable.setColor(Color.BLACK)
        gradientDrawable.setStroke(3, Color.WHITE)
        gradientDrawable.setSize(width * 9 / 10, height / 3)
        window.setBackgroundDrawable(gradientDrawable)
        mainLayout.background = gradientDrawable
    }

    private fun createSliderLayout(isBlue: Boolean): LinearLayout {
        var root = LinearLayout(applicationContext)
        var relativeLayout = RelativeLayout(applicationContext)
        relativeLayout.minimumWidth = width
        var seekBar: SeekBar = SeekBar(applicationContext)
        seekBar.minimumWidth = width
        var powerTextView = if (isBlue) createTextView("Výkon: ${ledInfo.getBlueLedPower(dayIndex)}W | ${ledInfo.getBluePercentage(dayIndex)}%") else createTextView("Výkon: ${ledInfo.getRedLedPower(dayIndex)}W | ${ledInfo.getRedPercentage(dayIndex)}%")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.min = 0
        }
        root.gravity = Gravity.VERTICAL_GRAVITY_MASK
        root.orientation = LinearLayout.VERTICAL
        seekBar.max = 1000
        seekBar.progress = ledInfo.getBlueLedPower(dayIndex).toInt()
        seekBar.minimumWidth = width * 3 / 4
        var ledName = if (isBlue) createTextView("Modrá LED: ") else createTextView("Červená LED: ")
        root.addView(ledName)
        root.addView(seekBar)
        root.addView(powerTextView)
        relativeLayout.addView(seekBar)

        return root
    }

    private fun createTextView(text: String): TextView {
        var textView = TextView(applicationContext)
        textView.text = text
        textView.gravity = Gravity.LEFT
        textView.textSize = 20f
        textView.setTextColor(Color.WHITE)
        return textView
    }
    private fun createTitle():TextView{
        var textView = createTextView(dayString)
        textView.textSize = 25F
        textView.gravity = Gravity.CENTER
        textView.width = width
        return textView


    }

}