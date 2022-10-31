package com.salazar.cheers.data.repository.party.impl

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import cheers.party.v1.CreatePartyRequest
import cheers.party.v1.FeedPartyRequest
import cheers.party.v1.PartyServiceGrpcKt
import cheers.story.v1.CreateStoryRequest
import cheers.story.v1.FeedStoryRequest
import cheers.type.PartyOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.PartyDao
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.mapper.toParty
import com.salazar.cheers.data.mapper.toStory
import com.salazar.cheers.data.mapper.toUser
import com.salazar.cheers.data.repository.party.PartyRepository
import com.salazar.cheers.internal.Party
import com.salazar.cheers.workers.CreatePartyWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PartyRepositoryImpl @Inject constructor(
    application: Application,
    private val service: PartyServiceGrpcKt.PartyServiceCoroutineStub,
    private val partyDao: PartyDao,
): PartyRepository {

    suspend fun getPartyFeed(page: Int, pageSize: Int): Result<List<Party>> {
        val request = FeedPartyRequest.newBuilder()
            .setPageSize(pageSize)
            .setPageToken(page.toString())
            .build()

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val response = service.feedParty(request)

        val parties = response.partiesList.map {
            it.toParty(uid)
        }

        return Result.success(parties)
    }

    override suspend fun createParty(party: PartyOuterClass.Party): Result<Unit> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = CreatePartyRequest.newBuilder()
            .setParty(party)
            .build()

        return try {
            val response = service.createParty(request)
//            partyDao.insert(response.toParty(uid, uid))
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getParty(partyId: String): Flow<Party> {
        return partyDao.getEvent(partyId)
    }

    override suspend fun feedParty(
        page: Int,
        pageSize: Int
    ): Flow<List<Party>> {
        return partyDao.feedParty()
    }

    override suspend fun fetchFeedParty(
        page: Int,
        pageSize: Int
    ): Result<List<Party>> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = FeedPartyRequest.newBuilder()
            .setPageSize(pageSize)
            .setPageToken("")
            .build()

        return try {
            val response = service.feedParty(request)
            val parties = response.partiesList.map {
                it.toParty(uid)
            }
            partyDao.insertAll(parties)

            Result.success(parties)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getMyParties(): Flow<List<Party>> {
        TODO("Not yet implemented")
    }

    override fun getUserParty(username: String): Flow<List<Story>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteParty(partyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun interestParty(partyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun uninterestParty(partyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}

