package fr.uca.bitcoinchecker.utils.api.json_converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestion
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestionDTO

class JsonToListOfQuoteSuggestionsConverter{
    companion object  : JsonConverter<List<QuoteSuggestion>, List<QuoteSuggestionDTO>> {
        override suspend fun convertUniqueItem(json : String) : List<QuoteSuggestion> {
            val mapper = jacksonObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            val quoteSuggestionDTO : List<QuoteSuggestionDTO> = mapper.readValue(json, object : TypeReference<List<QuoteSuggestionDTO>>(){})
            return convertDTOToModel(quoteSuggestionDTO)
        }

        override suspend fun convertDTOToModel(q: List<QuoteSuggestionDTO>) : List<QuoteSuggestion> {
            val result = mutableListOf<QuoteSuggestion>()
            q.forEach {
                result.add(QuoteSuggestion(it.id, it.name, it.symbol))
            }
            return result
        }
    }
}