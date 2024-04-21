package com.salazar.cheers.core.ui.components.qrcode

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType

@Composable
fun QrCodeComponent(
    value: String,
    modifier : Modifier = Modifier,
) {
    Barcode(
        modifier = modifier,
        type = BarcodeType.QR_CODE,
        value = value,
    )
}

@ComponentPreviews
@Composable
private fun QrCodeComponentPreview() {
    CheersPreview {
        QrCodeComponent(
            value = "https://cheers.social/cheers?utm_source=qr",
            modifier = Modifier.padding(16.dp),
        )
    }
}