package com.app.controller

import com.app.dto.CalculationResponse
import com.app.model.InsuranceCompany
import com.app.service.ICalculationService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/rest/calculation")
class CalculationController(private val calculationService: ICalculationService) {

    @Post("/company/{insuranceCompany}")
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse {
        return calculationService.getCalculation(insuranceCompany)
    }

    @Post("/company/all")
    suspend fun getAllCalculations(): List<CalculationResponse> {
        return calculationService.getAllCalculations()
    }

    @Post("/company/all-parallel")
    suspend fun getAllCalculationsInParallel(): List<CalculationResponse> {
        return calculationService.getAllCalculationsInParallel()
    }
}
