package com.example.minion.plantgrow

import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class LedMainInfo private constructor() {
    private val listOfLedInfo = ArrayList<LedInfo>(4)
    private val listOfTimeInfo = ArrayList<LedInfoTime>(4)

    companion object {
        private var ledMainInfo: LedMainInfo? = null
        private lateinit var file: File
        private lateinit var timeFile: File


        fun getInstance(): LedMainInfo {
            if (ledMainInfo == null) {
                ledMainInfo = LedMainInfo()
                initFile()
                initTimeFile()
                readFile()
                readTimeFile()
            }
            return ledMainInfo!!
        }

        private fun initFile() {
            if (file.exists()) {
                return
            }
            file.createNewFile()
            val bfw = BufferedWriter(FileWriter(file))
            for (i in 1..16) {
                bfw.write("0;0;0;0;0;0;0;")
                bfw.newLine()
            }
            bfw.close()
        }

        private fun readFile() {
            if (!file.canRead()) {
                return
            }
            var sc = Scanner(file)
            for (i in 0..3) {
                ledMainInfo!!.listOfLedInfo.add(LedInfo())
                ledMainInfo!!.listOfLedInfo[i].setOnRedArray(parseLedStatus(sc))
                ledMainInfo!!.listOfLedInfo[i].setOnBlueArray(parseLedStatus(sc))
                ledMainInfo!!.listOfLedInfo[i].setOnRedPowerArray(parseLedPower(sc))
                ledMainInfo!!.listOfLedInfo[i].setOnBluePowerArray(parseLedPower(sc))
            }
            sc.close()
        }

        private fun parseLedStatus(sc: Scanner): ArrayList<Boolean> {
            var string = sc.nextLine()
            var array = ArrayList<Boolean>(7)
            for (i in 0..6) {
                if (string[i * 2] == '1') {
                    array.add(true)
                } else array.add(false)
            }
            return array
        }

        private fun parseLedPower(sc: Scanner): ArrayList<Long> {
            var arr = ArrayList<Long>(7)
            var string = sc.nextLine()
            var number = ""
            for (char in string) {
                if (char != ';') {
                    number += char
                } else {
                    arr.add(number.toLong())
                    number = ""
                }
            }
            return arr
        }


        fun setPath(pth: String) {
            file = File("$pth/led.txt")
            timeFile = File("$pth/time.txt")
        }

        private fun initTimeFile() {
            if (timeFile.exists()) {
                return
            }
            timeFile.createNewFile()
            var bfw = BufferedWriter(FileWriter(timeFile))
            for (i in 0..3) {
                bfw.write("0;0;0;0;0;0;0;")
                bfw.newLine()
            }
            bfw.close()
        }

        private fun readTimeFile() {
            var sc = Scanner(timeFile)
            for (i in 0..3) {
                ledMainInfo!!.listOfTimeInfo.add(LedInfoTime())
                var arr = parseLedPower(sc)
                for (j in 0..6) {
                    ledMainInfo!!.listOfTimeInfo[i].addTimeFromMinutes(arr[j].toInt())
                    Log.i("App", "$j")
                }
            }
            sc.close()
        }


    }

    fun save() {
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        var bfw = BufferedWriter(FileWriter(file))
        for (data in listOfLedInfo) {
            bfw.write(data.getLedStatusString(false))
            bfw.newLine()
            bfw.write(data.getLedStatusString(true))
            bfw.newLine()
            bfw.write(data.getLedPower(false))
            bfw.newLine()
            bfw.write(data.getLedPower(true))
            bfw.newLine()
        }
        bfw.close()
        saveLedTime()
    }

    private fun saveLedTime() {
        if (timeFile.exists()) {
            file.delete()
        }
        var bfw = BufferedWriter(FileWriter(timeFile))
        for (timeInfo in listOfTimeInfo) {
            for (i in 0..6) {
                bfw.write("${timeInfo.getTimeInMinutesCalculated(i)};")
            }
            bfw.newLine()
        }
        bfw.close()
    }


    fun getLedInfo(potIndex: Int) = listOfLedInfo[potIndex]
    fun getLedTimeInfo(potIndex: Int) = listOfTimeInfo[potIndex]


}

data class LedInfo(
    private var onRed: ArrayList<Boolean> = ArrayList(7),
    private var onBlue: ArrayList<Boolean> = ArrayList(7),
    private var redPowerPercentage: ArrayList<Long> = ArrayList(7),
    private var bluePowerPercentage: ArrayList<Long> = ArrayList(7)
) {
    /*
    return true if is on
     */
    fun setOnRedArray(onRed: ArrayList<Boolean>) {
        this.onRed = onRed
    }

    fun setOnBlueArray(onBlue: ArrayList<Boolean>) {
        this.onBlue = onBlue
    }

    fun setOnRedPowerArray(redPowerPercentage: ArrayList<Long>) {
        this.redPowerPercentage = redPowerPercentage
    }

    fun setOnBluePowerArray(bluePowerPercentage: ArrayList<Long>) {
        this.bluePowerPercentage = bluePowerPercentage
    }

    fun getRedState(dayIndex: Int) = onRed[dayIndex]
    fun getBlueState(dayIndex: Int) = onBlue[dayIndex]
    fun setRedState(dayIndex: Int, status: Boolean) {
        onRed[dayIndex] = status
    }

    fun setBlueState(dayIndex: Int, status: Boolean) {
        onBlue[dayIndex] = status
    }

    fun getRedPercentage(dayIndex: Int) = redPowerPercentage[dayIndex]
    fun getBluePercentage(dayIndex: Int) = bluePowerPercentage[dayIndex]
    fun setRedPercentage(dayIndex: Int, value: Long) {
        redPowerPercentage[dayIndex] = value
    }

    fun setBluePercentage(dayIndex: Int, value: Long) {
        bluePowerPercentage[dayIndex] = value
    }

    fun getLedStatusString(isBlue: Boolean): String {
        var array = if (isBlue) onBlue else onRed
        var string = ""
        for (bool in array) {
            string += if (bool) "1;" else "0;"
        }
        return string

    }

    fun getLedPower(isBlue: Boolean): String {
        var array = if (isBlue) bluePowerPercentage else redPowerPercentage
        var string = ""
        for (long in array) {
            string += "$long;"
        }
        return string
    }

    fun getRedLedPower(dayIndex: Int): Float = getRedPercentage(dayIndex).toFloat() / 1000F * 30F
    fun getBlueLedPower(dayIndex: Int): Float = getBluePercentage(dayIndex).toFloat() / 1000F * 35F
}

data class LedInfoTime(
    private var timeInHours: ArrayList<Int> = ArrayList(),
    private var timeInMinutes: ArrayList<Int> = ArrayList()
) {
    fun getTimeInHours(dayIndex: Int) = timeInHours[dayIndex]
    fun getTimeInMinutes(dayIndex: Int) = timeInMinutes[dayIndex]
    fun setTimeInHours(dayIndex: Int, value: Int) {
        timeInHours[dayIndex] = value
    }

    fun setTimeInMinutes(dayIndex: Int, value: Int) {
        timeInMinutes[dayIndex] = value
    }

    fun getTimeInMinutesCalculated(dayIndex: Int) =
        timeInMinutes[dayIndex] + timeInHours[dayIndex] * 60

    fun addTimeFromMinutes(value: Int) {
        timeInHours.add(value / 60)
        timeInMinutes.add(value % 60)
        Log.i("App", "Groot")
    }


}

