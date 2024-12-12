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
        challenge = publicKey.challenge,
        timeout = 1_800_000,
        rpId = publicKey.rp.id,
        userVerification = "required",
        allowCredentials = this.publicKey.allowCredentials.map {
            val decodedBytes = it.id
                .replace('+', '-')
                .replace('/', '_')
                .replace("=", "")
            println(decodedBytes)
            GetPasskeyRequest.AllowCredentials(
                id = decodedBytes,
                type = "public-key",
                transports = it.transport.orEmpty(),
            )
        },
    )
}

fun BeginRegistrationResponse.toCreatePasskeyRequest(username: String): CreatePasskeyRequest {
    return CreatePasskeyRequest(
        challenge = publicKey.challenge,
        rp = CreatePasskeyRequest.Rp(
            id = publicKey.rp.id,
            name = publicKey.rp.name,
        ),
        user = CreatePasskeyRequest.User(
            id = publicKey.user.id,
            name = username,
            displayName = username,
        ),
        pubKeyCredParams = listOf(
            CreatePasskeyRequest.PubKeyCredParams(
                type = "public-key",
                alg = -7
            ),
            CreatePasskeyRequest.PubKeyCredParams(
                type = "public-key",
                alg = -257
            ),
        ),
        timeout = 1_800_000,
        attestation = "direct",
        excludeCredentials = emptyList(),
        authenticatorSelection = CreatePasskeyRequest.AuthenticatorSelection(
            authenticatorAttachment = "platform",
            requireResidentKey = true,
            residentKey = "required",
            userVerification = "required"
        )
    )
}
