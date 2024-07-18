package com.example.medscape20.domain.usecase.user.collector.customers

import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.presentation.screens.user.collector.customers.CustomersFilters
import javax.inject.Inject

class CustomersSetFilterUseCase @Inject constructor(){

    operator fun invoke(
        oldList: List<CustomersResDto>,
        newFilters: CustomersFilters,
        city: String,
        state: String
    ): List<CustomersResDto> {

        //better approach
        return oldList.filter {
            (!newFilters.cityFilter || it.city == city) &&
                    (!newFilters.stateFilter || it.state == state) &&
                    (!newFilters.medicalFilter || it.medical?:false) &&
                    (!newFilters.metalFilter || it.metal?:false) &&
                    (!newFilters.generalFilter || it.general?:false) &&
                    (!newFilters.plasticFilter || it.plastic?:false)
        }

//        return oldList.filter {
//            (if (newFilters.cityFilter) it.city == city else true) &&
//                    (if (newFilters.stateFilter) it.state == state else true) &&
//                    (if (newFilters.medicalFilter) it.medical == true else true) &&
//                    (if (newFilters.metalFilter) it.metal == true else true) &&
//                    (if (newFilters.generalFilter) it.general == true else true) &&
//                    (if (newFilters.plasticFilter) it.plastic == true else true)
//        }
    }
}