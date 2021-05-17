package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

fun main() {
    var sourceBase: BigDecimal
    var targetBase: BigDecimal
    levelOne@ while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val inputRaw = readLine()!!
        val input = inputRaw.split(" ")
        if (inputRaw == "/exit") return
        sourceBase = input[0].toBigDecimal()
        targetBase = input[1].toBigDecimal()
        levelTwo@ while (true) {
            println("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
            val input2Raw = readLine()!!
            if (input2Raw == "/back") continue@levelOne

            val input2 = input2Raw.split('.')
            var intPart = ""
            var fracPart = ""

            when  {
                input2Raw.contains('.') -> {
                    intPart = input2[0]
                    fracPart = input2[1]

                    val intPartConverted = xBaseToBaseX(intPart, sourceBase, targetBase)
                    val fracPartConverted = if (fracPart == "0") "00000" else xBaseToBaseXFrac(fracPart, sourceBase, targetBase)

                    val finalConvertedNumber = "$intPartConverted.$fracPartConverted"
                    println("Conversion result: $finalConvertedNumber\n")
                }
                else -> {
                    println("Conversion result: ${convertFromBigI(input2Raw, sourceBase, targetBase)}")

                }
            }
        }
    }
}

fun convertFromBigI(input2Raw: String, sourceBase: BigDecimal, targetBase: BigDecimal):String {         // This function only if the number is a whole
    val input2RawInBase10 = input2Raw.toBigInteger(sourceBase.toInt())
    return input2RawInBase10.toString(targetBase.toInt())
}

fun xBaseToBaseX(intPart: String, sourceBase: BigDecimal, targetBase: BigDecimal):String {              // This function converts the integer part
    val intPartInBase10 = intPart.toBigInteger(sourceBase.toInt())
    return intPartInBase10.toString(targetBase.toInt()) // it returns the integer part converted to target base
}

fun xBaseToBaseXFrac(fracPart: String, sourceBase: BigDecimal, targetBase: BigDecimal): String {        // This function converts the fractional part
    val fracDigitsDecodedToBase10Stage1 = mutableListOf<BigInteger>()
    val fracPartDecodedToBase10Stage2 = mutableListOf<BigDecimal>()

    var powCaunter = 0
    for (character in fracPart) {
        fracDigitsDecodedToBase10Stage1.add(character.toString().toBigInteger(sourceBase.toInt()))
    }

    for (digits in fracDigitsDecodedToBase10Stage1) {
        powCaunter --
        fracPartDecodedToBase10Stage2.add(digits.toBigDecimal().multiply(sourceBase.pow(powCaunter, MathContext.DECIMAL64)))
    }
    var fracPartDecodedToBase10 = BigDecimal(0.0)

    for (number in fracPartDecodedToBase10Stage2) {
        fracPartDecodedToBase10 = fracPartDecodedToBase10.add(number)
    }

    fracPartDecodedToBase10 = fracPartDecodedToBase10.setScale(5, RoundingMode.CEILING)

    // The function converted the frac part to base 10 until this part. From here it decodes it to targetBase

    val fracPartToBaseX = mutableListOf<BigInteger>()
    var temporalResult = fracPartDecodedToBase10
    val fracPartToBaseXConverted = mutableListOf<String>()
    repeat(5) {
        temporalResult = temporalResult.multiply(targetBase)
        val wholepart = temporalResult.toString().split('.')[0]
        fracPartToBaseX.add(wholepart.toBigInteger())
        temporalResult = "0.${temporalResult.toString().split('.')[1]}".toBigDecimal()
    }

    for (digits in fracPartToBaseX) {
        fracPartToBaseXConverted.add(digits.toString(targetBase.toInt()))
    }

    return fracPartToBaseXConverted.joinToString("")
}
