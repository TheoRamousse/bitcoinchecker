package fr.uca.bitcoinchecker.utils.api.json_converter

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fr.uca.bitcoinchecker.model.dto.QuoteDTO
import fr.uca.bitcoinchecker.model.Quote

class JsonToQuotesConverter{
    companion object  : JsonConverter<Quote, QuoteDTO> {
        override suspend fun convertUniqueItem(json : String) : Quote {
            val mapper = jacksonObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            var quoteDTO : QuoteDTO = mapper.readValue(json, QuoteDTO::class.java)
            return convertDTOToModel(quoteDTO)
        }

        override suspend fun convertDTOToModel(q: QuoteDTO) : Quote {
            return Quote(q.id, q.name, q.symbol, q.image.large, q.market_data.current_price.usd)
        }
    }
}