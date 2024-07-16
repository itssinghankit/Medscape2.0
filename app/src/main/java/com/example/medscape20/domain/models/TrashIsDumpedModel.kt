package com.example.medscape20.domain.models

data class TrashIsDumpedModel(
    val metal: Boolean,
    val general: Boolean,
    val medical: Boolean,
    val plastic: Boolean,
    val dump: Boolean,
)
