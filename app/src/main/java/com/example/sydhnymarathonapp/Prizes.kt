package com.example.sydhnymarathonapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sydhnymarathonapp.R
import com.example.sydhnymarathonapp.RotateListener
import com.example.sydhnymarathonapp.Roulette
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.util.Log


class Prizes: AppCompatActivity() {

    private lateinit var roulette: Roulette
    private lateinit var rotateResultTv: TextView
//    private lateinit var rouletteSizeTv: TextView
//    private lateinit var sizePlusBtn: Button
//    private lateinit var sizeMinusBtn: Button
    private lateinit var rotateBtn: Button

    private val rouletteData = listOf("1", "2", "3", "4", "5", "6", "7", "8")

    private var rotationResult: String? = null
    private var resultText: String? = null

    @SuppressLint("SetTextI18n")
    private val rouletteListener = object : RotateListener {
        override fun onRotateStart() {
            rotateResultTv.text = "Result : "
        }

        override fun onRotateEnd(result: String) {
            rotateResultTv.text = "Result : $result"

            rotationResult = result

            println(rotationResult)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prizes)

        // Initialize views
        roulette = findViewById(R.id.roulette)
        rotateResultTv = findViewById(R.id.rotate_result_tv)
//        rouletteSizeTv = findViewById(R.id.roulette_size_tv)
//        sizePlusBtn = findViewById(R.id.sizePlusBtn)
//        sizeMinusBtn = findViewById(R.id.sizeMinusBtn)
        rotateBtn = findViewById(R.id.rotate_btn)

        val buttonScannerBack: Button = findViewById(R.id.button3)

        PointsManager.updateActionBarPoints(this)

        buttonScannerBack.setOnClickListener {
            // Создаем Intent для перехода на DashboardActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        setupView()
    }

    private fun setupView() {
        roulette.apply {
            rouletteSize = 8
            setRouletteDataList(rouletteData)
        }
        PointsManager.updateActionBarPoints(this) // added Points display

//        sizePlusBtn.setOnClickListener { plusRouletteSize() }
//        sizeMinusBtn.setOnClickListener { minusRouletteSize() }
        rotateBtn.setOnClickListener {
            if (PointsManager.points < 20) {
                Toast.makeText(this, "Not enough points!", Toast.LENGTH_SHORT).show()
            }
            else {
                rotateRoulette()
            }
        }
    }

    private fun rotateRoulette() {
        PointsManager.subPoints(20)
        PointsManager.updateActionBarPoints(this)
        val toDegrees = (2000..10000).random().toFloat()
        roulette.rotateRoulette(toDegrees, 4000, rouletteListener)
    }

//    private fun plusRouletteSize() {
//        var rouletteSize = roulette.rouletteSize
//        if (rouletteSize == Roulette.ROULETTE_MAX_SIZE) return
//
//        roulette.rouletteSize = ++rouletteSize
//        rouletteSizeTv.text = rouletteSize.toString()
//    }
//
//    private fun minusRouletteSize() {
//        if (roulette.isRotate) return
//
//        var rouletteSize = roulette.rouletteSize
//        if (rouletteSize == Roulette.ROULETTE_MIN_SIZE) return
//
//        roulette.rouletteSize = --rouletteSize
//        rouletteSizeTv.text = rouletteSize.toString()
//    }

}
