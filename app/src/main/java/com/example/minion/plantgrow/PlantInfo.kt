package com.example.minion.plantgrow

import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext


data class PlantInfo(
    var name: String,
    var typeOfWatering: String,
    var moisture: Int,
    var typeOfLight: String,
    var wateringOn: Boolean,
    var lightOn: Boolean,
    var powerRedLed: Int,
    var powerBLueLed: Int
) {

    fun getLightOnString() = if (lightOn) "zapnuto" else "vypnuto"
    fun getWateringOnString() = if (wateringOn) "zapnuto" else "vypnuto"
    fun getWateringType() = if (typeOfWatering == "auto") "automatické" else "manualni"
    fun getLightType() = if (typeOfLight == "auto") "automatické" else "manualni"
    fun getMoisturePercentage() = "$moisture%"
    fun getBlueLedPower() = powerBLueLed
    fun getRedLedPower() = powerRedLed
    fun getBlueLedWattage() = powerBLueLed / 100f * 35
    fun getRedLedWattage() = powerRedLed / 100f * 30


}

class PlantInfoCollection private constructor() {
    private val listOfPots = ArrayList<PlantInfo>(4)

    companion object {

        private lateinit var file:File
        private var plantInfoCollection: PlantInfoCollection? = null
        fun getInstance(): PlantInfoCollection {
            if (plantInfoCollection == null) {
                writeFirstInfo()
                var listOfParsedData = gatherInformationFromFile()
                plantInfoCollection = PlantInfoCollection()
                for (i in 0..3) plantInfoCollection!!.listOfPots.add(listOfParsedData[i])
            }
            return plantInfoCollection!!
        }

        private fun gatherInformationFromFile(): ArrayList<PlantInfo> {
            var plantsInfo = ArrayList<PlantInfo>()
            if (file.exists()) {
                if (file.canRead()) {
                    var sc = Scanner(file)
                    for (i in 0..3) {
                        plantsInfo.add(parseReadedString(sc.nextLine()))
                    }
                    sc.close()
                }
            }
            return plantsInfo
        }
        fun setPath(pth: String){
            file = File("$pth/txt.txt")
        }
        private fun parseReadedString(str: String): PlantInfo {

            var string = str
            Log.i("App", str)
            var index = getIndex(string)
            var name = string.take(index)
            string =  string.drop(index + 1)
            index = getIndex(string)
            var wateringType = string.take(index)
            string =  string.drop(index + 1)
            index = getIndex(string)
            var ledOn = string.take(index)
            string=  string.drop(index + 1)
            index = getIndex(string)
            var redLed = string.take(index)
            string = string.drop(index + 1)
            index = getIndex(string)
            var blueLed = string.take(index)
            var wateringOn = wateringType == "auto"
            var lightOn = ledOn == "auto"
            Log.i("App", name)
            Log.i("App", wateringType)
            Log.i("App", ledOn)
            return PlantInfo(
                name,
                wateringType,
                0,
                ledOn,
                wateringOn,
                lightOn,
                redLed.toInt(),
                blueLed.toInt()
            )

        }

        private fun getIndex(string: String): Int {
            var index = 0
            Log.i("App", string)
            while (string[index] != ';') {
                index++
            }
            return index
        }

        private fun writeFirstInfo() {
            if (file.exists()) return
            file.createNewFile()
            if (file.canWrite()) {
                var fileWriter = FileWriter(file)
                var bfw = BufferedWriter(fileWriter)
                for (i in 1..4) {
                    bfw.write("Nedefinováno;")
                    bfw.write("manu;")
                    bfw.write("manu;")
                    bfw.write("0;0;")
                    bfw.newLine()
                }
                bfw.close()
            }
        }





    }

    fun getPotInfo(index: Int) = listOfPots[index]
    fun changePotValue(
        index: Int,
        name: String = listOfPots[index].name,
        typeOfWatering: String = listOfPots[index].typeOfWatering,
        moisture: Int = listOfPots[index].moisture,
        typeOfLight: String = listOfPots[index].typeOfLight,
        wateringOn: Boolean = listOfPots[index].wateringOn,
        lightOn: Boolean = listOfPots[index].lightOn,
        powerRedLed: Int = listOfPots[index].powerRedLed,
        powerBLueLed: Int = listOfPots[index].powerBLueLed

    ) {
        listOfPots[index].name = name
        listOfPots[index].typeOfLight = typeOfLight
        listOfPots[index].wateringOn = wateringOn
        listOfPots[index].lightOn = lightOn
        listOfPots[index].moisture = moisture
        listOfPots[index].typeOfWatering = typeOfWatering
        listOfPots[index].powerRedLed = powerRedLed
        listOfPots[index].powerBLueLed = powerBLueLed
    }
    fun saveData(){
        if (file.canWrite()){
            file.delete()
            file.createNewFile()
            val bfw = BufferedWriter(FileWriter(file))
            for (potInfo in listOfPots){
            bfw.write("${potInfo.name};${potInfo.typeOfWatering};${potInfo.typeOfLight};${potInfo.getRedLedPower()};${potInfo.getBlueLedPower()};")
                Log.i("App", "${potInfo.name};${potInfo.typeOfWatering};${potInfo.typeOfLight};${potInfo.getRedLedPower()};${potInfo.getBlueLedPower()};")
                bfw.newLine()
            }
            bfw.close()
        }
    }
}