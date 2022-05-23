package com.salazar.cheers.data.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.work.*
import com.salazar.cheers.backend.GoApi
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.paging.EventRemoteMediator
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.workers.UploadEventWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventRepository @Inject constructor(
    application: Application,
    private val goApi: GoApi,
    private val database: CheersDatabase,
) {

    private val workManager = WorkManager.getInstance(application)
    val eventDao = database.eventDao()

    fun getEvent(eventId: String): Flow<Event> {
        return eventDao.getEvent(eventId = eventId)
    }

    fun getEventFeed(): Flow<PagingData<Event>> {
        return Pager(
            config = PagingConfig(
                pageSize = PostRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = EventRemoteMediator(database = database, service = goApi),
        ) {
            eventDao.pagingSourceFeed()
        }.flow
    }

    fun getEvents(): Flow<List<Event>> {
        return eventDao.getEvents()
    }

    suspend fun refreshMyEvents() = withContext(Dispatchers.IO) {
        try {
            val myEvents = goApi.getEvents(0, 10)
            Log.d("DORA", myEvents.toString())
            eventDao.insertAll(myEvents)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateEvent(event: Event) = withContext(Dispatchers.IO) {
        goApi.updateEvent(event = event)
    }

    private suspend fun uninterestEvent(eventId: String) {
        goApi.uninterestEvent(eventId = eventId)
    }

    private suspend fun interestEvent(eventId: String) {
        goApi.interestEvent(eventId = eventId)
    }

    suspend fun hideEvent(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.deleteWithId(eventId = eventId)
    }

    suspend fun deleteEvent(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.deleteWithId(eventId = eventId)
        goApi.deleteEvent(eventId = eventId)
    }

    suspend fun uploadEvent(event: Event) {
        goApi.createEvent(event = event)
    }

    suspend fun toggleInterested(eventId: String) {
        eventDao.toggleInterested(eventId = eventId)
    }

    suspend fun toggleInterested(event: Event) {
        eventDao.update(event.copy(interested = !event.interested))
        if (event.interested)
            uninterestEvent(event.id)
        else
            interestEvent(event.id)
    }

    fun createEvent(
        event: Event,
    ) {
        event.apply {
            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadEventWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "NAME" to name,
                        "ADDRESS" to address,
                        "DESCRIPTION" to description,
                        "EVENT_PRIVACY" to privacy.name,
                        "IMAGE_URI" to imageUrl,
                        "START_DATETIME" to startDate,
                        "END_DATETIME" to endDate,
                        "LOCATION_NAME" to locationName,
                        "LATITUDE" to latitude,
                        "LONGITUDE" to longitude,
                    )
                )
                .build()

            workManager.enqueue(uploadWorkRequest)
        }
    }

    companion object { }
}