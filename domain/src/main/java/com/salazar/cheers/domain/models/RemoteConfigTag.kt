package com.salazar.cheers.domain.models

enum class RemoteConfigParameter(
    val key: String,
) {
    // Remote config keys
    Passkey("app_feature_passkey"),

    // Remote ab test keys
//    ABTestDisplayLastHomeContext("ab_test_display_last_home_context"),
}
