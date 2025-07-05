package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditorToolbar(
    modifier: Modifier = Modifier,
    actions: List<String> = listOf("Save", "Format", "Run"),
    onActionClick: (action: String, index: Int) -> Unit = { _, _ -> },
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        actions.forEachIndexed { index, label ->
            HoverUnderlineButton(
                text = label,
                enabled = enabled,
                onClick = { onActionClick(label, index) }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HoverUnderlineButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    var isHovered by remember { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.onSurface

    Text(
        text = text,
        fontSize = 13.sp,
        color = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            isHovered -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.primary
        },
        modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) {
                isHovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                isHovered = false
            }
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                interactionSource = null,
                indication = null,
                enabled = enabled,
                onClickLabel = "tap $text",
                role = Role.Button,
                onClick = onClick
            )
            .padding(vertical = 4.dp)
            .drawBehind {
                if (isHovered) {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth
                    drawLine(
                        color = color,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
            }
    )
}
