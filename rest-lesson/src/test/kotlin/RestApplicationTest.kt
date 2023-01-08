import basic.otuskotlin.otus.ru.model.CatCardModel
import basic.otuskotlin.otus.ru.module
import basic.otuskotlin.otus.ru.repo.CatsRepositoryInMemory
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class RestApplicationTest {
    private val json = Json {
        prettyPrint = true
    }

    @Test
    fun create() = testApplication {
        application { module() }
        val response = client.post("/cat/create") {
            val requestObj = stubModel
            contentType(ContentType.Application.Json)
            val requestJson = json.encodeToString(CatCardModel.serializer(), requestObj)
            setBody(requestJson)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseJson = response.bodyAsText()
        val responseModel = json.decodeFromString(CatCardModel.serializer(), responseJson)
        assertNotEquals(stubId, responseModel.id)
        assertNotNull(responseModel.creationDt)
        assertEquals(stubModel.name, responseModel.name)
        assertEquals(stubModel.birthDate, responseModel.birthDate)
        assertEquals(stubModel.colors, responseModel.colors)
    }

    @Test
    fun readOK() = testApplication {
        application {
            module(repository = CatsRepositoryInMemory(listOf(stubModel)))
        }
        val response = client.get("/cat/read/$stubId")

        assertEquals(HttpStatusCode.OK, response.status)
        val responseJson = response.bodyAsText()
        val responseModel = json.decodeFromString(CatCardModel.serializer(), responseJson)
        assertEquals(stubId, responseModel.id)
        assertNotNull(responseModel.creationDt)
        assertEquals(stubModel.name, responseModel.name)
        assertEquals(stubModel.birthDate, responseModel.birthDate)
        assertEquals(stubModel.colors, responseModel.colors)
    }

    @Test
    fun readFail() = testApplication {
        application { module() }
        val response = client.get("/cat/read/$stubId")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun update() = testApplication {
        application {
            module(repository = CatsRepositoryInMemory(listOf(stubModel)))
        }
        val response = client.post("/cat/update") {
            val requestObj = stubModel.copy(colors = listOf("black"))
            contentType(ContentType.Application.Json)
            val requestJson = json.encodeToString(CatCardModel.serializer(), requestObj)
            setBody(requestJson)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseJson = response.bodyAsText()
        val responseModel = json.decodeFromString(CatCardModel.serializer(), responseJson)
        assertEquals(stubId, responseModel.id)
        assertEquals(stubModel.name, responseModel.name)
        assertEquals(stubModel.birthDate, responseModel.birthDate)
        assertEquals(listOf("black"), responseModel.colors)
    }

    @Test
    fun deleteOK() = testApplication {
        application {
            module(repository = CatsRepositoryInMemory(listOf(stubModel)))
        }
        val response = client.get("/cat/delete/$stubId")

        assertEquals(HttpStatusCode.OK, response.status)
        val responseJson = response.bodyAsText()
        val responseModel = json.decodeFromString(CatCardModel.serializer(), responseJson)
        assertEquals(stubId, responseModel.id)
        assertNotNull(responseModel.creationDt)
        assertEquals(stubModel.name, responseModel.name)
        assertEquals(stubModel.birthDate, responseModel.birthDate)
        assertEquals(stubModel.colors, responseModel.colors)
    }

    @Test
    fun deleteFail() = testApplication {
        application { module() }
        val response = client.get("/cat/delete/$stubId")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun index() = testApplication {
        application {
            module(
                repository = CatsRepositoryInMemory(
                    listOf(
                        stubModel,
                        stubModel.copy(id = "test-id-2"),
                        stubModel.copy(id = "test-id-3"),
                        stubModel.copy(id = "test-id-4"),
                        stubModel.copy(id = "test-id-5"),
                    )
                )
            )
        }
        val response = client.get("/cat/index?offset=1&count=3")

        assertEquals(HttpStatusCode.OK, response.status)
        val responseJson = response.bodyAsText().byteInputStream()
        val responseList = json.decodeToSequence<CatCardModel>(responseJson).toList()
        assertEquals(3, responseList.count())
        assertEquals("test-id-2", responseList.first().id)
        assertEquals("test-id-4", responseList.last().id)
    }

    companion object {
        val stubId = "test-id-1"
        val stubModel = CatCardModel(
            id = stubId,
            name = "Barsik",
            birthDate = "2020-02-02",
            colors = listOf("red", "white"),
        )
    }
}
