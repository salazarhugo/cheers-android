package com.salazar.cheers.feature.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.circular_progress.CircularProgressComponent
import com.salazar.cheers.core.ui.theme.GreenGoogle
import com.salazar.cheers.core.ui.theme.StrongRed
import com.salazar.cheers.shared.util.Resource

@Composable
fun UsernameAvailabilityIconComponent(
    state: Resource<Boolean>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is Resource.Error -> {}
            is Resource.Loading -> {
                CircularProgressComponent(
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                )
            }

            is Resource.Success -> {
                if (state.data == true) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check icon",
                        tint = GreenGoogle,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close icon",
                        tint = StrongRed,
                    )
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun UsernameAvailabilityPreview() {
    CheersPreview {
        UsernameAvailabilityIconComponent(
            state = Resource.Loading(isLoading = true),
            modifier = Modifier,
        )
    }
}