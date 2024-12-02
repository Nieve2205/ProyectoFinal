package com.example.tarpuy.model

data class RequestModel(val contents: List<Parts>)
data class Parts(val parts: List<Text>)
data class Text(val text: String)
data class ResponseModel(val contents: List<Content>)
data class Content(val parts: List<PartResponse>)
data class PartResponse(val text: String)
