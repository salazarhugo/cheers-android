package com.salazar.cheers.domain.update_profile

import com.salazar.cheers.data.account.Account
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.core.model.User
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userRepositoryImpl: UserRepositoryImpl,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        picture: String?,
        banner: String?,
        bio: String,
        name: String,
        website: String,
        favouriteDrinkId: String?,
    ): Result<User> = withContext(ioDispatcher) {
        return@withContext userRepositoryImpl.updateUserProfile(
            picture = picture.orEmpty(),
            name = name,
            bio = bio,
            website = website,
            banner = banner.orEmpty(),
            favouriteDrinkId = favouriteDrinkId,
        ).onSuccess { user ->
            accountRepository.putAccount(
                account = Account(
                    id = user.id,
                    name = user.name,
                    username = user.username,
                    banner = user.banner.orEmpty(),
                    email = user.email,
                    picture = user.picture.orEmpty(),
                    verified = user.verified,
                ),
            )
        }
    }
}
