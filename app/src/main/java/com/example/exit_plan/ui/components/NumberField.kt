package com.example.exit_plan.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> if (input.all { it.isDigit() || it in " .," }) onValueChange(input) },
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        suffix = suffix?.let { { Text(it) } },
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
    )
}
