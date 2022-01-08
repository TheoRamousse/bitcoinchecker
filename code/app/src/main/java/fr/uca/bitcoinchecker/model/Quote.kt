package fr.uca.bitcoinchecker.model

data class Quote(var id : String, var name: String, var symbol: String, var imageUrl: String, var currentPrice: Double) {
}