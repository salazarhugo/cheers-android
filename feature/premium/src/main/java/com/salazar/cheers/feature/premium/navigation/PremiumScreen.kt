package com.salazar.cheers.feature.premium.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.SubscriptionOfferDetails
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.theme.orangeColors

@Composable
fun PremiumScreen(
    uiState: PremiumUiState,
    onBackPressed: () -> Unit,
    onSubscribeClick: () -> Unit = {},
    onPlanClick: (SubscriptionOfferDetails) -> Unit,
) {
    val plans = uiState.plans
    val features = uiState.features

    Scaffold(
        topBar = {
            PremiumTopBar(
                onBackPressed = onBackPressed,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !uiState.isLoading && !uiState.isPremium,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                PremiumBottomBar(
                    formattedPrice = uiState.selectedPlan?.formattedPrice.orEmpty(),
                    modifier = Modifier
                        .navigationBarsPadding(),
                    onSubscribeClick = onSubscribeClick,
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it),
        ) {
            if (uiState.isPremium) {
                subscriptionStatus(
                    subscriptionProductId = uiState.subscriptionProductID.orEmpty(),
                )
            } else {
                plans(
                    selectedPlan = uiState.selectedPlan,
                    plans = plans,
                    onPlanClick = onPlanClick,
                )
            }
            item {
                Spacer(Modifier.height(84.dp))
            }
            exclusiveFeatures(
                features = features,
            )
        }
    }
}

private fun LazyListScope.subscriptionStatus(
    subscriptionProductId: String,
) {
    item {
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .animateItem()
                .padding(16.dp),
        ) {
            Text(
                text = "Your Premium Subscription is active."
            )
            Text(
                text = "Extension date is"
            )
            val annotatedString = buildAnnotatedString {
                append("Manage your subscription in the")
                val link = LinkAnnotation.Url(
                    url = "https://play.google.com/store/account/subscriptions?sku=cheers_premium&package=com.salazar.cheers",
                    styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary)),
                    linkInteractionListener = {
                        val url = (it as LinkAnnotation.Url).url
                        uriHandler.openUri(url)
                    }
                )
                withLink(link = link) {
                    append(" Play Store")
                }
                append(".")
            }
            Text(
                text = annotatedString,
            )
        }
    }
}

private fun LazyListScope.plans(
    selectedPlan: SubscriptionOfferDetails?,
    plans: List<SubscriptionOfferDetails>,
    onPlanClick: (SubscriptionOfferDetails) -> Unit,
) {
    items(
        items = plans,
    ) {
        PremiumPlanItem(
            plan = it,
            modifier = Modifier.animateItem(),
            onPlanClick = onPlanClick,
            isSelected = selectedPlan?.offerToken == it.offerToken,
        )
    }
}

private fun LazyListScope.exclusiveFeatures(
    features: List<PremiumFeature>,
) {
    val colorShift = 30f

    itemsIndexed(
        items = features,
    ) { index, feature ->
        val padding = if (index == 0) {
            Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(MaterialTheme.colorScheme.surfaceDim)
                .padding(top = 8.dp)
        } else {
            Modifier
        }
        val colors = orangeColors.map { color ->
            val hsv = FloatArray(3)
            val red = color.red * 255f // Convert to 0-255 range
            val green = color.green * 255f
            val blue = color.blue * 255f
            android.graphics.Color.RGBToHSV(red.toInt(), green.toInt(), blue.toInt(), hsv)
            hsv[0] = (hsv[0] + (index * colorShift)) % 360f
            Color.hsv(hsv[0], hsv[1], hsv[2])
        }

        PremiumFeatureItem(
            name = feature.name,
            description = feature.description,
            icon = feature.icon,
            colors = colors,
            modifier = Modifier
                .animateItem()
                .then(padding)
                .clickable { }
        )
        if (index != features.lastIndex) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 64.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.background,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun PremiumScreenPreview() {
    val features = listOf(
        PremiumFeature(
            name = "Exclusive Parties",
            description = "Get access to exclusive parties reserved for premium users."
        ),
        PremiumFeature(
            name = "No Ads",
            description = "No more ads in your feed where Cheers sometimes shows ads."
        ),
        PremiumFeature(
            name = "Premium Badge",
            description = "Get an exclusive badge next to your name showing your VIP status within the Cheers community."
        ),
        PremiumFeature(
            name = "Special Discounts & Offers",
            description = "Enjoy exclusive deals from our partners."
        ),
        PremiumFeature(
            name = "Early Access to New Features",
            description = "Be the first to try the latest and greatest features.",
        ),
        PremiumFeature(
            name = "Priority Support",
            description = "Get help quickly and efficiently from our dedicated support team.",
        ),
        PremiumFeature(
            name = "Exclusive Chat Features",
            description = "Enjoy high-quality video calls, ad-free messaging, and more.",
        ),
    )
    CheersPreview {
        PremiumScreen(
            uiState = PremiumUiState(
                isRefreshing = true,
                isPremium = false,
                isLoading = false,
                features = features,
            ),
            onBackPressed = {},
            onPlanClick = {},
        )
    }
}
