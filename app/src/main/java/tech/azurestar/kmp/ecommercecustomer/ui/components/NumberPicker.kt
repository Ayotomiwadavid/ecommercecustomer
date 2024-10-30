package tech.azurestar.kmp.ecommercecustomer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

enum class NumberPickerOrientation {
    Horizontal,
    Vertical
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    orientation: NumberPickerOrientation = NumberPickerOrientation.Horizontal
) {
    var isError by remember { mutableStateOf(false) }
    var textFieldValue by remember(value) { mutableStateOf(value.toString()) }

    val arrangement = when (orientation) {
        NumberPickerOrientation.Horizontal -> Box {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberPickerContent(
                    value = value,
                    textFieldValue = textFieldValue,
                    onTextFieldValueChange = { newValue ->
                        textFieldValue = newValue
                        handleValueChange(newValue, minValue, maxValue, onValueChange) { isError = it }
                    },
                    onDecrease = { if (value > minValue) onValueChange(value - 1) },
                    onIncrease = { if (value < maxValue) onValueChange(value + 1) },
                    isError = isError,
                    enabled = enabled,
                    minValue = minValue,
                    maxValue = maxValue,
                    decreaseEnabled = enabled && value > minValue,
                    increaseEnabled = enabled && value < maxValue
                )
            }
        }
        NumberPickerOrientation.Vertical -> Box {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberPickerContent(
                    value = value,
                    textFieldValue = textFieldValue,
                    onTextFieldValueChange = { newValue ->
                        textFieldValue = newValue
                        handleValueChange(newValue, minValue, maxValue, onValueChange) { isError = it }
                    },
                    onDecrease = { if (value > minValue) onValueChange(value - 1) },
                    onIncrease = { if (value < maxValue) onValueChange(value + 1) },
                    isError = isError,
                    enabled = enabled,
                    minValue = minValue,
                    maxValue = maxValue,
                    decreaseEnabled = enabled && value > minValue,
                    increaseEnabled = enabled && value < maxValue,
                    isVertical = true
                )
            }
        }
    }
}

@Composable
private fun NumberPickerContent(
    value: Int,
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    isError: Boolean,
    enabled: Boolean,
    minValue: Int,
    maxValue: Int,
    decreaseEnabled: Boolean,
    increaseEnabled: Boolean,
    isVertical: Boolean = false
) {
    if (!isVertical) {
        // Decrease button
        IconButton(
            onClick = onDecrease,
            enabled = decreaseEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease"
            )
        }
    } else {
        // Increase button (top for vertical)
        IconButton(
            onClick = onIncrease,
            enabled = increaseEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase"
            )
        }
    }

    // Number input field
    Text(text = textFieldValue)

    if (!isVertical) {
        // Increase button (right for horizontal)
        IconButton(
            onClick = onIncrease,
            enabled = increaseEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase"
            )
        }
    } else {
        // Decrease button (bottom for vertical)
        IconButton(
            onClick = onDecrease,
            enabled = decreaseEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease"
            )
        }
    }
}

private fun handleValueChange(
    newValue: String,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    onErrorChange: (Boolean) -> Unit
) {
    newValue.toIntOrNull()?.let { numericValue ->
        val isError = numericValue < minValue || numericValue > maxValue
        onErrorChange(isError)
        if (!isError) {
            onValueChange(numericValue)
        }
    } ?: run {
        onErrorChange(true)
    }
}