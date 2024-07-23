package com.example.medscape20.presentation.screens.user.customer.home.sources.sources_desc

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.medscape20.data.remote.dto.user.customer.home.sources.Subtopic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SourcesDescStates(
    val title: String = "",
    val image: String? = null,
    val sourcesDesc: List<Subtopic> = emptyList()
)

@HiltViewModel
class SourcesDescViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SourcesDescStates())
    val state: StateFlow<SourcesDescStates> = _state.asStateFlow()

    fun event(action: SourcesDescEvents) {
        when (action) {

            is SourcesDescEvents.SetSourcesData -> {
                val subTopicList = action.data.subtopics.toMutableList()
                subTopicList.add(0, Subtopic( action.data.description,"Definition"))
                _state.update {
                    it.copy(
                        title = action.data.topic,
                        image = action.data.image,
                        sourcesDesc = subTopicList
                    )
                }
            }
        }
    }

}