package com.example.medscape20.data.mapper

import com.example.medscape20.data.remote.dto.user.collector.home.CustomerHomePeopleCountResDto
import com.example.medscape20.domain.models.CustomerHomePeopleCountModel

fun List<CustomerHomePeopleCountResDto>.toPeopleCountModel(
    state: String,
    city: String
): CustomerHomePeopleCountModel {
    var totalCount = 0
    var stateCount = 0
    var cityCount = 0
    this.forEach {
        if (it.dump == true) {
            totalCount += 1
            if (it.state == state) stateCount += 1
            if (it.city == city) cityCount += 1
        }

    }
    return CustomerHomePeopleCountModel(cityCount, stateCount, totalCount)
}