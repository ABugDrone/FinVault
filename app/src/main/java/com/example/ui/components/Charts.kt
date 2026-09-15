package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.security.CryptoManager
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.VaultDarkBg
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

val CategoryPalette = listOf(
    EmeraldIncome,
    CyanAccent,
    GoldAccent,
    PurpleAccent,
    RoseExpense,
    AmberAccent,
    Color(0xFF38BDF8),
    Color(0xFFF472B6),
    Color(0xFF4ADE80),
    Color(0xFFA78BFA),
    Color(0xFFFB923C)
)

data class SliceData(
    val label: String,
    val value: Double,
    val percentage: Float,
    val color: Color
)

@Composable
fun FinVaultDonutChart(
    transactions: List<TransactionEntity>,
    typeFilter: TransactionType = TransactionType.EXPENSE,
    isPrivacyMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val filtered = transactions.filter { it.type == typeFilter }
    val totalAmount = filtered.sumOf { it.amount }

    val slices = remember(filtered, totalAmount) {
        if (totalAmount <= 0.0) {
            emptyList()
        } else {
            val grouped = filtered.groupBy { it.category }
                .mapValues { it.value.sumOf { tx -> tx.amount } }
                .toList()
                .sortedByDescending { it.second }

            grouped.mapIndexed { index, (cat, amt) ->
                SliceData(
                    label = cat,
                    value = amt,
                    percentage = (amt / totalAmount).toFloat(),
                    color = CategoryPalette[index % CategoryPalette.size]
                )
            }
        }
    }

    var selectedSliceIndex by remember { mutableIntStateOf(-1) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(slices) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(700))
    }

    if (slices.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No ${typeFilter.name.lowercase()} records in this timeframe",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(216.dp)
                    .testTag("pie_chart_canvas")
                    .pointerInput(slices) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touchVec = tapOffset - center
                            val distance = touchVec.getDistance()
                            val outerRadius = minOf(size.width, size.height).toFloat() / 2f
                            val innerRadius = outerRadius * 0.65f

                            if (distance in innerRadius..outerRadius) {
                                var angle = Math.toDegrees(atan2(touchVec.y.toDouble(), touchVec.x.toDouble())).toFloat()
                                if (angle < 0) angle += 360f

                                var currentAngle = 0f
                                var tappedIndex = -1
                                for (i in slices.indices) {
                                    val sweep = slices[i].percentage * 360f
                                    if (angle in currentAngle..(currentAngle + sweep)) {
                                        tappedIndex = i
                                        break
                                    }
                                    currentAngle += sweep
                                }
                                selectedSliceIndex = if (selectedSliceIndex == tappedIndex) -1 else tappedIndex
                            } else {
                                selectedSliceIndex = -1
                            }
                        }
                    }
            ) {
                val strokeWidth = 32.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val radius = diameter / 2f
                val topLeft = Offset(center.x - radius, center.y - radius)
                val arcSize = Size(diameter, diameter)

                var startAngle = -90f
                slices.forEachIndexed { index, slice ->
                    val sweepAngle = slice.percentage * 360f * animationProgress.value
                    val isSelected = index == selectedSliceIndex
                    val currentStroke = if (isSelected) strokeWidth * 1.3f else strokeWidth

                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.coerceAtLeast(1.5f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(
                            width = currentStroke,
                            cap = StrokeCap.Round
                        )
                    )
                    startAngle += sweepAngle
                }
            }

            // Center readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val activeSlice = if (selectedSliceIndex in slices.indices) slices[selectedSliceIndex] else null
                Text(
                    text = activeSlice?.label ?: if (typeFilter == TransactionType.INCOME) "Earnings" else "Expenses",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = CryptoManager.formatMaskedAmount(
                        activeSlice?.value ?: totalAmount,
                        isPrivacyMode
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeSlice?.color ?: MaterialTheme.colorScheme.onSurface
                )
                if (activeSlice != null) {
                    Text(
                        text = "${String.format("%.1f", activeSlice.percentage * 100)}%",
                        fontSize = 12.sp,
                        color = activeSlice.color,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Categorized Legend
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            slices.take(6).forEachIndexed { index, slice ->
                val isSelected = index == selectedSliceIndex
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) slice.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable {
                            selectedSliceIndex = if (selectedSliceIndex == index) -1 else index
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(slice.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slice.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${(slice.percentage * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

data class BarGroup(
    val label: String,
    val income: Double,
    val expense: Double
)

@Composable
fun FinVaultBarChart(
    barGroups: List<BarGroup>,
    isPrivacyMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (barGroups.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No activity data for bar chart", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val maxVal = remember(barGroups) {
        val highest = barGroups.maxOfOrNull { maxOf(it.income, it.expense) } ?: 1.0
        if (highest <= 0.0) 1.0 else highest * 1.15
    }

    var selectedGroupIndex by remember { mutableIntStateOf(-1) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(barGroups) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(600))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bar_chart_section")
    ) {
        // Legend & Selection readout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldIncome))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Income", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RoseExpense))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Expense", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (selectedGroupIndex in barGroups.indices) {
                val group = barGroups[selectedGroupIndex]
                Text(
                    text = "${group.label}: +${CryptoManager.formatMaskedAmount(group.income, isPrivacyMode)} / -${CryptoManager.formatMaskedAmount(group.expense, isPrivacyMode)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 12.dp)
                .pointerInput(barGroups) {
                    detectTapGestures { offset ->
                        val slotWidth = size.width / barGroups.size
                        val tappedIdx = (offset.x / slotWidth).toInt().coerceIn(0, barGroups.lastIndex)
                        selectedGroupIndex = if (selectedGroupIndex == tappedIdx) -1 else tappedIdx
                    }
                }
        ) {
            val width = size.width
            val height = size.height - 30.dp.toPx()
            val groupCount = barGroups.size
            val slotWidth = width / groupCount
            val barWidth = (slotWidth * 0.32f).coerceAtMost(22.dp.toPx())
            val gap = 4.dp.toPx()

            // Draw baseline
            drawLine(
                color = Color.Gray.copy(alpha = 0.25f),
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )

            barGroups.forEachIndexed { i, group ->
                val centerX = slotWidth * i + slotWidth / 2f
                val isSelected = i == selectedGroupIndex

                // Highlight slot background if selected
                if (isSelected) {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.05f),
                        topLeft = Offset(slotWidth * i + 2.dp.toPx(), 0f),
                        size = Size(slotWidth - 4.dp.toPx(), height + 24.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                    )
                }

                // Income bar
                val incHeight = ((group.income / maxVal) * height * animProgress.value).toFloat()
                if (incHeight > 0f) {
                    drawRoundRect(
                        color = EmeraldIncome,
                        topLeft = Offset(centerX - barWidth - (gap / 2f), height - incHeight),
                        size = Size(barWidth, incHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }

                // Expense bar
                val expHeight = ((group.expense / maxVal) * height * animProgress.value).toFloat()
                if (expHeight > 0f) {
                    drawRoundRect(
                        color = RoseExpense,
                        topLeft = Offset(centerX + (gap / 2f), height - expHeight),
                        size = Size(barWidth, expHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }
        }

        // X-Axis Labels Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            barGroups.forEachIndexed { i, group ->
                Text(
                    text = group.label,
                    fontSize = 11.sp,
                    color = if (i == selectedGroupIndex) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (i == selectedGroupIndex) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

data class LinePoint(
    val label: String,
    val value: Double
)

@Composable
fun FinVaultLineChart(
    points: List<LinePoint>,
    isPrivacyMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Need at least 2 data points for line trend", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val minVal = points.minOf { it.value }
    val maxVal = points.maxOf { it.value }
    val range = if (maxVal == minVal) 1.0 else (maxVal - minVal)

    var selectedPointIndex by remember { mutableIntStateOf(-1) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(points) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(750))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("line_chart_section")
    ) {
        // Top label & value indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Net Balance Trajectory",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val currentPoint = if (selectedPointIndex in points.indices) points[selectedPointIndex] else points.last()
            Text(
                text = "${currentPoint.label}: ${CryptoManager.formatMaskedAmount(currentPoint.value, isPrivacyMode)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentPoint.value >= 0) EmeraldIncome else RoseExpense
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        val stepX = size.width / (points.size - 1)
                        val idx = ((offset.x + stepX / 2f) / stepX).toInt().coerceIn(0, points.lastIndex)
                        selectedPointIndex = if (selectedPointIndex == idx) -1 else idx
                    }
                }
        ) {
            val width = size.width
            val height = size.height - 24.dp.toPx()
            val stepX = width / (points.size - 1)

            val path = Path()
            val fillPath = Path()

            val coordinates = points.mapIndexed { i, p ->
                val x = i * stepX
                val normalizedY = ((p.value - minVal) / range).toFloat()
                val y = height - (normalizedY * height * progress.value)
                Offset(x, y)
            }

            if (coordinates.isNotEmpty()) {
                path.moveTo(coordinates[0].x, coordinates[0].y)
                fillPath.moveTo(coordinates[0].x, height)
                fillPath.lineTo(coordinates[0].x, coordinates[0].y)

                for (i in 1 until coordinates.size) {
                    val prev = coordinates[i - 1]
                    val curr = coordinates[i]
                    val c1 = Offset((prev.x + curr.x) / 2f, prev.y)
                    val c2 = Offset((prev.x + curr.x) / 2f, curr.y)
                    path.cubicTo(c1.x, c1.y, c2.x, c2.y, curr.x, curr.y)
                    fillPath.cubicTo(c1.x, c1.y, c2.x, c2.y, curr.x, curr.y)
                }

                fillPath.lineTo(coordinates.last().x, height)
                fillPath.close()

                // Draw gradient area under curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            EmeraldIncome.copy(alpha = 0.35f),
                            CyanAccent.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw line stroke
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(EmeraldIncome, CyanAccent, GoldAccent)
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw point markers
                coordinates.forEachIndexed { idx, pointOffset ->
                    val isSelected = idx == selectedPointIndex
                    val radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx()

                    drawCircle(
                        color = VaultDarkBg,
                        radius = radius + 2.dp.toPx(),
                        center = pointOffset
                    )
                    drawCircle(
                        color = if (isSelected) GoldAccent else CyanAccent,
                        radius = radius,
                        center = pointOffset
                    )
                }
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { i, p ->
                // show sparse labels to avoid crowding
                if (i == 0 || i == points.size / 2 || i == points.lastIndex) {
                    Text(
                        text = p.label,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
