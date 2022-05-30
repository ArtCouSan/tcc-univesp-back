package br.com.controlefrotas.repository

import br.com.controlefrotas.entities.Transport
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransportRepository : MongoRepository<Transport, String>  {
    fun findByIdentificacao(identificacao: String) : List<Transport>;
    @Query(value = "{ 'identificacao': ?0 ,'dtAlteracao':{ \$gte: ?1, \$lte: ?2}, 'estaLotado': true}")
    fun findByIdentificacaoAndDtAlteracaoBetween(identificacao: String, dtInicio: LocalDateTime, dtFim: LocalDateTime) : List<Transport>;
}