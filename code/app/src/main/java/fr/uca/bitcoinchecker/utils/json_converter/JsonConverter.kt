package fr.uca.bitcoinchecker.utils.json_converter

interface JsonConverter<T: Any, DTO : Any> {
    fun convertUniqueItem(json : String) : T
    fun convertDTOToModel(q: DTO) : T
}