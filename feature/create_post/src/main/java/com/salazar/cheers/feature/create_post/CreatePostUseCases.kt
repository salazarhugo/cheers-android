package com.salazar.cheers.feature.create_post

import com.salazar.cheers.domain.create_post.CreatePostUseCase
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.get_last_known_location.GetLastKnownLocationUseCase
import com.salazar.cheers.domain.get_location_name.GetLocationNameUseCase
import com.salazar.cheers.domain.list_drink.ListDrinkUseCase
import javax.inject.Inject


data class CreatePostUseCases @Inject constructor(
    internal val getAccountUseCase: GetAccountUseCase,
    internal val listDrinkUseCase: ListDrinkUseCase,
    internal val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    internal val createPostUseCase: CreatePostUseCase,
    internal val getLocationNameUseCase: GetLocationNameUseCase,
)
