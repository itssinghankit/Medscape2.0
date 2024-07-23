package com.example.medscape20.presentation.screens.user.customer.home.sources.sources_desc

import com.example.medscape20.data.remote.dto.user.customer.home.sources.SourcesDto

sealed class SourcesDescEvents {
    data class SetSourcesData(val data:SourcesDto):SourcesDescEvents()
}