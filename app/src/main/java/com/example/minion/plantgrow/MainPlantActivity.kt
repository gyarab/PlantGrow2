package com.example.minion.plantgrow

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main_plant.*
import kotlinx.android.synthetic.main.app_bar_main_plant.*

class MainPlantActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var width = 0
    private var hegiht = 0
    private lateinit var mainLayout: RelativeLayout
    private var displayMetrics = DisplayMetrics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_plant)
        setSupportActionBar(toolbar)
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mainLayout = findViewById(R.id.over_view)
        width = displayMetrics.widthPixels
        hegiht = displayMetrics.heightPixels
        PlantInfoCollection.setPath(applicationContext.filesDir.path)
        WateringMainInfo.setPath((applicationContext.filesDir.path))
        LedMainInfo.setPath(applicationContext.filesDir.path)
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, 0)
        BluetoothCommunication.getInstance().connect()

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        makeLayout()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_plant, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var intent = Intent(applicationContext, PlantActivity::class.java)

        when (item.itemId) {
            R.id.nav_plant1 -> {
                intent.putExtra("index", 1)
                startActivity(intent)
            }
            R.id.nav_plant2 -> {
                intent.putExtra("index", 2)
                startActivity(intent)
            }
            R.id.nav_plant3 -> {
                intent.putExtra("index", 3)
                startActivity(intent)
            }
            R.id.nav_plant4 -> {
                intent.putExtra("index", 4)
                startActivity(intent)
            }
            R.id.nav_overview -> {

            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeLayout() {
        var title = TextView(applicationContext)
        title.text = "Přehled"
        title.gravity = Gravity.CENTER_HORIZONTAL
        title.width = width
        title.textSize = 25F
        title.y = 200f
        title.setTextColor(Color.BLACK)
        mainLayout.addView(title)
        for (i in 0..3) {
            mainLayout.addView(makePlant(i))
        }

    }

    private fun makePlant(index: Int): LinearLayout {
        var linearLayout = LinearLayout(applicationContext)
        linearLayout.orientation = LinearLayout.VERTICAL
        var drawable = GradientDrawable()
        drawable.setStroke(3, Color.BLACK)
        drawable.cornerRadius = 25f
        linearLayout.background = drawable
        linearLayout.gravity = Gravity.CENTER_HORIZONTAL
        linearLayout.setPadding(0, 20, 0, 20)
        var plant = TextView(applicationContext)
        plant.text = "Rostlina ${index+1}"
        plant.textSize = 23f
        plant.gravity = Gravity.CENTER
        plant.width = width
        plant.setTextColor(Color.BLACK)

        var name = TextView(applicationContext)
        var plantInfo = PlantInfoCollection.getInstance().getPotInfo(index)
        name.text = "Jméno: ${plantInfo.name}"
        name.textSize = 20f
        name.gravity = Gravity.CENTER
        name.width = width
        name.setTextColor(Color.BLACK)

        var settingsWater = TextView(applicationContext)
        settingsWater.text = "Nastavení zalévání: ${plantInfo.getWateringType()}"
        settingsWater.textSize = 18f
        settingsWater.gravity = Gravity.CENTER
        settingsWater.width = width
        settingsWater.setTextColor(Color.BLACK)

        var settingsLed = TextView(applicationContext)
        settingsLed.text = "Nastavení svícení: ${plantInfo.getLightType()}"
        settingsLed.textSize = 18f
        settingsLed.gravity = Gravity.CENTER
        settingsLed.width = width
        settingsLed.setTextColor(Color.BLACK)
        linearLayout.addView(plant)
        linearLayout.addView(name)
        linearLayout.addView(settingsLed)
        linearLayout.addView(settingsWater)

        linearLayout.y = hegiht / 6 + hegiht/6*index.toFloat()
        return linearLayout

    }
}
