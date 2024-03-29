package com.salazar.cheers.data.auth.mapper

import com.salazar.cheers.data.auth.b64Encode
import com.salazar.cheers.data.auth.model.CreatePasskeyRequest
import com.salazar.cheers.data.auth.model.CreatePasskeyResponseData
import com.salazar.cheers.data.auth.model.GetPasskeyRequest
import com.salazar.cheers.data.auth.model.GetPasskeyResponseData
import com.salazar.cheers.shared.data.request.FinishLoginPasskey
import com.salazar.cheers.shared.data.request.FinishLoginResponse
import com.salazar.cheers.shared.data.request.Passkey
import com.salazar.cheers.shared.data.request.Response
import com.salazar.cheers.shared.data.response.BeginLoginResponse
import com.salazar.cheers.shared.data.response.BeginRegistrationResponse
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


private const val RELYING_PARTY_ID = "cheers.social"

fun CreatePasskeyResponseData.toPasskey(): Passkey {
    return Passkey(
        id = id,
        type = type,
        rawId = rawId.encodeB64(),
        authenticatorAttachment = authenticatorAttachment,
        response = Response(
            clientDataJSON = response.clientDataJSON,
            attestationObject = response.attestationObject,
            transports = response.transports,
        ),
    )
}

fun GetPasskeyResponseData.toPasskey(): FinishLoginPasskey {
    return FinishLoginPasskey(
        id = id,
        type = type,
        rawId = rawId,
        authenticatorAttachment = authenticatorAttachment,
        response = FinishLoginResponse(
            clientDataJSON = response.clientDataJSON,
            authenticatorData = response.authenticatorData,
            signature = response.signature,
            userHandle = "",
        ),
    )
}

private fun String.encodeB64(): String {
    return toByteArray(StandardCharsets.UTF_8).b64Encode()
}

fun BeginLoginResponse.toGetPasskeyRequest(): GetPasskeyRequest {
    return GetPasskeyRequest(
        challenge = challenge,
        timeout = 1_800_000,
        rpId = relyingPartyId,
        userVerification = userVerification,
        allowCredentials = emptyList(),
//        allowCredentials.map {
//            GetPasskeyRequest.AllowCredentials(
//                id = it.id,
//                type = "public-key",
//                transports = it.transport.orEmpty() + "internal",
//            )
//        },
    )
}

fun BeginRegistrationResponse.toCreatePasskeyRequest(username: String): CreatePasskeyRequest {
    return CreatePasskeyRequest(
        challenge = challenge,
        rp = CreatePasskeyRequest.Rp(
            name = "Cheers",
            id = RELYING_PARTY_ID
        ),
        user = CreatePasskeyRequest.User(
            id = userId,
            name = username,
            displayName = username,
        ),
        pubKeyCredParams = listOf(
            CreatePasskeyRequest.PubKeyCredParams(
                type = "public-key",
                alg = -7
            )
        ),
        timeout = 1800000,
        attestation = "Android Key Attestation",
        excludeCredentials = emptyList(),
        authenticatorSelection = CreatePasskeyRequest.AuthenticatorSelection(
            authenticatorAttachment = "platform",
            requireResidentKey = false,
            residentKey = "required",
            userVerification = "preferred"
        )
    )
}
