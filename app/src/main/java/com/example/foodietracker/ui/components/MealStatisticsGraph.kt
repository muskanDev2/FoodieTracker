package com.example.foodietracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodietracker.data.model.Meal
import java.util.*

@Composable
fun MealStatisticsGraph(
    meals: List<Meal>,
    modifier: Modifier = Modifier
) {
    if (meals.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "No meal data available",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    // Group meals by date
    val mealsByDate = meals.groupBy { 
        Calendar.getInstance().apply { 
            time = it.timestamp 
        }.get(Calendar.DAY_OF_WEEK)
    }

    // Calculate max meals per day for scaling
    val maxMeals = mealsByDate.values.maxOfOrNull { it.size } ?: 0

    val textMeasurer = rememberTextMeasurer()
    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / 7 // 7 days in a week

        // Draw bars for each day
        for (day in 1..7) {
            val mealsForDay = mealsByDate[day]?.size ?: 0
            val barHeight = if (maxMeals > 0) {
                (mealsForDay.toFloat() / maxMeals) * height
            } else 0f

            val x = (day - 1) * barWidth
            val y = height - barHeight

            // Draw bar
            drawRect(
                color = MaterialTheme.colorScheme.primary,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barHeight)
            )

            // Draw day label
            val label = dayLabels[day - 1]
            val textStyle = TextStyle(
                color = Color.Black,
                fontSize = 12.sp
            )
            val textLayoutResult = textMeasurer.measure(label, textStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                topLeft = Offset(
                    x = x + (barWidth - textLayoutResult.size.width) / 2,
                    y = height + 4.dp.toPx()
                ),
                style = textStyle
            )

            // Draw meal count
            if (mealsForDay > 0) {
                val countText = mealsForDay.toString()
                val countTextLayoutResult = textMeasurer.measure(countText, textStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text = countText,
                    topLeft = Offset(
                        x = x + (barWidth - countTextLayoutResult.size.width) / 2,
                        y = y - countTextLayoutResult.size.height - 4.dp.toPx()
                    ),
                    style = textStyle
                )
            }
        }
    }
} 