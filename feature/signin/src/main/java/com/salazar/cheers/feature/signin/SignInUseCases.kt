package com.salazar.cheers.feature.signin

import com.salazar.cheers.domain.usecase.CheckAlreadySignedInUseCase
import com.salazar.cheers.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.salazar.cheers.domain.usecase.SignInWithEmailLinkUseCase
import com.salazar.cheers.domain.usecase.SignInWithOneTapUseCase
import javax.inject.Inject


data class SignInUseCases @Inject constructor(
    val signInWithEmailLinkUseCase: SignInWithEmailLinkUseCase,
    val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    val signInWithOneTapUseCase: SignInWithOneTapUseCase,
    val checkAlreadySignedInUseCase: CheckAlreadySignedInUseCase,
)
