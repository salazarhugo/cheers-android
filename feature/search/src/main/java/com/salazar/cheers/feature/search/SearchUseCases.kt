package com.salazar.cheers.feature.search

import com.salazar.cheers.domain.search.ClearRecentSearchUseCase
import com.salazar.cheers.domain.search.CreateRecentUserUseCase
import com.salazar.cheers.domain.search.DeleteRecentSearchUseCase
import com.salazar.cheers.domain.search.GetRecentSearchUseCase
import com.salazar.cheers.domain.search.SearchUseCase
import javax.inject.Inject


data class SearchUseCases @Inject constructor(
    val searchUseCase: SearchUseCase,
    val getRecentSearchUseCase: GetRecentSearchUseCase,
    val createRecentUserUseCase: CreateRecentUserUseCase,
    val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    val clearRecentSearchUseCase: ClearRecentSearchUseCase,
)