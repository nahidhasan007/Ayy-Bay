package com.ayybay.app.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.label
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.BmiCategory
import com.ayybay.app.presentation.util.bmiOf
import com.ayybay.app.presentation.util.categoryOf
import com.ayybay.app.presentation.util.healthyWeightRange
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorScreen(
    initialHeightCm: Double?,
    initialWeightKg: Double?,
    onSave: (heightCm: Double, weightKg: Double) -> Unit,
    onBack: () -> Unit
) {
    var heightText by remember { mutableStateOf(initialHeightCm?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var weightText by remember { mutableStateOf(initialWeightKg?.let { if (it == 0.0) "" else it.toString() } ?: "") }

    val height = heightText.toDoubleOrNull()
    val weight = weightText.toDoubleOrNull()
    val bmi = if (height != null && height > 0 && weight != null && weight > 0) bmiOf(height, weight) else null

    LaunchedEffect(height, weight) {
        if (height != null && height > 0 && weight != null && weight > 0) {
            onSave(height, weight)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "⚖️  " + tr("BMI Calculator", "বিএমআই ক্যালকুলেটর"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = { LanguageToggle(modifier = Modifier.padding(end = 12.dp)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = tr("Height (cm)", "উচ্চতা (সেমি)"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = { input -> heightText = input.filter { it.isDigit() || it == '.' } },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("170") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = tr("Weight (kg)", "ওজন (কেজি)"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { input -> weightText = input.filter { it.isDigit() || it == '.' } },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("65") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }

            if (bmi != null && height != null) {
                val category = categoryOf(bmi)
                val range = healthyWeightRange(height)
                item {
                    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = tr("Your BMI", "আপনার বিএমআই"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = String.format("%.1f", bmi),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = category.color
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(shape = RoundedCornerShape(50), color = category.color.copy(alpha = 0.12f)) {
                                Text(
                                    text = category.label(),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    color = category.color,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            BmiRangeBar(bmi = bmi, modifier = Modifier.fillMaxWidth().height(28.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = tr(
                                    "Healthy weight range: ${range.start.roundToInt()}–${range.endInclusive.roundToInt()} kg",
                                    "স্বাস্থ্যকর ওজনের সীমা: ${range.start.roundToInt()}–${range.endInclusive.roundToInt()} কেজি"
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = tr("Enter your height and weight to see your BMI", "আপনার বিএমআই দেখতে উচ্চতা ও ওজন লিখুন"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun BmiRangeBar(bmi: Double, modifier: Modifier = Modifier) {
    val minBmi = 15.0
    val maxBmi = 40.0
    val segments = listOf(
        BmiCategory.UNDERWEIGHT.color to 18.5,
        BmiCategory.NORMAL.color to 25.0,
        BmiCategory.OVERWEIGHT.color to 30.0,
        BmiCategory.OBESE.color to maxBmi
    )

    Canvas(modifier = modifier) {
        var start = minBmi
        segments.forEach { (color, end) ->
            val startX = ((start - minBmi) / (maxBmi - minBmi) * size.width).toFloat()
            val endX = ((end.coerceAtMost(maxBmi) - minBmi) / (maxBmi - minBmi) * size.width).toFloat()
            drawRect(color = color, topLeft = Offset(startX, 0f), size = androidx.compose.ui.geometry.Size(endX - startX, size.height))
            start = end
        }
        val markerX = (((bmi.coerceIn(minBmi, maxBmi)) - minBmi) / (maxBmi - minBmi) * size.width).toFloat()
        drawLine(
            color = Color.White,
            start = Offset(markerX, 0f),
            end = Offset(markerX, size.height),
            strokeWidth = 6f
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.5f),
            start = Offset(markerX, 0f),
            end = Offset(markerX, size.height),
            strokeWidth = 2f
        )
    }
}
