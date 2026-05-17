package com.bjmc.classpet.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.RewardLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardHistoryScreen(
    logs: List<RewardLogEntity>,
    onUndo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    if (logs.isEmpty()) {
        Text(
            text = "暂无奖励记录",
            modifier = modifier.fillMaxSize().padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(logs, key = { it.id }) { log ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) { onUndo(log.id); true }
                        else false
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {},
                    enableDismissFromStartToEnd = false
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = log.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Text(text = "+${log.score}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
