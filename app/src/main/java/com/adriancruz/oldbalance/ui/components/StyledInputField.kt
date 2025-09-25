package com.adriancruz.oldbalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.ui.theme.AppColors
import com.adriancruz.oldbalance.ui.theme.Shapes
import com.adriancruz.oldbalance.ui.theme.Typography

@Composable
fun StyledInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    val fieldModifier = if (onClick != null) {
        modifier.clickable(
            interactionSource = interactionSource,
            indication = null, // No ripple effect
            onClick = onClick
        )
    } else {
        modifier
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = fieldModifier.fillMaxWidth(),
        singleLine = true,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(AppColors.InputBackground, Shapes.medium)
                    .border(1.dp, AppColors.InputBorder, Shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.body1.copy(color = AppColors.TextSecondary)
                        )
                    }
                    innerTextField()
                }
            }
        },
        textStyle = Typography.body1.copy(color = AppColors.TextPrimary)
    )
}
