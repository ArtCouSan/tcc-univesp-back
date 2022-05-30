package br.com.controlefrotas.entities

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "dados_cadastrais")
data class RegistrationData(
    @Id
    val _id: ObjectId = ObjectId.get(),
    val placa: String,
    val identificacao: String,
    val dtInicioAtuacao: LocalDateTime)
