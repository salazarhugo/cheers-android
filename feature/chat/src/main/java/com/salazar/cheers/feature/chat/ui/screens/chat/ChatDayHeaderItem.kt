package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import java.text.SimpleDateFormat
import java.util.Date


@Composable
internal fun ChatDayHeaderItem(
    modifier: Modifier = Modifier,
    messageTime: Long,
) {
    val date = Date(messageTime)
    val res = SimpleDateFormat("MMMM dd").format(date)

    ChatDayHeaderComponent(
        modifier = modifier,
        dayString = res,
    )
}

@Composable
private fun ChatDayHeaderComponent(
    dayString: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@ComponentPreviews
@Composable
private fun ChatDayHeaderItemPreview() {
    CheersPreview {
        ChatDayHeaderItem(
            modifier = Modifier.padding(16.dp),
            messageTime = Date().time,
        )
    }
}