package com.app.controller

import com.app.service.IWsCalculationService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.flow.channelFlow

@Controller("/rest/flow/calculation")
class CalculationFlowController(private val calculationService: IWsCalculationService) {

    @Post(value = "/company/all-parallel", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun getFlowCalculation() = channelFlow {
        calculationService.getAllCalculationsWithErrorMessages(channel)
    }
}
