package basic.otuskotlin.otus.ru.repo

import basic.otuskotlin.otus.ru.model.CatCardModel
import java.time.Instant
import java.util.SortedMap
import java.util.UUID

/**
 * Реализация репозитория "в памяти", все данные сохраняются в кэше только на время работы приложения, не stateless
 */
class CatsRepositoryInMemory(
    initObjects: List<CatCardModel> = emptyList(),
): ICatsRepository {
    private val cache: SortedMap<String, CatCardModel> = sortedMapOf()

    init {
        initObjects.forEach {
            val id = it.id ?: UUID.randomUUID().toString()
            cache[id] = it.copy(id = id, creationDt = Instant.now().toEpochMilli())
        }
    }

    override suspend fun create(cat: CatCardModel): CatCardModel {
        val cat2save = cat.copy(
            id = UUID.randomUUID().toString(),
            creationDt = Instant.now().toEpochMilli(),
        )
        cache[cat2save.id] = cat2save
        return cat2save
    }

    override suspend fun read(id: String): CatCardModel? {
        return cache[id]
    }

    override suspend fun update(cat: CatCardModel): CatCardModel {
        cat.id?.let {
            cache[it] = cat
            return cat
        }?: return create(cat)
    }

    override suspend fun delete(id: String): CatCardModel? {
        return cache.remove(id)
    }

    override suspend fun index(offset: Int, count: Int): List<CatCardModel> {
        val cacheSize = cache.size
        val fromIndex = if (offset >= cacheSize) return emptyList() else offset
        val toIndex = if ((offset + count) > cacheSize) cacheSize else (offset + count)
        return cache.values.toList().subList(fromIndex, toIndex)
    }
}
