package fr.uca.bitcoinchecker.utils.api.json_converter

interface JsonConverter<T: Any, DTO : Any> {
    fun convertUniqueItem(json : String) : T
    fun convertDTOToModel(q: DTO) : T
}