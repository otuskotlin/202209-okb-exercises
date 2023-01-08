package basic.otuskotlin.otus.ru.model

import kotlinx.serialization.Serializable

@Serializable
data class CatCardModel(
    val id: String? = null,
    val name: String? = null,
    val birthDate: String? = null,
    val creationDt: Long? = null,
    val colors: List<String>? = null,
    val imgUrl: String? = null,
)
