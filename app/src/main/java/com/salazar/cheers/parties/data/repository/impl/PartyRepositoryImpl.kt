package com.salazar.cheers.parties.data.repository.impl

import cheers.party.v1.*
import cheers.type.PartyOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.data.internal.WatchStatus
import com.salazar.cheers.data.db.PartyDao
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.mapper.toParty
import com.salazar.cheers.parties.data.mapper.toPartyAnswer
import com.salazar.cheers.parties.data.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PartyRepositoryImpl @Inject constructor(
    private val service: PartyServiceGrpcKt.PartyServiceCoroutineStub,
    private val partyDao: PartyDao,
) : PartyRepository {

    suspend fun getPartyFeed(page: Int, pageSize: Int): Result<List<Party>> {
        val request = FeedPartyRequest.newBuilder()
            .setPageSize(pageSize)
            .setPageToken(page.toString())
            .build()

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val response = service.feedParty(request)

        val parties = response.itemsList.map {
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
            val parties = response.itemsList.map {
                it.toParty(uid)
            }
            partyDao.insertAll(parties)

            Result.success(parties)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun listParty(
        page: Int,
        pageSize: Int,
        userId: String,
    ): Flow<List<Party>> = flow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = ListPartyRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .setUserId(userId)
            .build()

        val localParties = partyDao.getEvents(accountId = userId).first()
        emit(localParties)

        try {
            val response = service.listParty(request)
            val parties = response.itemsList.map {
                it.toParty(uid)
            }
            partyDao.insertAll(parties)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val parties = partyDao.getEvents(accountId = userId)
        emitAll(parties)
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

    override suspend fun setWatchStatus(
        partyId: String,
        watchStatus: WatchStatus
    ): Result<Unit> {
        return try {
            val party = getParty(partyId = partyId).first()
            partyDao.update(party.copy(watchStatus = watchStatus))

            val request = AnswerPartyRequest.newBuilder()
                .setPartyId(partyId)
                .setWatchStatus(watchStatus.toPartyAnswer())
                .build()
            val response = service.answerParty(request = request)
            Result.success(Unit)
        } catch (e:Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}