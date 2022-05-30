
package br.com.controlefrotas.repository

import br.com.controlefrotas.entities.RegistrationData
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistrationDataRepository : MongoRepository<RegistrationData, String> {
    fun findByIdentificacao(identificacao: String) : RegistrationData;
}