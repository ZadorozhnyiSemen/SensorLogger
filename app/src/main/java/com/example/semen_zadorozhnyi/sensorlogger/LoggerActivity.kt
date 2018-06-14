package com.example.semen_zadorozhnyi.sensorlogger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_logger.accel_x
import kotlinx.android.synthetic.main.activity_logger.accel_y
import kotlinx.android.synthetic.main.activity_logger.accel_z
import kotlinx.android.synthetic.main.activity_logger.gyro_x
import kotlinx.android.synthetic.main.activity_logger.gyro_y
import kotlinx.android.synthetic.main.activity_logger.gyro_z
import kotlinx.android.synthetic.main.activity_logger.start_button
import kotlinx.android.synthetic.main.activity_logger.stop_button
import java.io.File
import java.io.FileWriter

class LoggerActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var manageraccel: SensorManager
    private lateinit var managergyro: SensorManager
    private lateinit var writer: FileWriter
    private var inProgress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logger)

        manageraccel = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        managergyro = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stop_button.isEnabled = false

        start_button.setOnClickListener {
            start_button.isEnabled = false
            stop_button.isEnabled = true
            println(it.context.filesDir.toString())
            writer = FileWriter(File(it.context.filesDir, "sensors"))
            writer.write("sep=;\n")

            managergyro.registerListener(this@LoggerActivity, managergyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL)
            manageraccel.registerListener(this@LoggerActivity, manageraccel.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL)

            inProgress = true
        }

        stop_button.setOnClickListener {
            start_button.isEnabled = true
            stop_button.isEnabled = false

            inProgress = false
            manageraccel.flush(this@LoggerActivity)
            manageraccel.unregisterListener(this@LoggerActivity)
            managergyro.flush(this@LoggerActivity)
            managergyro.unregisterListener(this@LoggerActivity)
            try {
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (inProgress) {
            try {
                when (event?.sensor?.type) {
                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        writer.write("ACC; ${event.timestamp}\n")
                        accel_x.text = event.values[0].toString()
                        accel_y.text = event.values[1].toString()
                        accel_z.text = event.values[2].toString()
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        writer.write("GYRO; ${event.timestamp}\n")
                        gyro_x.text = event.values[0].toString()
                        gyro_y.text = event.values[1].toString()
                        gyro_z.text = event.values[2].toString()
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //noOp
    }
}
