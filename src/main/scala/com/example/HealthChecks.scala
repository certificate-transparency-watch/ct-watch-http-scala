package com.example

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheckRegistry

class HealthChecks(healthCheckRegistry: HealthCheckRegistry) {
  healthCheckRegistry.register("foo", new Foo)
}

class Foo extends HealthCheck {
  override def check(): HealthCheck.Result = HealthCheck.Result.healthy("good")
}