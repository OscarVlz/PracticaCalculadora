package com.example.calculadoraapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true
    private lateinit var workingTV: TextView
    private lateinit var resultTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        workingTV = findViewById(R.id.workingsTV)
        resultTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workingTV.append(view.text)

                canAddDecimal = false
            } else
                workingTV.append(view.text)

            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        workingTV.text = ""
        resultTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = workingTV.length()
        if (length > 0)
            workingTV.text = workingTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View) {
        resultTV.text = calculateResults()
    }



    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        if (nextDigit != 0f) {
                            newList.add(prevDigit / nextDigit)
                            restartIndex = i + 1
                        } else {
                            // Manejar la división por cero
                            workingTV.text = ""
                            resultTV.text = "Error"
                            return mutableListOf()
                        }
                    }
                    '√' -> {
                        if (prevDigit >= 0) {
                            newList.add(sqrt(prevDigit.toDouble()).toFloat())
                        } else {
                            // Manejar la raíz cuadrada de un número negativo
                            workingTV.text = ""
                            resultTV.text = "Error: Raíz cuadrada de un número negativo"
                            return mutableListOf()
                        }
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }



    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }

                when (character) {
                    '√' -> {
                        list.add('√')
                    }
                    else -> {
                        list.add(character)
                    }
                }
            }
        }

        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }

        return list
    }

    fun squareRootAction(view: View) {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isNotEmpty()) {
            // Agregar la raíz cuadrada al final de la expresión
            digitsOperators.add('√')
            // Realizar los cálculos
            val result = calculateResults()
            if (result.isNotEmpty()) {
                workingTV.text = result
                canAddOperation = true
                canAddDecimal = true
            }
        } else {
            // Manejar el caso donde no hay dígitos para calcular la raíz cuadrada
            workingTV.text = ""
            resultTV.text = "Error: No hay números para calcular la raíz cuadrada"
        }
    }
}
