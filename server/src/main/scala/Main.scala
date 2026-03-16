package your_name.project_name.server

import cats.effect.{IO, Resource, ResourceApp}
import cats.syntax.all.*
import sttp.tapir.server.netty.cats.NettyCatsServer
import your_name.project_name.server.config.Env
import your_name.project_name.server.services.CoreService

object Main extends ResourceApp.Forever:

  /**
    * The main entry point for this application. To run, use "sbt dev".
    *
    * @param args
    *   Unused.
    */
  def run(args: List[String]) =

    NettyCatsServer
      .io()
      .flatMap: server =>
        val service = server
          .host(Env.HOST)
          .port(Env.PORT)
          .addEndpoints(CoreService.all)
        Resource.make(service.start())(_.stop()).as(())
