package ru.avem.stand.view.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> ComboBox(
    modifier: Modifier = Modifier,
    selectedItem: MutableState<T>,
    items: List<T>,
    isEditable: Boolean = true,
    onDismissState: () -> Unit = {},
    onSelect: (T) -> Unit = {},
) {
    var expandedState by remember {
        onSelect(selectedItem.value)
        mutableStateOf(false)
    }

    Column(modifier = modifier.border(1.dp, Color.Black)) {
        if (items.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.clickable { expandedState = true }.padding(16.dp).fillMaxWidth(),
            ) {
                Text(
                    selectedItem.value.toString(),
                    style = LocalTextStyle.current
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(text = "Элементы отсутствуют в списке")
            }
        }
        if (isEditable) {
            DropdownMenu(
                modifier = Modifier.width(600.dp),
                expanded = expandedState,
                onDismissRequest = {
                    expandedState = false
                    onDismissState()
                }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        modifier = Modifier.width(600.dp),
                        onClick = {
                            selectedItem.value = item
                            onSelect(item)
                            expandedState = false
                        }) {
                        Text(
                            item.toString(),
                            modifier = Modifier.padding(16.dp).width(600.dp),
                            style = LocalTextStyle.current
                        )
                    }
                }
            }
        }
    }
}
