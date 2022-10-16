package com.salazar.cheers.data.repository

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import cheers.party.v1.FeedPartyRequest
import cheers.party.v1.PartyServiceGrpcKt
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.mapper.toParty
import com.salazar.cheers.internal.Party
import com.salazar.cheers.workers.CreatePartyWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PartyRepository @Inject constructor(
    application: Application,
    private val partyService: PartyServiceGrpcKt.PartyServiceCoroutineStub,
    private val database: CheersDatabase,
) {

    private val workManager = WorkManager.getInstance(application)

    val partyDao = database.partyDao()

    fun getEvent(eventId: String): Flow<Party> {
        return partyDao.getEvent(eventId = eventId)
    }

    suspend fun createParty(
        party: Party,
    ) {
        party.apply {
            val uploadWorkRequest = OneTimeWorkRequestBuilder<CreatePartyWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "NAME" to name,
                        "ADDRESS" to address,
                        "DESCRIPTION" to description,
                        "EVENT_PRIVACY" to privacy.name,
                        "IMAGE_URI" to bannerUrl,
                        "START_DATETIME" to startDate,
                        "END_DATETIME" to endDate,
                        "LOCATION_NAME" to locationName,
                        "LATITUDE" to latitude,
                        "SHOW_GUEST_LIST" to showGuestList,
                        "LONGITUDE" to longitude,
                    )
                )
                .build()

            workManager.enqueue(uploadWorkRequest)
        }
    }

    suspend fun getPartyFeed(page: Int, pageSize: Int): Result<List<Party>> {
        val request = FeedPartyRequest.newBuilder()
            .setPageSize(pageSize)
            .setPageToken(page.toString())
            .build()

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val response = partyService.feedParty(request)

        val parties = response.partiesList.map {
            it.toParty(uid)
        }

        return Result.success(parties)
    }
}

