package com.example.youtubeconverter.data

import android.content.ContentResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExternalStorageRepository(
    private val service: ExternalStorageService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadExternalStorageResults(contentResolver: ContentResolver) : Result<List<SongItem>> =
        withContext(ioDispatcher){
            try {
                val results = service.searchExternalStorage(contentResolver)
                Result.success(results)
            }catch (e: Exception){
                Result.failure(e)
            }
        }
}