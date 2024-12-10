package com.salazar.cheers.data.party.data.repository.impl

import cheers.party.v1.AnswerPartyRequest
import cheers.party.v1.CreatePartyRequest
import cheers.party.v1.FeedPartyRequest
import cheers.party.v1.GetPartyItemRequest
import cheers.party.v1.ListPartyRequest
import cheers.party.v1.PartyServiceGrpcKt
import com.salazar.cheers.core.db.dao.PartyDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.shared.data.mapper.toParty
import com.salazar.cheers.shared.data.mapper.toPartyAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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
            .setPage(page)
            .build()

        val response = service.feedParty(request)

        val parties = response.itemsList.map {
            it.toParty()
        }

        return Result.success(parties)
    }

    override suspend fun createParty(
        request: CreatePartyRequest,
    ): Result<Unit> {
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
        val request = GetPartyItemRequest.newBuilder()
            .setPartyId(partyId)
            .build()

        try {
            val response = service.getPartyItem(request)
            val party = response.item.toParty()
            partyDao.insert(party.asEntity())

            Result.success(party)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

        return partyDao.getEvent(partyId).mapNotNull { it?.asExternalModel() }
    }

    override suspend fun feedParty(
        page: Int,
        pageSize: Int
    ): Flow<List<Party>> {
        return partyDao.feedParty().map { it.asExternalModel() }
    }

    override suspend fun fetchFeedParty(
        city: String,
        page: Int,
        pageSize: Int
    ): Result<List<Party>> {
        val request = FeedPartyRequest.newBuilder()
            .setPageSize(pageSize)
            .setPage(page)
            .setCity(city)
            .build()

        println(request)
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
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}