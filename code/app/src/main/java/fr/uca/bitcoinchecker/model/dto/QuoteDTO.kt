package fr.uca.bitcoinchecker.model.dto

data class QuoteDTO(var id : String, var name : String, var symbol: String, var image: LargeImageItem, var market_data: MarketData) {

    data class LargeImageItem(var large: String) {}

    data class MarketData(var current_price : CurrentPrice) {
        data class CurrentPrice(var usd : Double) {
        }

    }
}