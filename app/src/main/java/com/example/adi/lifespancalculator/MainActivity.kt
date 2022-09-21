package com.example.adi.lifespancalculator

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private var dOB: TextView? = null
    private var age: TextView? = null
    private var vib: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) @RequiresApi(Build.VERSION_CODES.S) {
            vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vib = vibratorManager!!.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vib = getSystemService(VIBRATOR_SERVICE) as Vibrator?
        }
        dOB = findViewById(R.id.textViewOfDateOfBirth)
        age = findViewById(R.id.textViewOfAge)
        val ageUnit1 = findViewById<TextView>(R.id.textViewOfAgeUnit1)
        val btnDatePicker = findViewById<Button>(R.id.btnForDatePicker)
        val ageUnit2 = findViewById<TextView>(R.id.textViewOfAgeUnit2)
        val btnAgeUnit = findViewById<Button>(R.id.btnAgeUnitModifier)

        val unit = TimeUnits(ageUnit1.text.toString())
        btnAgeUnit.setOnClickListener {
            vibIt()
            unit.changeDay()
            ageUnit1.text = unit.day
            ageUnit2.text = unit.day

            if ((dOB as TextView).text != "DD/MM/YYYY") {
                setAge(ageUnit1.text.toString(), (dOB as TextView).text.toString())
            }
        }

        btnDatePicker.setOnClickListener {
            vibIt()
            clickOnDatePicker(ageUnit1.text.toString())
        }
    }

    private fun clickOnDatePicker(ageUnit: String) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this,{_, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/${selectedYear}"
            dOB?.text = selectedDate
            setAge(ageUnit, selectedDate)

        }, year, month, day).show()

    }

    private fun setAge(ageUnit: String, dOB: String) {
        val selectedDate = dOB

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val theDate = sdf.parse(selectedDate)
        val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))

        var timeSpan = 0.0

        val unit = TimeUnits(ageUnit)
        val div = unit.convVal()
        theDate?.let {
            currentDate?.let {
                val selectedDateInUnit = theDate.time / div
                val currentDateInUnit = currentDate.time / div

                timeSpan = abs(currentDateInUnit - selectedDateInUnit)
            }
        }
        age?.text = timeSpan.toLong().toString()
    }

    private fun vibIt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib?.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vib?.vibrate(60)
        }
    }
}

class TimeUnits(day: String) {
    var day: String

    init {
        this.day = day
    }

    private val days =
        arrayListOf("IN SECONDS", "IN MINUTES", "IN HOURS", "IN DAYS", "IN MONTHS", "IN YEARS")
    private val conversionValues = mapOf(
        "IN SECONDS" to 1000.0,
        "IN MINUTES" to 60000.0,
        "IN HOURS" to 3600000.0,
        "IN DAYS" to 86400000.0,
        "IN MONTHS" to 2635200000.0,
        "IN YEARS" to 31622400000.0
    )

    fun changeDay() {
        if (this.day == "IN YEARS") {
            this.day = "IN SECONDS"
        } else {
            val i = days.indexOf(this.day)
            this.day = days[i + 1]
        }
    }

    fun convVal(): Double {
        return conversionValues[this.day]!!
    }
}