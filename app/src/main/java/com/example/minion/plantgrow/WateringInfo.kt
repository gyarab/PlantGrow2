package com.example.minion.plantgrow

import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class WateringMainInfo private constructor() {
    private val listOfWateringInfo = ArrayList<WateringInfo>(4)


    companion object {
        private lateinit var file: File
        private var wateringLedInfo: WateringMainInfo? = null
        fun getInstance(): WateringMainInfo {
            if (wateringLedInfo == null) {
                wateringLedInfo = WateringMainInfo()
                saveFirstWateringDataToFile()
                for (i in 0..3) {
                    wateringLedInfo!!.listOfWateringInfo.add(WateringInfo(ArrayList<Boolean>(7), 0))
                }

            }
            parseWateringData()
            return wateringLedInfo!!
        }

        private fun saveFirstWateringDataToFile() {
            if (!file.exists()) {
                file.createNewFile()
            } else {
                return
            }

            if (file.canWrite()) {

                var bfw = BufferedWriter(FileWriter(file))
                for (i in 1..4) {
                    bfw.write("0;0;0;0;0;0;0;50")
                    bfw.newLine()

                }
                bfw.close()
            }
        }

        private fun parseWateringData() {
            if (!file.canRead()) {
                return
            }
            Log.i("App", "ahoj")
            var sc = Scanner(file);
            for (i in 0..3) {
                var readedString = sc.nextLine()
                Log.i("App", readedString)
                for (j in 0..6) {
                    var wateringOn = false
                    if (readedString[2 * j] == '1') {
                        wateringOn = true
                    }
                    wateringLedInfo!!.listOfWateringInfo[i].addDayWateringState(wateringOn)
                }
                readedString = readedString.drop(14)
                wateringLedInfo!!.listOfWateringInfo[i].setLitres(readedString.toLong())
            }
            sc.close()
        }


        fun setPath(pth: String) {
            file = File("$pth/watering.txt")
        }
    }

    fun saveWateringData() {
        if (file.delete()) {
            file.createNewFile()
        }
        var bfw = BufferedWriter(FileWriter(file))
        for (i in 0..3) {
            var stringToWrite = ""
            for (j in 0..6) {
                if (listOfWateringInfo[i].getDayWateringState(j)) {
                    stringToWrite += "1;"
                } else {
                    stringToWrite += "0;"
                }
            }
            stringToWrite += listOfWateringInfo[i].getLitres()
            bfw.write(stringToWrite)
            bfw.newLine()
        }
        bfw.close()
    }

    fun getWateringState(potIndex: Int, dayIndex: Int) =
        listOfWateringInfo[potIndex - 1].getDayWateringState(dayIndex)

    fun setWateringState(potIndex: Int, dayIndex: Int, value: Boolean) {
        listOfWateringInfo[potIndex - 1].setDayWateringState(dayIndex, value)
    }
    fun getWaterVolume(potIndex: Int) = listOfWateringInfo.get(potIndex-1).getLitres()
    fun setWaterVolume(potIndex: Int, waterVolume:Long){
        listOfWateringInfo[potIndex-1].setLitres(waterVolume)
    }
}

private data class WateringInfo(
    private var on: ArrayList<Boolean>,
    private var milliLitres: Long = 0
) {
    fun getDayWateringState(day: Int) = on[day]
    fun setDayWateringState(day: Int, value: Boolean) {
        on[day] = value
    }

    fun addDayWateringState(value: Boolean) {
        on.add(value)
    }

    fun getLitres() = milliLitres
    fun setLitres(value: Long) {
        milliLitres = value
    }


}


