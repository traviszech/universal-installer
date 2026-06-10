package app.pwhs.tv.presentation.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.pwhs.core.data.AppRepository
import app.pwhs.core.domain.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository(application.applicationContext)

    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val apps = _apps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val installedApps = withContext(Dispatchers.IO) { repo.getInstalledApps(includeSystem = false) }
            _apps.value = installedApps
            _isLoading.value = false
        }
    }
}
