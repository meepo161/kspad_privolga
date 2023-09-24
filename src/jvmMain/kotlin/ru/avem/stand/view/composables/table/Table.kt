package ru.avem.stand.view.composables.table

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> Table(
    modifier: Modifier = Modifier.fillMaxWidth(),
    selectedItem: T? = null,
    items: List<T>,
    tableScheme: TableScheme<T>,
    onItemPrimaryPressed: (Int) -> Unit = {},
    onItemSecondaryPressed: (Int) -> Unit = {},
    colorHeader: Color = MaterialTheme.colors.primary,
    rowHeight: Dp = 48.dp,
    fontSize: TextUnit = 48.sp,
    weights: List<Float> = listOf(0.1f)
) {
    var hoveredItem by remember { mutableStateOf(selectedItem) }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        if (tableScheme.name.isNotEmpty()) {
            Row {
                Box(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(8.dp)
                    ).weight(1.0f).height(rowHeight.times(1.33f)).clip(RoundedCornerShape(8.dp))
                        .background(colorHeader), contentAlignment = Alignment.Center
                ) {
                    Text(
                        tableScheme.name,
                        fontSize = fontSize,
                        style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.surface)
                    )
                }
            }
        }
        Row {
            if (tableScheme.columnNames.size == tableScheme.columns.size) {
                tableScheme.columnNames.forEachIndexed { columnIndex, column ->
                    Box(
                        modifier = Modifier.border(
                            width = 1.dp, color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
                        ).weight(weights[columnIndex % weights.size]).height(rowHeight.times(1.33f))
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorHeader), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            column,
                            fontSize = fontSize,
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.surface)
                        )
                    }
                }
            } else {
                tableScheme.columns.forEachIndexed { columnIndex, column ->
                    Box(
                        modifier = Modifier.weight(weights[columnIndex % weights.size]).height(rowHeight.times(1.33f))
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorHeader), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            column.name,
                            fontSize = fontSize,
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.surface)
                        )
                    }
                }
            }
        }


        items.forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.onClick(matcher = PointerMatcher.mouse(PointerButton.Primary),
                onClick = { onItemPrimaryPressed(rowIndex) })
                .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary),
                    keyboardModifiers = { true },
                    onClick = { onItemSecondaryPressed(rowIndex) }).background(
                    if (hoveredItem == row) {
                        MaterialTheme.colors.secondary
                    } else {
                        MaterialTheme.colors.background
                    }
                ).onPointerEvent(eventType = PointerEventType.Enter) {
                    hoveredItem = row
                }.onPointerEvent(eventType = PointerEventType.Exit) {
                    hoveredItem = null
                }.fillMaxWidth()
            ) {
                tableScheme.columns.forEachIndexed { columnIndex, column ->
                    val field = row!!.getField<MutableState<String>>(column.name)!!.value
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxWidth()
                            .weight(weights[columnIndex % weights.size], fill = false)
                            .height(rowHeight), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = field,
                            fontSize = fontSize,
                            color = if (selectedItem == row) Color.Gray else MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}
