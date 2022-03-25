package com.app.dto

import com.app.model.InsuranceCompany
import java.math.BigDecimal

data class CalculationResponse(val price: BigDecimal, val insuranceCompany: InsuranceCompany)
