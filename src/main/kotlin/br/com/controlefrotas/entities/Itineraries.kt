package br.com.controlefrotas.entities

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "itinerarios")
data class Itineraries (
    @Id
    val _id: ObjectId = ObjectId.get(),
    val placa: String,
    val identificacao: String,
    val itinerario: List<Itinerary>)