package com.example.myapplication.componentes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun GraficoBarras(datos: List<Float>, etiquetas: List<String>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                val entries = datos.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value)
                }

                val dataSet = BarDataSet(entries, "Tiempo de uso (minutos)").apply {
                    color = android.graphics.Color.rgb(100, 149, 237)
                    valueTextSize = 12f
                }

                data = BarData(dataSet)
                description.isEnabled = false
                legend.isEnabled = true

                xAxis.valueFormatter = IndexAxisValueFormatter(etiquetas)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                xAxis.labelRotationAngle = -45f

                axisRight.isEnabled = false
                animateY(1000)
                invalidate()
            }
        }
    )
}