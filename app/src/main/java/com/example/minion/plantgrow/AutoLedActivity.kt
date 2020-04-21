package com.example.minion.plantgrow

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.*

class AutoLedActivity : AppCompatActivity() {
    private lateinit var rootLayout: LinearLayout
    private var height = 0
    private var width = 0
    private var index = 0
    private val daysInWeek =
        arrayOf("Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle")
    private lateinit var ledMainInfo: LedMainInfo
    private lateinit var ledInfo: LedInfo
    private var powerTextViews = ArrayList<TextView>()
    private var runnable = object : Runnable {
        override fun run() {
            var textViewIndex = 0
            ledInfo = LedMainInfo.getInstance().getLedInfo(index)

            for (index in 0..6) {
                powerTextViews[textViewIndex].text =
                    "Výkon: ${ledInfo.getRedLedPower(index)}W"
                powerTextViews[textViewIndex + 1].text =
                    "Výkon: ${ledInfo.getBlueLedPower(index)}W"
                textViewIndex += 2
            }

            Handler().postDelayed(this, 20)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auto_led_layout)
        rootLayout = findViewById(R.id.auto_led_root)
        rootLayout.gravity = Gravity.VERTICAL_GRAVITY_MASK
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        index = intent.getIntExtra("index", 0)
        ledMainInfo = LedMainInfo.getInstance()
        ledInfo = ledMainInfo.getLedInfo(index)
        for (i in 0..6) {
            rootLayout.addView(createDayLayout(daysInWeek[i], i))
            rootLayout.addView(createSpace(50))
        }

        Handler().post(runnable)

    }

    private fun createDayLayout(dayString: String, dayIndex: Int): LinearLayout {
        var mainLayout = LinearLayout(applicationContext)
        mainLayout.gravity = Gravity.CENTER_VERTICAL
        mainLayout.orientation = LinearLayout.VERTICAL
        var drawable = GradientDrawable()
        drawable.setSize(width, height / 10)
        drawable.setColor(Color.LTGRAY)
        drawable.cornerRadius = 100F
        mainLayout.background = drawable
        var dayInTheWeek = createTextView(dayString)
        dayInTheWeek.textSize = 22F
        dayInTheWeek.gravity = Gravity.CENTER_HORIZONTAL
        dayInTheWeek.width = width
        mainLayout.addView(dayInTheWeek)
        mainLayout.addView(createLedLayout(dayIndex, false))
        mainLayout.addView(createLedLayout(dayIndex, true))
        mainLayout.addView(createSettingsButton(dayString, dayIndex))
        return mainLayout


    }

    private fun createTextView(text: String): TextView {
        var textView = TextView(applicationContext)
        textView.text = text
        textView.gravity = Gravity.LEFT
        textView.textSize = 20f
        textView.setTextColor(Color.BLACK)
        return textView
    }


    private fun createLedLayout(dayIndex: Int, isBlue: Boolean): RelativeLayout {
        var root = RelativeLayout(applicationContext)
        var led = if (isBlue) createTextView("Modrá LED: ") else createTextView("Červená LED: ")
        var switch = Switch(applicationContext)
        switch.isChecked =
            if (isBlue) ledInfo.getRedState(dayIndex) else ledInfo.getRedState(dayIndex)
        var statusText = TextView(applicationContext)
        statusText = if (isBlue) {
            createTextView(if (ledInfo.getBlueState(dayIndex)) "on" else "off")
        } else {
            createTextView(if (ledInfo.getRedState(dayIndex)) "on" else "off")
        }


        var powerText = createTextView("")
        powerText.text =
            if (isBlue) "Výkon: ${ledInfo.getBlueLedPower(dayIndex)}W" else "Výkon: ${ledInfo.getRedLedPower(
                dayIndex
            )}W"
        if (!switch.isChecked) {
            powerText.paintFlags = powerText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                powerText.paintFlags = powerText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                statusText.text = "on"

            } else {
                powerText.paintFlags = powerText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                statusText.text = "off"
            }
            if (isBlue) {
                ledInfo.setBlueState(dayIndex, isChecked)
            } else {
                ledInfo.setRedState(dayIndex, isChecked)
            }
        }


        powerTextViews.add(powerText)
        led.x = 50F
        statusText.x = 2 * width / 6F +50
        powerText.x = width / 2F
        switch.x = 5 * width / 6F


        root.addView(led)
        root.addView(statusText)
        root.addView(powerText)
        root.addView(switch)
        return root
    }

    private fun createSettingsButton(dayOfWeek: String, dayIndex: Int): RelativeLayout {
        var relativeLayout = RelativeLayout(applicationContext)
        relativeLayout.gravity = Gravity.CENTER
        var button = Button(applicationContext)
        var drawable = GradientDrawable()
        drawable.setColor(Color.DKGRAY)
        drawable.cornerRadius = 25f
        drawable.setStroke(3, Color.WHITE)
        button.background = drawable
        button.gravity = Gravity.CENTER
        button.text = "Upravit"
        button.width = width / 4
        button.setTextColor(Color.WHITE)
        button.textSize = 18F
        button.setOnClickListener {
            var intent = Intent(applicationContext, PopupWindowActivity::class.java)
            intent.putExtra("index", index)
            intent.putExtra("dayIndex", dayIndex)
            intent.putExtra("dayString", dayOfWeek)
            startActivity(intent)
        }
        relativeLayout.addView(button)
        return relativeLayout
    }

    private fun createSpace(height: Int): Space {
        var space = Space(applicationContext)
        space.minimumHeight = height
        space.minimumWidth = width
        return space
    }


    override fun onDestroy() {
        super.onDestroy()
        ledMainInfo.save()
    }
}