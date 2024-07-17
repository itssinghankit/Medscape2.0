package com.example.medscape20.data.mapper

import com.example.medscape20.data.remote.dto.user.customer.trash.TrashIsDumpedResDto
import com.example.medscape20.domain.models.TrashIsDumpedModel

fun TrashIsDumpedResDto.toTrashIsDumpedModel(): TrashIsDumpedModel {
    return TrashIsDumpedModel(
        metal = this.metal,
        general = this.general,
        medical = this.medical,
        plastic = this.plastic,
        dump = this.dump
    )
}