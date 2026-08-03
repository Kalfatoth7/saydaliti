package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Pharmacy
import com.example.ui.theme.*

@Composable
fun PharmacyComparisonChart(pharmacies: List<Pharmacy>, modifier: Modifier = Modifier) {
    val activePharmacies = pharmacies.filter { it.id != "central_warehouse" }
    val maxSales = (activePharmacies.maxOfOrNull { it.salesToday } ?: 1.0).coerceAtLeast(1.0)

    val colors = listOf(Pharmacy1Color, Pharmacy2Color, Pharmacy3Color, Pharmacy4Color)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "مقارنة مبيعات الصيدليات الأربع اليوم",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val barWidth = size.width / (activePharmacies.size * 2f)
                val spacing = size.width / activePharmacies.size

                activePharmacies.forEachIndexed { index, pharmacy ->
                    val barHeight = ((pharmacy.salesToday / maxSales) * (size.height - 30.dp.toPx())).toFloat()
                    val xPos = index * spacing + spacing / 4f
                    val yPos = size.height - barHeight - 20.dp.toPx()

                    val barColor = colors.getOrElse(index) { MedicalTealPrimary }

                    // Draw Bar
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(xPos, yPos),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                activePharmacies.forEachIndexed { index, pharmacy ->
                    val color = colors.getOrElse(index) { MedicalTealPrimary }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "صيدلية ${index + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${pharmacy.salesToday.toInt()} ر.س",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueTrendLineChart(modifier: Modifier = Modifier) {
    val trendData = listOf(32000f, 38000f, 35000f, 42000f, 49000f, 46000f, 53900f)
    val days = listOf("الخميس", "الجمعة", "السبت", "الأحد", "الإثنين", "الثلاثاء", "اليوم")
    val maxVal = trendData.maxOrNull() ?: 1f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "اتجاه الإيرادات خلال 7 أيام",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedicalTealContainer
                ) {
                    Text(
                        text = "+12% هذا الأسبوع",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTealOnContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val lineColor = MedicalTealPrimary
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val stepX = size.width / (trendData.size - 1)
                val path = Path()

                trendData.forEachIndexed { i, valPoint ->
                    val x = i * stepX
                    val y = size.height - ((valPoint / maxVal) * (size.height - 20.dp.toPx()))
                    if (i == 0) {
                        path.moveTo(x, y)
                    } else {
                        val prevX = (i - 1) * stepX
                        val prevY = size.height - ((trendData[i - 1] / maxVal) * (size.height - 20.dp.toPx()))
                        val controlX = (prevX + x) / 2f
                        path.cubicTo(controlX, prevY, controlX, y, x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Fill gradient area below line
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                    )
                )

                // Draw dots
                trendData.forEachIndexed { i, valPoint ->
                    val x = i * stepX
                    val y = size.height - ((valPoint / maxVal) * (size.height - 20.dp.toPx()))
                    drawCircle(color = lineColor, radius = 5.dp.toPx(), center = Offset(x, y))
                    drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(x, y))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
