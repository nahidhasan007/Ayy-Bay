package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.model.LinkCategory
import com.ayybay.app.domain.repository.LinkRepository
import com.ayybay.app.domain.usecase.AddLinkUseCase
import com.ayybay.app.domain.usecase.DeleteLinkUseCase
import com.ayybay.app.domain.usecase.GetAllLinksUseCase
import com.ayybay.app.domain.usecase.GetLinksByCategoryUseCase
import com.ayybay.app.presentation.mvi.LinkCategoryItem
import com.ayybay.app.presentation.mvi.LinkUiEffect
import com.ayybay.app.presentation.mvi.LinkUiIntent
import com.ayybay.app.presentation.mvi.LinkUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LinkViewModel(
    private val getAllLinksUseCase: GetAllLinksUseCase,
    private val getLinksByCategoryUseCase: GetLinksByCategoryUseCase,
    private val addLinkUseCase: AddLinkUseCase,
    private val deleteLinkUseCase: DeleteLinkUseCase,
    private val linkRepository: LinkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LinkUiState())
    val uiState: StateFlow<LinkUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<LinkUiEffect>()
    val uiEffect: SharedFlow<LinkUiEffect> = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            linkRepository.seedIfEmpty()
            handleIntent(LinkUiIntent.LoadCategories)
        }
    }

    fun handleIntent(intent: LinkUiIntent) {
        when (intent) {
            is LinkUiIntent.LoadCategories -> loadCategories()
            is LinkUiIntent.LoadLinksByCategory -> loadLinksByCategory(intent.category)
            is LinkUiIntent.LoadLinkDetail -> loadLinkDetail(intent.id)
            is LinkUiIntent.AddLink -> addLink(intent.link)
            is LinkUiIntent.DeleteLink -> deleteLink(intent.link)
            is LinkUiIntent.ClearError -> clearError()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                categories = emptyList()
            )

            getAllLinksUseCase().catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load categories"
                )
            }.collect { allLinks ->
                val categoryItems = LinkCategory.values().map { category ->
                    val count = allLinks.count { it.category == category }
                    LinkCategoryItem(category = category, count = count)
                }
                _uiState.value = _uiState.value.copy(
                    categories = categoryItems,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    private fun loadLinksByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                links = emptyList()
            )

            getLinksByCategoryUseCase(category).catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load links"
                )
            }.collect { links ->
                _uiState.value = _uiState.value.copy(
                    links = links,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    private fun loadLinkDetail(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedLink = null
            )

            linkRepository.getLinkById(id).catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load link detail"
                )
            }.collect { link ->
                _uiState.value = _uiState.value.copy(
                    selectedLink = link,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    private fun addLink(link: DailyLink) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                addLinkUseCase(link)
                _uiEffect.emit(LinkUiEffect.ShowToast("Link added successfully"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add link"
                )
            }
        }
    }

    private fun deleteLink(link: DailyLink) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                deleteLinkUseCase(link)
                _uiEffect.emit(LinkUiEffect.ShowToast("Link deleted"))
                _uiEffect.emit(LinkUiEffect.NavigateBack)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete link"
                )
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}