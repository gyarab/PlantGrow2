package com.example.minion.plantgrow

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.*

class WateringActivity : AppCompatActivity() {

    private lateinit var rootLayout: LinearLayout
    private var height = 0
    private var width = 0
    private var index = 0
    private val daysInWeek =
        arrayOf("Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle")
    private lateinit var wateringMainInfo: WateringMainInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watering_layout)
        rootLayout = findViewById(R.id.water_layout)
        index = intent.getIntExtra("index", 0)
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        wateringMainInfo = WateringMainInfo.getInstance()
        for (i in 0..6) {
            rootLayout.addView(createLayout(daysInWeek[i], i))
            rootLayout.addView(createSpace(50))
        }
    }

    private fun createLayout(dayOfWeek: String, dayIndex: Int): RelativeLayout {
        var rootLinearLayout = RelativeLayout(applicationContext)
        rootLinearLayout.gravity = Gravity.CENTER_VERTICAL
        var drawable = GradientDrawable()
        drawable.setSize(width, height / 12)
        drawable.setColor(Color.LTGRAY)
        drawable.cornerRadius = 100F
        rootLinearLayout.background = drawable
        var dayTextView = createTextView("$dayOfWeek: ")
        var statusTextView =
            createTextView(if (wateringMainInfo.getWateringState(index, dayIndex)) "on" else "off")
        var switch = Switch(applicationContext)
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                statusTextView.text = "on"
            } else {
                statusTextView.text = "off"
            }
            wateringMainInfo.setWateringState(index, dayIndex, isChecked)
        }
        switch.isChecked = wateringMainInfo.getWateringState(index, dayIndex)




        dayTextView.x = 50F
        statusTextView.x = width / 3F
        switch.x = width / 2F
        rootLinearLayout.addView(dayTextView)
        rootLinearLayout.addView(statusTextView)
        rootLinearLayout.addView(switch)
        return rootLinearLayout
    }

    private fun createSpace(height:Int): Space {
    var space = Space(applicationContext)
        space.minimumWidth = width
        space.minimumHeight = height
        return space

    }

    private fun createTextView(text: String): TextView {
        var textView = TextView(applicationContext)
        textView.text = text
        textView.gravity = Gravity.LEFT
        textView.textSize = 20f
        textView.setTextColor(Color.BLACK)
        return textView
    }

    override fun onDestroy() {
        super.onDestroy()
        wateringMainInfo.saveWateringData()
    }


}