package com.salazar.cheers.data.repository

import android.app.Application
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.paging.EventRemoteMediator
import com.salazar.cheers.internal.Party
import com.salazar.cheers.workers.UploadEventWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventRepository @Inject constructor(
    application: Application,
    private val coreService: CoreService,
    private val database: CheersDatabase,
) {

    private val workManager = WorkManager.getInstance(application)
    val eventDao = database.eventDao()

    fun getEvent(eventId: String): Flow<Party> {
        return eventDao.getEvent(eventId = eventId)
    }

    fun getEventFeed(): Flow<PagingData<Party>> {
        return Pager(
            config = PagingConfig(
                pageSize = PostRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = EventRemoteMediator(database = database, service = coreService),
        ) {
            eventDao.pagingSourceFeed()
        }.flow
    }

    fun getEvents(): Flow<List<Party>> {
        return eventDao.getEvents()
    }

    suspend fun refreshMyEvents() = withContext(Dispatchers.IO) {
        try {
            val myEvents = coreService.getEvents(0, 10)
            Log.d("DORA", myEvents.toString())
            eventDao.insertAll(myEvents)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateEvent(party: Party) = withContext(Dispatchers.IO) {
        coreService.updateEvent(party = party)
    }

    private suspend fun goingEvent(eventId: String) {
        coreService.goingEvent(eventId = eventId)
    }

    private suspend fun ungoingEvent(eventId: String) {
        coreService.ungoingEvent(eventId = eventId)
    }

    private suspend fun uninterestEvent(partyId: String) {
        coreService.uninterestEvent(partyId = partyId)
    }

    private suspend fun interestEvent(partyId: String) {
        coreService.interestEvent(partyId = partyId)
    }

    suspend fun hideEvent(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.deleteWithId(eventId = eventId)
    }

    suspend fun deleteEvent(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.deleteWithId(eventId = eventId)
        coreService.deleteEvent(eventId = eventId)
    }

    suspend fun uploadEvent(party: Party) {
        coreService.createParty(party = party)
    }

    suspend fun toggleGoing(eventId: String) {
        eventDao.toggleGoing(eventId = eventId)
    }

    suspend fun interestedList(eventId: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            coreService.interestedList(eventId = eventId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun goingList(eventId: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            coreService.goingList(eventId = eventId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun toggleInterested(eventId: String) {
        eventDao.toggleInterested(eventId = eventId)
    }

    suspend fun toggleGoing(party: Party) {
        eventDao.update(party.copy(going = !party.going))
        if (party.going)
            ungoingEvent(party.id)
        else
            goingEvent(party.id)
    }

    suspend fun toggleInterested(party: Party) {
        eventDao.update(party.copy(interested = !party.interested))
        if (party.interested)
            uninterestEvent(party.id)
        else
            interestEvent(party.id)
    }

    fun createEvent(
        party: Party,
    ) {
        party.apply {
            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadEventWorker>()
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

    companion object
}