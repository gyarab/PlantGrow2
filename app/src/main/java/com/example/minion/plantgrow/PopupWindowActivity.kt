package com.example.minion.plantgrow

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
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
    private lateinit var ledInfoDuplicate: LedInfo
    private lateinit var ledInfoTime: LedInfoTime

    @RequiresApi(Build.VERSION_CODES.O)
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
        ledInfoTime = LedMainInfo.getInstance().getLedTimeInfo(index)
        ledInfoDuplicate = ledInfo
        mainLayout.addView(createTitle())
        mainLayout.addView(createSliderLayout(false))
        mainLayout.addView(createSliderLayout(true))
        mainLayout.addView(createTimeSlider(false))
        mainLayout.addView(createTimeSlider(true))
    }

    private fun setPopupStyle() {
        window.setLayout(width * 9 / 10, height / 2)
        var gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = 50F
        gradientDrawable.setColor(Color.BLACK)
        gradientDrawable.setStroke(3, Color.WHITE)
        gradientDrawable.setSize(width * 9 / 10, height / 3)
        window.setBackgroundDrawable(gradientDrawable)
        mainLayout.background = gradientDrawable
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSliderLayout(isBlue: Boolean): LinearLayout {
        var root = LinearLayout(applicationContext)
        var relativeLayout = RelativeLayout(applicationContext)
        relativeLayout.minimumWidth = width
        var seekBar: SeekBar = SeekBar(applicationContext)
        seekBar.min = 0
        seekBar.max = 1000

        Log.i("App", "${seekBar.max}")
        Log.i("App", "${seekBar.progress}")
        seekBar.minimumWidth = width
        var powerTextView = if (isBlue) createTextView(
            "Výkon: ${ledInfo.getBlueLedPower(dayIndex)}W | ${ledInfo.getBluePercentage(dayIndex)}%"
        ) else createTextView(
            "Výkon: ${ledInfo.getRedLedPower(dayIndex)}W | ${ledInfo.getRedPercentage(
                dayIndex
            )}%"
        )

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (isBlue) {
                    if (ledInfo.getRedLedPower(dayIndex) + progress / 1000F * 35 < 35) {
                        ledInfo.setBluePercentage(dayIndex, progress.toLong())
                    } else {
                        seekBar!!.progress =
                            ((35F - ledInfo.getRedLedPower(dayIndex)) / 35F * 1000).toInt()

                    }

                } else {
                    if (ledInfo.getBlueLedPower(dayIndex) + progress / 1000F * 30 < 35) {
                        ledInfo.setRedPercentage(dayIndex, progress.toLong())
                    } else {

                        seekBar!!.progress =
                            ((35F - ledInfo.getBlueLedPower(dayIndex)) / 35F * 1000).toInt()
                    }
                }
                powerTextView.text =
                    if (isBlue) ("Výkon: ${ledInfo.getBlueLedPower(dayIndex)}W | ${ledInfo.getBluePercentage(
                        dayIndex
                    ) / 10}%") else ("Výkon: ${ledInfo.getRedLedPower(dayIndex)}W | ${ledInfo.getRedPercentage(
                        dayIndex
                    ) / 10}%")


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })



        root.gravity = Gravity.VERTICAL_GRAVITY_MASK
        root.orientation = LinearLayout.VERTICAL
        seekBar.progress = if (isBlue) ledInfoDuplicate.getBluePercentage(dayIndex)
            .toInt() else ledInfoDuplicate.getRedPercentage(dayIndex).toInt()
        seekBar.minimumWidth = width * 3 / 4
        var ledName = if (isBlue) createTextView("Modrá LED: ") else createTextView("Červená LED: ")
        Log.i("App", "${seekBar.progress}")
        root.addView(ledName)
        root.addView(seekBar)
        root.addView(powerTextView)

        return root
    }

    private fun createTextView(text: String): TextView {
        var textView = TextView(applicationContext)
        textView.text = text
        textView.setPadding(width / 30, 20, 0, 20)
        textView.gravity = Gravity.LEFT
        textView.textSize = 20f
        textView.setTextColor(Color.WHITE)
        return textView
    }

    private fun createTitle(): TextView {
        var textView = createTextView(dayString)
        textView.textSize = 25F
        textView.gravity = Gravity.CENTER
        textView.width = width
        return textView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTimeSlider(isMinute: Boolean): LinearLayout {
        var linearLayout = LinearLayout(applicationContext)
        linearLayout.gravity = Gravity.CENTER_HORIZONTAL
        linearLayout.orientation = LinearLayout.VERTICAL
        var showTextView =
            if (isMinute) createTextView("Délka svitu v minutách: ${ledInfoTime.getTimeInMinutes(dayIndex)}min") else createTextView(
                "Délka svitu v hodinách: ${ledInfoTime.getTimeInHours(dayIndex)}h"
            )
        var seekBar = SeekBar(applicationContext)
        if (isMinute) {
            seekBar.min = 0
            seekBar.max = 59
            seekBar.progress = ledInfoTime.getTimeInMinutes(dayIndex)
        } else {
            seekBar.min = 1
            seekBar.max = 8
            seekBar.progress = ledInfoTime.getTimeInHours(dayIndex)
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isMinute) {
                    ledInfoTime.setTimeInMinutes(dayIndex, progress)
                    showTextView.text = "Délka svitu v minutách: ${ledInfoTime.getTimeInMinutes(dayIndex)}min"
                } else {
                    ledInfoTime.setTimeInHours(dayIndex, progress)
                    showTextView.text = "Délka svitu v hodinách: ${ledInfoTime.getTimeInHours(dayIndex)}h"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        linearLayout.addView(seekBar)
        linearLayout.addView(showTextView)
        return linearLayout
    }


    override fun onDestroy() {
        super.onDestroy()
        LedMainInfo.getInstance().save()

    }
}