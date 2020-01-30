 package converter

import java.util.Scanner

enum class Measurement(
    var plural: String = "???",
    var singular: String = "???",
    var short: String = "???",
    val midVal: Double = 1.0,
    var to: Double = 0.0
) {
    Millimeters("millimeters", "millimeter", "mm", 0.001),
    Centimeters("centimeters", "centimeter", "cm", 0.01),
    Inches("inches", "inch", "in", 0.0254),
    Feet("feet", "foot", "ft", 0.3048),
    Yards("yards", "yard", "yd", 0.9144),
    Meters("meters", "meter", "m", 1.0),
    Kilometers("kilometers", "kilometer", "km", 1000.0),
    Miles("miles", "mile", "mi", 1609.35),
    Milligram("milligrams", "milligram", "mg", 0.001),
    Gram("grams", "gram", "g", 1.0),
    Ounce("ounces", "ounce", "oz", 28.3495),
    Pound("pounds", "pound", "lb", 453.592),
    Kilogram("kilograms", "kilogram", "kg", 1000.0),
    Celsius("degrees Celsius", "degree Celsius", "dc", -273.15),
    Fahrenheit("degrees Fahrenheit", "degree Fahrenheit", "df", -459.67),
    Kelvin("Kelvins", "Kelvin", "K", 0.0),
    MeasurementError;
    companion object {
        fun areEqual(
            m1: String,
            m2: Measurement
        ): Boolean {
            return when {
                m2.ordinal in 13..14 || "degree" in m1 -> {
                    when (m1.toLowerCase()) {
                        in m2.plural.toLowerCase(),
                        in m2.singular.toLowerCase(),
                        in m2.short -> true
                        else -> false
                    }
                }
                else -> {
                    when (m1.toLowerCase()) {
                        m2.plural.toLowerCase(),
                        m2.singular.toLowerCase(),
                        m2.short.toLowerCase() -> true
                        else -> false
                    }
                }
            }
        }
        fun setMeasurement(measurement: String): Measurement {
            for (m2 in values())
                if (areEqual(measurement, m2)) return m2
            return MeasurementError
        }
        fun inRange(m1: Measurement, m2: Measurement): Boolean {
            return m1.ordinal in 0..7 && m2.ordinal in 0..7 ||
                    m1.ordinal in 7..12 && m2.ordinal in 7..12 ||
                    m1.ordinal in 13..15 && m2.ordinal in 13..15
        }
        fun getMeasurement(m: Measurement): String {
            return when (m.to) {
                1.0 -> m.singular
                else -> m.plural
            }
        }
    }
    fun setValue(value: Double): Measurement {
        this.to = value
        return this
    }
    fun getResult(
        m2: Measurement
    ): String {
        return when {
            this == MeasurementError ->
                "Conversion from ${this.plural} to ${m2.plural} is impossible"
            m2.to < 0 && this.ordinal !in 13..15 && m2.ordinal !in 13..15 -> {
                when (this.ordinal) {
                    in 0..7 -> "Length shouldn't be negative"
                    in 7..12 -> "Weight shouldn't be negative"
                    else -> ""
                }
            }
            m2 == MeasurementError || !inRange(this, m2) -> {
                "Conversion from ${this.plural} to ${m2.plural} is impossible"
            }
            this.to < 0 && this.ordinal !in 13..15 -> {
                when (this.ordinal) {
                    in 0..7 -> "Length shouldn't be negative"
                    in 7..12 -> "Weight shouldn't be negative"
                    else -> ""
                }
            }
            else ->
                "${this.to} ${getMeasurement(this)} is ${m2.to} ${getMeasurement(m2)}"
        }
    }
    fun convertTo(measurement: String): String {
        val m = setMeasurement(measurement)
        if (inRange(this, m)) {
            val value = when (this) {
                Celsius -> {
                    val cToK = this.to - this.midVal
                    val cToF = cToK * 9 / 5 + m.midVal
                    when (m) {
                        Fahrenheit -> cToF
                        Kelvin -> cToK
                        else -> this.to
                    }
                }
                Fahrenheit -> {
                    val fTok = (this.to - this.midVal) * 5 / 9
                    val ftoC = fTok + m.midVal
                    when (m) {
                        Celsius -> ftoC
                        Kelvin -> fTok
                        else -> this.to
                    }
                }
                Kelvin -> {
                    when (m) {
                        Celsius -> this.to + m.midVal
                        Fahrenheit -> this.to * 9/5 + m.midVal
                        else -> this.to
                    }
                }
                else -> this.to * (this.midVal / m.midVal)
            }
            m.setValue(value)
        }
        return this.getResult(m)
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    do {
        var out: Boolean
        do {
            print("Enter what you want to convert (or exit): ")
            val input = if (scanner.hasNextLine()) scanner.nextLine() else ""

            val textScanner = Scanner(input)
            var text = textScanner.next()
            while (textScanner.hasNext())
                text += " ${textScanner.next()}"
            out = text == "exit"

            val sc = Scanner(input)
            val isDouble = sc.hasNextDouble()
            val number = if (isDouble) sc.next() else ""
            var m1 = if (sc.hasNext()) sc.next() else ""
            m1 = if ((m1.toLowerCase() == "degrees" || m1.toLowerCase() == "degree") && sc.hasNext()) "$m1 ${sc.next()}" else m1
            val someValue = if (sc.hasNext()) sc.next() else ""
            var m2 = if (sc.hasNext()) sc.next() else ""
            m2 = if ((m2.toLowerCase() == "degrees" || m2.toLowerCase() == "degree") && sc.hasNext()) "$m2 ${sc.next()}" else m2
            val value = "$number $m1 $someValue $m2"

            if (isDouble && value == text) {
                println(
                    Measurement
                        .setMeasurement(m1)
                        .setValue(number.toDouble())
                        .convertTo(m2)
                )
            } else if (!out) println("Parse error")
        } while (isDouble && value == text)
    } while (!out)
}
