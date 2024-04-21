package com.salazar.cheers.feature.home.navigation.ads

import android.net.Uri
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.ui.MediaCarouselComponent


@Composable
fun NativeAdView(
    modifier: Modifier = Modifier,
    ad: NativeAd,
) {
    val context = LocalContext.current
    val bodyViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val headlineViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val callToActionViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val adViewId by remember { mutableIntStateOf(View.generateViewId()) }

    AndroidView(
        modifier = modifier,
        factory = {
            val bodyView = ComposeView(context).apply {
                id = bodyViewId
            }
            val headlineView = ComposeView(context).apply {
                id = headlineViewId
            }
            val callToActionView = ComposeView(context).apply {
                id = callToActionViewId
            }
            NativeAdView(context).apply {
                id = adViewId
                addView(bodyView)
                addView(headlineView)
                addView(callToActionView)
            }
        },
        update = { view ->
            val bodyView = view.findViewById<ComposeView>(bodyViewId)
            val headlineView = view.findViewById<ComposeView>(headlineViewId)
            val callToActionView = view.findViewById<ComposeView>(callToActionViewId)

            view.setNativeAd(ad)

            view.callToActionView = callToActionView
            view.headlineView = headlineView

            headlineView.setContent {
                NativeAdHeader(
                    icon = ad.icon?.uri,
                    headline = ad.headline.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp, 11.dp),
                )
            }
            callToActionView.setContent {
                NativeAdPost(
                    ad = ad,
                    onCallToActionClick = {
                        callToActionView.performClick()
                    }
                )
            }
        },
    )
}

@Composable
private fun NativeAdPost(
    modifier: Modifier = Modifier,
    ad: NativeAd,
    onCallToActionClick: () -> Unit,
) {
    val callToAction = ad.callToAction
    Column(
        modifier = modifier,
    ) {
        HorizontalDivider()
        NativeAdHeader(
            icon = ad.icon?.uri,
            headline = ad.headline.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp, 11.dp),
        )
        HorizontalDivider()
        MediaCarouselComponent(
            medias = ad.images.map { Media.Image(uri = it.uri ?: Uri.EMPTY) }
        )
        if (callToAction != null) {
            NativeAdButton(
                text = callToAction,
                modifier = Modifier.fillMaxWidth(),
                onClick = onCallToActionClick,
            )
        }
    }
}