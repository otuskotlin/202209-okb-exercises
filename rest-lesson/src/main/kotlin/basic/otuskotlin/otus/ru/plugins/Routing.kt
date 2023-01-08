package basic.otuskotlin.otus.ru.plugins

import basic.otuskotlin.otus.ru.model.CatCardModel
import basic.otuskotlin.otus.ru.repo.ICatsRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    repo: ICatsRepository,
) {
    routing {
        get("/hello") {
            call.respond("Hello")
        }

        route("/cat") {
            post("/create") {
                // Получение тела сообщения с последующей десериализацией
                val rq = call.receive<CatCardModel>()
                // Сохранение данных в репозитории
                val rs = repo.create(rq)
                // Сериализация и отправка ответа
                call.respond(rs)
            }
            get("/read/{id}") {
                val id = call.parameters["id"]
                id?.let {
                    val rs = repo.read(it)
                    rs?.let { response ->
                        call.respond(response)
                    }?: call.respond(status = HttpStatusCode.NotFound, "Not found with id = $it")
                } ?: call.respond(status = HttpStatusCode.BadRequest, "Id must be not null")
            }
            post("/update") {
                val rq = call.receive<CatCardModel>()
                val rs = repo.update(rq)
                call.respond(rs)
            }
            get("/delete/{id}") {
                val id = call.parameters["id"]
                id?.let {
                    val rs = repo.delete(it)
                    rs?.let { response ->
                        call.respond(response)
                    }?: call.respond(status = HttpStatusCode.NotFound, "Not found with id = $it")
                } ?: call.respond(status = HttpStatusCode.BadRequest, "Id must be not null")
            }
            get("/index") {
                val offset = call.parameters["offset"]?.toInt()?: 0
                val count = call.parameters["count"]?.toInt()?: 500
                val rs = repo.index(offset, count)
                call.respond(rs)
            }
        }
    }
}
