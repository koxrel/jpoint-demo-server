package com.insurance.conf

import io.micronaut.context.annotation.ConfigurationInject
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("delays")
data class InsuranceDelays @ConfigurationInject constructor (val first: Long, val second: Long, val third: Long)
