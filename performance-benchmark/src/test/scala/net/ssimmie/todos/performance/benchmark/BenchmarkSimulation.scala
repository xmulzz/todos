package net.ssimmie.todos.performance.benchmark

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class BenchmarkSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8181")
    .acceptHeader("application/json")

  val loadBalancers: ScenarioBuilder = scenario("Todos Actuator")
    .exec(HealthCheckAction.healthcheck)

  setUp(
    loadBalancers.inject(constantUsersPerSec(10) during (30 seconds))
  ).protocols(
    httpProtocol
  ).assertions(
    global.responseTime.percentile3.lte(10),
    global.responseTime.percentile4.lt(20),
    global.successfulRequests.percent.is(100)
  )
}
