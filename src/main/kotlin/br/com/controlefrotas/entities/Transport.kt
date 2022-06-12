package br.com.controlefrotas.entities

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "onibus")
data class Transport(
    @Id
    val _id: ObjectId = ObjectId.get(),
    val placa: String,
    val estaLotado: Boolean,
    val dtAlteracao: LocalDateTime,
    val status: String,
    val uf: String,
    val sentidoIda: String,
    val sentidoVolta: String,
    val identificacao: String,
    val limite: Integer,
    val idRota: Integer
)
