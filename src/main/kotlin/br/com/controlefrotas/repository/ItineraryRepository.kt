package br.com.controlefrotas.repository

import br.com.controlefrotas.entities.Itineraries
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ItineraryRepository : MongoRepository<Itineraries, String> {
    fun findByIdentificacao(identificacao: String) : Itineraries;
}