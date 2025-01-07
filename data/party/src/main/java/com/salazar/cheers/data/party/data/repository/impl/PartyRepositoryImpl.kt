package com.salazar.cheers.data.party.data.repository.impl

import cheers.party.v1.AnswerPartyRequest
import cheers.party.v1.CreatePartyRequest
import cheers.party.v1.DeletePartyRequest
import cheers.party.v1.FeedPartyRequest
import cheers.party.v1.GetPartyItemRequest
import cheers.party.v1.ListGoingRequest
import cheers.party.v1.ListInterestedRequest
import cheers.party.v1.ListPartyRequest
import cheers.party.v1.PartyServiceGrpcKt
import cheers.type.Pagination
import com.salazar.cheers.core.db.dao.PartyDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.PartyID
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.shared.data.mapper.toFilter
import com.salazar.cheers.shared.data.mapper.toParty
import com.salazar.cheers.shared.data.mapper.toPartyAnswer
import com.salazar.cheers.shared.data.mapper.toUserItem
import com.salazar.cheers.shared.data.toDataError
import com.salazar.cheers.shared.util.result.DataError
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PartyRepositoryImpl @Inject constructor(
    private val service: PartyServiceGrpcKt.PartyServiceCoroutineStub,
    private val partyDao: PartyDao,
) : PartyRepository {

    override val filtersFlow: MutableStateFlow<String> = MutableStateFlow("")

    override fun updateFilter(chatFilter: String) {
        filtersFlow.update { chatFilter }
    }

    suspend fun getPartyFeed(page: Int, pageSize: Int): Result<List<Party>> {
        val pagination = Pagination.PaginationRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        val request = FeedPartyRequest.newBuilder()
            .setPagination(pagination)
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
            partyDao.insert(response.item.toParty().asEntity())
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
        } catch (e: StatusException) {
            e.printStackTrace()
            val dataError = e.toDataError()
            if (dataError == DataError.Network.NOT_FOUND) {
                partyDao.deleteWithId(partyId)
            }
            Result.failure(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

        return partyDao.getEvent(partyId).mapNotNull { it?.asExternalModel() }
    }

    override suspend fun feedParty(
        city: String,
        page: Int,
        pageSize: Int
    ): Flow<List<Party>> {
        return partyDao.feedParty(
            city = city,
        ).map { it.asExternalModel() }
    }

    override suspend fun fetchFeedParty(
        city: String,
        page: Int,
        pageSize: Int
    ): Result<List<Party>> {
        val pagination = Pagination.PaginationRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        val request = FeedPartyRequest.newBuilder()
            .setPagination(pagination)
            .setCity(city)
            .build()

        return try {
            val response = service.feedParty(request)
            val parties = response.itemsList.map {
                it.toParty()
            }
            partyDao.insertAll(parties.asEntity())

            Result.success(parties)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun listInterested(
        page: Int,
        pageSize: Int,
        partyID: PartyID
    ): com.salazar.cheers.shared.util.result.Result<List<UserItem>, DataError> {
        val pagination = Pagination.PaginationRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        val request = ListInterestedRequest.newBuilder()
            .setPagination(pagination)
            .setPartyId(partyID)
            .build()

        return try {
            val response = service.listInterested(request)
            val parties = response.usersList.map {
                it.toUserItem()
            }
            com.salazar.cheers.shared.util.result.Result.Success(parties)
        } catch (e: StatusException) {
            e.printStackTrace()
            com.salazar.cheers.shared.util.result.Result.Error(e.toDataError())
        } catch (e: Exception) {
            e.printStackTrace()
            com.salazar.cheers.shared.util.result.Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun listGoing(
        page: Int,
        pageSize: Int,
        partyID: PartyID
    ): com.salazar.cheers.shared.util.result.Result<List<UserItem>, DataError> {
        val pagination = Pagination.PaginationRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        val request = ListGoingRequest.newBuilder()
            .setPagination(pagination)
            .setPartyId(partyID)
            .build()

        return try {
            val response = service.listGoing(request)
            val parties = response.usersList.map {
                it.toUserItem()
            }
            com.salazar.cheers.shared.util.result.Result.Success(parties)
        } catch (e: Exception) {
            e.printStackTrace()
            com.salazar.cheers.shared.util.result.Result.Error(e.toDataError())
        }
    }

    override suspend fun listParty(
        filter: String,
        page: Int,
        pageSize: Int
    ): com.salazar.cheers.shared.util.result.Result<Pair<List<Party>, List<Filter>>, DataError> {
        val pagination = Pagination.PaginationRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        val request = ListPartyRequest.newBuilder()
            .setPagination(pagination)
            .setFilter(filter)
            .build()

        return try {
            val response = service.listParty(request)
            val parties = response.itemsList.map {
                it.toParty()
            }
            partyDao.insertAll(parties.asEntity())
            val newFilters = response.filtersList.map { it.toFilter() }
            val pair = Pair(parties, newFilters)
            com.salazar.cheers.shared.util.result.Result.Success(pair)
        } catch (e: Exception) {
            e.printStackTrace()
            com.salazar.cheers.shared.util.result.Result.Error(e.toDataError())
        }
    }

    override fun listPartyFlow(
        filter: String,
        page: Int,
        pageSize: Int,
        userId: String,
    ): Flow<List<Party>> {
        val partyEntities = when (filter) {
            "hosting" -> partyDao.listPartyByUserID(userId = userId)
            "interested" -> partyDao.listPartyByWatchStatus(watchStatus = WatchStatus.INTERESTED)
            "going" -> partyDao.listPartyByWatchStatus(watchStatus = WatchStatus.GOING)
            "past" -> partyDao.listPastPartyByUserId(userId = userId)
            else -> partyDao.listPartyByUserIdAndWatchStatus(userId = userId)
        }
        return partyEntities.map { it.asExternalModel() }
    }

    override suspend fun deleteParty(partyId: String): Result<Unit> {
        return try {
            val request = DeletePartyRequest.newBuilder()
                .setPartyId(partyId)
                .build()
            val response = service.deleteParty(request = request)
            partyDao.deleteWithId(partyId)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
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
fun <T> concatenate(vararg flows: Flow<T>) =
    flows.asFlow().flattenConcat()