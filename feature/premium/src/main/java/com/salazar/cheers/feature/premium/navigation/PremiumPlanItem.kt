package com.salazar.cheers.feature.premium.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.SubscriptionOfferDetails
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PremiumPlanItem(
    isSelected: Boolean,
    plan: SubscriptionOfferDetails,
    modifier: Modifier = Modifier,
    onPlanClick: (SubscriptionOfferDetails) -> Unit,
) {
    ListItem(
        modifier = modifier.clickable {
            onPlanClick(plan)
        },
        trailingContent = {
            Text(
                text = plan.monthlyFormattedPrice.toString(),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = plan.name,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        supportingContent = {
            if (plan.name == "Monthly") return@ListItem

            val annotatedString = buildAnnotatedString {
//                withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
//                    append("$59.99")
//                }
                append("${plan.formattedPrice}")
            }
            Text(text = annotatedString)
        },
        leadingContent = {
            RadioButton(
                selected = isSelected,
                onClick = { onPlanClick(plan) },
            )
        }
    )
}

@ComponentPreviews
@Composable
private fun PremiumPlansPreview() {
    CheersPreview {
        PremiumPlanItem(
            plan = SubscriptionOfferDetails(
                name = "Annual",
                formattedPrice = "",
                offerToken = "",
                monthlyFormattedPrice = "",
            ),
            modifier = Modifier,
            onPlanClick = {},
            isSelected = true
        )
    }
}
