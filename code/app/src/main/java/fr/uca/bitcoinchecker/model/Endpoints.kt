package fr.uca.bitcoinchecker.model

enum class Endpoints(var prefix : String, var suffix: String) {
    API_ENDPOINT_CURRENT_QUOTE("https://api.coingecko.com/api/v3/coins/", "?localization=en&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false"),
    API_ENDPOINT_ALL_QUOTES("https://api.coingecko.com/api/v3/coins/list", "")
}