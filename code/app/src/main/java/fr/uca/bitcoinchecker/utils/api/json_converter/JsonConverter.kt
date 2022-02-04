package fr.uca.bitcoinchecker.utils.api.json_converter

interface JsonConverter<T: Any, DTO : Any> {
    suspend fun convertUniqueItem(json : String) : T
    suspend fun convertDTOToModel(q: DTO) : T
}