package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.Module
import hse.diploma.cybersecplatform.domain.VulnerabilityRepo
import hse.diploma.cybersecplatform.model.VulnerabilityType
import hse.diploma.cybersecplatform.utils.logD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Module
class HomeScreenViewModel @Inject constructor(
    private val vulnerabilityRepo: VulnerabilityRepo,
) : ViewModel() {
    // TODO: replace with real data
    private val _vulnerabilities = MutableStateFlow<List<Pair<VulnerabilityType, Int>>>(emptyList())
    val vulnerabilities = _vulnerabilities.asStateFlow()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery = _searchQuery.asStateFlow()

    init {
        _vulnerabilities.value = vulnerabilityRepo.getVulnerabilities()
    }

    fun onSearchQueryChange(newSearchQuery: TextFieldValue) {
        _searchQuery.value = newSearchQuery
        searchForVulnerability(newSearchQuery.text)
    }

    private fun searchForVulnerability(query: String) {
        logD(TAG, "Search for vulnerability with query: $query")
        _vulnerabilities.value =
            vulnerabilityRepo.getVulnerabilities().filter { item ->
                vulnerabilityRepo.getTitle(item.first.config.titleTextId)
                    .contains(query, ignoreCase = true)
            }
    }

    companion object {
        private const val TAG = "HomeScreenViewModel"
    }
}
