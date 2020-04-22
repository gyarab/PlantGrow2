package com.example.minion.plantgrow

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.O)
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
        rootLayout.addView(createSeekBarLayout())
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSeekBarLayout():LinearLayout{
        var seekBar = SeekBar(applicationContext)
        var linearLayout = LinearLayout(applicationContext)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER_VERTICAL
        seekBar.max = 200
        seekBar.min = 50
        seekBar.minimumWidth = width*3/4

        seekBar.progress = wateringMainInfo.getWaterVolume(index).toInt()
        var waterVolumeInfo = createTextView("Objem vody k zalití: ${wateringMainInfo.getWaterVolume(index)} ml")
        waterVolumeInfo.minimumWidth = width
        waterVolumeInfo.setTextColor(Color.WHITE)
        waterVolumeInfo.gravity = Gravity.CENTER
        waterVolumeInfo.setPadding(50,0,0,50)


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                waterVolumeInfo.text = "Objem vody k zalití: ${wateringMainInfo.getWaterVolume(index)} ml"
                wateringMainInfo.setWaterVolume(index, progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        linearLayout.addView(waterVolumeInfo)
        linearLayout.addView(seekBar)
        return linearLayout
    }




    override fun onDestroy() {
        super.onDestroy()
        wateringMainInfo.saveWateringData()
    }


}