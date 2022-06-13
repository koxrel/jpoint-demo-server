package com.app.dto

import com.app.model.InsuranceCompany
import java.math.BigDecimal

sealed interface CalculationResponse {
    val isSuccess: Boolean

    val price: BigDecimal?

    val insuranceCompany: InsuranceCompany
}

data class SuccessfulCalculationResponse(
    override val price: BigDecimal,
    override val insuranceCompany: InsuranceCompany
) : CalculationResponse {
    override val isSuccess = true
}

data class UnsuccessfulCalculationReponse(override val insuranceCompany: InsuranceCompany) : CalculationResponse {
    override val isSuccess = false

    override val price = null
}
