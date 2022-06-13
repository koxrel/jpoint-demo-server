package com.app.controller

import com.app.service.CalculationService
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map

@Controller("/rest/flow/calculation")
class CalculationFlowController(
    private val calculationService: CalculationService,
    private val objectMapper: ObjectMapper
) {
    @Post(value = "/company/all-parallel", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun getCalculation() = channelFlow {
        calculationService.getAllCalculationsWithErrorPlaceholders(channel)
    }.map { objectMapper.writeValueAsString(it) + "\n" }
}
