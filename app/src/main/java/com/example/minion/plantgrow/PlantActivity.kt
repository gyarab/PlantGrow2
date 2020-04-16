package com.example.minion.plantgrow

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.widget.*


class PlantActivity : AppCompatActivity() {
    private var index = 0
    private lateinit var mainLayout: LinearLayout
    private var displayMetrics = DisplayMetrics()
    private var height = 0
    private var width = 0
    private lateinit var plantInfo: PlantInfo
    private var wateringOn = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_info)

        mainLayout = findViewById(R.id.plant_layout)
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        index = intent.getIntExtra("index", 0)
        plantInfo = PlantInfoCollection.getInstance().getPotInfo(--index)
        applicationContext.filesDir.path
        makeTitle()
        makeOverview()
        makeLedSliders(30f, "Červená LED:", false)
        makeLedSliders(35f, "Modrá LED:", true)
        makeManualCommands()
        makeAutoLayout()
    }

    private fun makeTitle() {
        var textView = TextView(this)
        var thisIndex = index + 1
        textView.text = "Rostlina $thisIndex"
        textView.textSize = 25F
        textView.setTextColor(Color.WHITE)
        textView.width = width
        textView.gravity = Gravity.CENTER
        mainLayout.addView(textView)
    }

    private fun makeOverview() {
        var plantInfoTitle = makeTextViewStyle()
        plantInfoTitle.textSize = 23F
        plantInfoTitle.setPadding(10, 40, 0, 0)
        plantInfoTitle.text = "Informace"
        var linearLayout = LinearLayout(applicationContext)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.gravity = Gravity.LEFT
        var plantTitleView = makeTextViewStyle()
        plantTitleView.text = "Jmeno: "
        var editText = EditText(applicationContext)
        editText.setTextColor(Color.WHITE)
        editText.textSize = 20f
        editText.setText("${plantInfo.name}")
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (editText.text.length > 30) {
                    var string = editText.text
                    string.take(30)
                    editText.text = string
                }
                PlantInfoCollection.getInstance()
                    .changePotValue(index = index, name = editText.text.toString())
                plantInfo = PlantInfoCollection.getInstance().getPotInfo(index)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editText.text.length > 30) {
                    var string = editText.text
                    string.take(30)
                    editText.text = string

                }
            }
        })
        var plantMoistureView = makeTextViewStyle()
        plantMoistureView.text = "Připojeno"


        var powerOnLed = makeTextViewStyle()
        powerOnLed.text =
            "Výkon LED: ${plantInfo.getBlueLedWattage() + plantInfo.getRedLedWattage()}/35W"


        linearLayout.addView(plantTitleView)
        linearLayout.addView(editText)
        mainLayout.addView(plantInfoTitle)
        mainLayout.addView(linearLayout)
        mainLayout.addView(plantMoistureView)
        mainLayout.addView(powerOnLed)
    }

    private fun makeTextViewStyle(): TextView {
        var plantTitleView = TextView(this)
        plantTitleView.gravity = Gravity.LEFT
        plantTitleView.setPadding(50, 20, 0, 0)
        plantTitleView.setTextColor(Color.WHITE)
        plantTitleView.textSize = 20F
        return plantTitleView
    }

    private fun makeManualCommands() {
        var plantManualInfo = makeTextViewStyle()
        plantManualInfo.textSize = 23F
        plantManualInfo.setPadding(10, 40, 0, 0)
        plantManualInfo.text = "Manuální příkazy: "
        var relativeLayout = RelativeLayout(this)
        relativeLayout.gravity = Gravity.LEFT
        relativeLayout.setPadding(0, 50, 0, 50)
        var relativeLayout2 = RelativeLayout(this)
        relativeLayout2.gravity = Gravity.LEFT
        relativeLayout2.setPadding(0, 0, 0, 50)
        var wateringBtn = setButtonStyle("Zalít")
        var lightUpPot = setButtonStyle("Indikovat")
        var lightUpLeds = setButtonStyle("Zapnout Led")
        var lightDownLeds = setButtonStyle("Vypnout Led")
        wateringBtn.x = width / 10F
        lightUpPot.x = width / 2F + (width / 2 - width / 10F - width / 3)
        lightUpLeds.x = width / 10f
        lightDownLeds.x = width / 2F + (width / 2 - width / 10F - width / 3)
        wateringBtn.setOnClickListener {
            if (wateringOn) {
                BluetoothCommunication.getInstance().sendManualCommand("w${index + 1}0")
                wateringOn = false
            } else {
                wateringOn = true
                BluetoothCommunication.getInstance().sendManualCommand("w${index + 1}1")
            }
        }

        lightUpLeds.setOnClickListener {
            var instance = BluetoothCommunication.getInstance()
            instance.sendManualCommand(
                "l${index + 1}${instance.intToStyleString(plantInfo.getRedLedPower())}${instance.intToStyleString(
                    plantInfo.getBlueLedPower()
                )}"
            )
            Log.i(
                "App",
                "l${index + 1}${instance.intToStyleString(plantInfo.getRedLedPower())}${instance.intToStyleString(
                    plantInfo.getBlueLedPower()
                )}"
            )
        }
        lightDownLeds.setOnClickListener {
            BluetoothCommunication.getInstance().sendManualCommand("l${index + 1}000000")
        }
        lightUpPot.setOnClickListener {
            BluetoothCommunication.getInstance().sendManualCommand("p${index+1}")

        }



        mainLayout.addView(plantManualInfo)
        relativeLayout.addView(wateringBtn)
        relativeLayout.addView(lightUpPot)
        relativeLayout2.addView(lightUpLeds)
        relativeLayout2.addView(lightDownLeds)
        mainLayout.addView(relativeLayout)
        mainLayout.addView(relativeLayout2)
    }

    private fun setButtonStyle(text: String): Button {
        var btn = Button(this)
        var drawable = GradientDrawable()
        drawable.setColor(Color.DKGRAY)
        drawable.cornerRadius = 25f
        drawable.setStroke(3, Color.WHITE)
        btn.background = drawable
        btn.gravity = Gravity.CENTER
        btn.text = text
        btn.setTextColor(Color.WHITE)
        btn.width = width / 3
        btn.textSize = 18F
        return btn
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeLedSliders(maxWatage: Float, text: String, blue: Boolean) {
        var linearLayout = LinearLayout(applicationContext)
        linearLayout.gravity = Gravity.CENTER
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(10, 10, 10, 10)
        var title = makeTextViewStyle()
        title.text = text
        var seekBar = SeekBar(applicationContext)
        seekBar.setPadding(50, 10, 10, 10)
        seekBar.max = 100
        seekBar.min = 0
        if (blue) seekBar.progress = (plantInfo.getBlueLedPower())
        else seekBar.progress = (plantInfo.getRedLedPower())

        var powerText = makeTextViewStyle()
        if (blue) powerText.text =
            "Výkon: ${plantInfo.getBlueLedWattage()}/35W | ${plantInfo.getBlueLedPower()}%"
        else powerText.text =
            "Výkon: ${plantInfo.getRedLedWattage()}/30W | ${plantInfo.getRedLedPower()}%"
        powerText.gravity = Gravity.CENTER


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (blue) {
                    PlantInfoCollection.getInstance()
                        .changePotValue(index = index, powerBLueLed = progress)
                    plantInfo = PlantInfoCollection.getInstance().getPotInfo(index)
                    var sum = plantInfo.getBlueLedWattage() + plantInfo.getRedLedWattage()
                    if (sum >= 35) {
                        var progress = (35 - plantInfo.getRedLedWattage()) / 35 * 100
                        seekBar!!.progress = progress.toInt()
                        PlantInfoCollection.getInstance()
                            .changePotValue(index = index, powerBLueLed = progress.toInt())
                        plantInfo = PlantInfoCollection.getInstance().getPotInfo(index)
                    }
                    powerText.text =
                        "Výkon: ${plantInfo.getBlueLedWattage()}/35W | ${plantInfo.getBlueLedPower()}%"
                } else {
                    PlantInfoCollection.getInstance()
                        .changePotValue(index = index, powerRedLed = progress)
                    plantInfo = PlantInfoCollection.getInstance().getPotInfo(index)
                    var sum = plantInfo.getBlueLedWattage() + plantInfo.getRedLedWattage()
                    if (sum >= 35) {
                        var progress = (35 - plantInfo.getBlueLedWattage()) / 30 * 100
                        seekBar!!.progress = progress.toInt()
                        PlantInfoCollection.getInstance()
                            .changePotValue(index = index, powerRedLed = progress.toInt())
                        plantInfo = PlantInfoCollection.getInstance().getPotInfo(index)
                    }

                    powerText.text =
                        "Výkon: ${plantInfo.getRedLedWattage()}/35W | ${plantInfo.getRedLedPower()}%"

                }
                var textView = mainLayout.getChildAt(4) as TextView
                textView.text =
                    "Výkon LED: ${plantInfo.getRedLedWattage() + plantInfo.getBlueLedWattage()}/35W"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        linearLayout.addView(seekBar)
        linearLayout.addView(powerText)
        mainLayout.addView(title)
        mainLayout.addView(linearLayout)
    }

    override fun finish() {
        super.finish()
        PlantInfoCollection.getInstance().saveData()
    }
    private fun makeAutoLayout(){
        var relativeLayout = RelativeLayout(applicationContext)
        relativeLayout.gravity = Gravity.LEFT
        relativeLayout.setPadding(0,0,0,100)
        var title = makeTextViewStyle()
        title.setPadding(0,50,0,50)
        title.text = "Automatické příkazy"
        title.textSize = 23F
        title.gravity = Gravity.CENTER
        var wateringBtn = setButtonStyle("Zalévání")
        var ledButton = setButtonStyle("Osvícení")
        wateringBtn.x = width / 10F
        ledButton.x = width / 2F + (width / 2 - width / 10F - width / 3)
        wateringBtn.setOnClickListener {
            var intent = Intent(applicationContext, WateringActivity::class.java)
            intent.putExtra("index", index)
            startActivity(intent)
        }
        ledButton.setOnClickListener {
            var intent = Intent(applicationContext, AutoLedActivity::class.java)
            intent.putExtra("index", index)
            startActivity(intent)
        }




        mainLayout.addView(title)
        mainLayout.addView(relativeLayout)
        relativeLayout.addView(wateringBtn)
        relativeLayout.addView(ledButton)
    }


}