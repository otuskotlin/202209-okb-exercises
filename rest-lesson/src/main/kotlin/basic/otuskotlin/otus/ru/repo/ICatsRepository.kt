package basic.otuskotlin.otus.ru.repo

import basic.otuskotlin.otus.ru.model.CatCardModel

/**
 * Интерфейс репозитория, регламентирующий методы доступа к данным
 */
interface ICatsRepository {

    suspend fun create(cat: CatCardModel): CatCardModel

    suspend fun read(id: String): CatCardModel?

    suspend fun update(cat: CatCardModel): CatCardModel

    suspend fun delete(id: String): CatCardModel?

    suspend fun index(offset: Int, count: Int): List<CatCardModel>
}
