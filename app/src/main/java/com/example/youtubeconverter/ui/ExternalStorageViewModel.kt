package com.example.youtubeconverter.ui

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubeconverter.data.ExternalStorageRepository
import com.example.youtubeconverter.data.ExternalStorageService
import com.example.youtubeconverter.data.SongItem
import com.example.youtubeconverter.data.StorageLoadingStatus
import kotlinx.coroutines.launch

class ExternalStorageViewModel : ViewModel() {
    private val repository = ExternalStorageRepository(ExternalStorageService())

    private val _searchResults = MutableLiveData<List<SongItem>>()
    val searchResults: LiveData<List<SongItem>> = _searchResults

    private val _loadingStatus = MutableLiveData(StorageLoadingStatus.SUCCESS)
    val loadingStatus: LiveData<StorageLoadingStatus> = _loadingStatus

    fun loadSearchResults(contentResolver: ContentResolver){
        viewModelScope.launch {
            _loadingStatus.value = StorageLoadingStatus.LOADING
            val result = repository.loadExternalStorageResults(contentResolver)
            _searchResults.value = result.getOrNull()
            _loadingStatus.value = when (result.isSuccess){
                true -> StorageLoadingStatus.SUCCESS
                false -> StorageLoadingStatus.FAILURE
            }
            if (_searchResults.value == null || _searchResults.value!!.isEmpty()){
                _loadingStatus.value = StorageLoadingStatus.FAILURE
            }
        }
    }
}