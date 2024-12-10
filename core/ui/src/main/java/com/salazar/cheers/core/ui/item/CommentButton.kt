package com.salazar.cheers.core.ui.item

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun CommentButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Bounce(
        modifier = modifier,
        onBounce = onClick,
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(id = R.drawable.comment),
            contentDescription = null,
        )
    }
}

@ComponentPreviews
@Composable
private fun CommentButtonDarkPreview() {
    CheersPreview {
        CommentButton()
    }
}