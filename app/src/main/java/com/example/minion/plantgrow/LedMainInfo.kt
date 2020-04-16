package com.example.minion.plantgrow

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class LedMainInfo private constructor(){
    private val listOfLedInfo = ArrayList<LedInfo>(4)

    companion object {
        private var ledMainInfo: LedMainInfo? = null
        private lateinit var file: File
        fun getInstance(): LedMainInfo {
            if (ledMainInfo == null) {
                ledMainInfo = LedMainInfo()
                initFile()
                readFile()
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
    }

    fun getLedInfo(potIndex:Int) = listOfLedInfo[potIndex]



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
    fun getRedLedPower(dayIndex: Int) = getRedPercentage(dayIndex)/1000*30
    fun getBlueLedPower(dayIndex: Int) = getBluePercentage(dayIndex)/1000*35

}