package com.salazar.cheers.feature.premium.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.premium.PremiumCard
import com.salazar.cheers.feature.premium.R

@Composable
fun PremiumScreen(
    uiState: PremiumUiState,
    onBackPressed: () -> Unit,
    navigateToSignIn: () -> Unit = {},
    navigateToSignUp: () -> Unit = {},
    navigateToPremiumMoreSheet: (String) -> Unit = {},
    onSubscribeClick: () -> Unit = {},
//    onPremiumUIAction: (PremiumUIAction) -> Unit = {},
) {
    Scaffold(
        topBar = {
             PremiumTopBar(
                 onBackPressed = onBackPressed,
             )
        },
        bottomBar = {
            PremiumBottomBar(
                modifier = Modifier.navigationBarsPadding(),
                onSubscribeClick = onSubscribeClick,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            PremiumCard(
                onSubscribeClick = onSubscribeClick,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun PremiumScreenPreview() {
    CheersPreview {
    }
}
