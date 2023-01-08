package basic.otuskotlin.otus.ru

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import basic.otuskotlin.otus.ru.plugins.*
import basic.otuskotlin.otus.ru.repo.CatsRepositoryInMemory
import basic.otuskotlin.otus.ru.repo.ICatsRepository

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(
    repository: ICatsRepository? = null,
) {

    // инициализация репозитория для доступа к данным
    val repo = repository ?: CatsRepositoryInMemory()

    configureHTTP()
    configureSerialization()
    configureRouting(repo)
}
