package com.example.alwayson.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwayson.data.UrlList
import com.example.alwayson.repository.MainRepository
import com.example.alwayson.utils.NetworkHelper
import com.example.alwayson.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class
MainVieModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _urls = MutableLiveData<Resource<UrlList>>()
    val urls: LiveData<Resource<UrlList>>
        get() = _urls

    private val _errorSites = MutableLiveData<Resource<ResponseBody>>()
    //val errorSites: LiveData<Resource<ResponseBody>>
    //get() = _errorSites

    fun postErrorSites(code: String, clientId: String, name: String, domain: String) {
        viewModelScope.launch {
            _errorSites.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.postErrorSites(code, clientId, name, domain).let {
                    if (it.isSuccessful) {
                        _errorSites.postValue(Resource.success(it.body()))
                    } else {
                        _errorSites.postValue(Resource.error(it.errorBody().toString(), null))
                    }
                }
            }/* else {
                _errorSites.postValue(Resource.error("No internet connection", null))
            }*/
        }
    }


    fun fetchUrls(path: String) {
        viewModelScope.launch {
            _urls.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.getUrls(path).let {
                    if (it.isSuccessful) {
                        _urls.postValue(Resource.success(it.body()))
                    } else {
                        _urls.postValue(Resource.error(it.errorBody().toString(), null))
                    }
                }
            } /*else {
                _urls.postValue(Resource.error("No internet connection", null))
            }*/
        }
    }
}