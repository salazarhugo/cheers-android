package com.salazar.cheers.feature.signin

import com.salazar.cheers.domain.usecase.CheckAlreadySignedInUseCase
import com.salazar.cheers.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.salazar.cheers.domain.usecase.SignInWithEmailLinkUseCase
import com.salazar.cheers.domain.usecase.SignInWithGoogleUseCase
import javax.inject.Inject


data class SignInUseCases @Inject constructor(
    val signInWithEmailLinkUseCase: SignInWithEmailLinkUseCase,
    val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    val checkAlreadySignedInUseCase: CheckAlreadySignedInUseCase,
)
