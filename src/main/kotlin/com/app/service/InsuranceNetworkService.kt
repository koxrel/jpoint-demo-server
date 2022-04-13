package com.app.service

import com.app.model.InsuranceCompany
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

interface InsuranceNetworkService {
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): String
}

@Singleton
class InsuranceNetworkServiceImpl(@Value("\${insurance-companies.host}") private val host: String) : InsuranceNetworkService {
    private val httpClient = HttpClient.newHttpClient()

    override suspend fun getCalculation(insuranceCompany: InsuranceCompany): String {
        val request = HttpRequest.newBuilder(URI("$host/${insuranceCompany.name.lowercase()}")).GET().build()

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await().body()
    }
}
