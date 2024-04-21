package com.salazar.cheers.data.party.data.repository.impl

import cheers.party.v1.AnswerPartyRequest
import cheers.party.v1.CreatePartyRequest
import cheers.party.v1.FeedPartyRequest
import cheers.party.v1.ListPartyRequest
import cheers.party.v1.PartyServiceGrpcKt
import cheers.type.PartyOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.db.dao.PartyDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.data.party.data.mapper.toPartyAnswer
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.data.party.toParty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

        val response = service.feedParty(request)

        val parties = response.itemsList.map {
            it.toParty()
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
        return partyDao.getEvent(partyId).map { it.asExternalModel() }
    }

    override suspend fun feedParty(
        page: Int,
        pageSize: Int
    ): Flow<List<Party>> {
        return partyDao.feedParty().map { it.asExternalModel() }
    }

    override suspend fun fetchFeedParty(
        page: Int,
        pageSize: Int
    ): Result<List<Party>> {
        val request = FeedPartyRequest.newBuilder()
            .setPageSize(pageSize)
            .setPageToken("")
            .build()

        return try {
            val response = service.feedParty(request)
            val parties = response.itemsList.map {
                it.toParty()
            }
            partyDao.clearAll()
            partyDao.insertAll(parties.asEntity())

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
        val request = ListPartyRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .setUserId(userId)
            .build()

        val localParties = partyDao.getEvents(accountId = userId).first()
        emit(localParties.asExternalModel())

        try {
            val response = service.listParty(request)
            val parties = response.itemsList.map {
                it.toParty()
            }
            partyDao.insertAll(parties.asEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val parties = partyDao.getEvents(accountId = userId)
        emitAll(parties.map { it.asExternalModel() })
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
            partyDao.update(party.copy(watchStatus = watchStatus).asEntity())

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