package br.com.controlefrotas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
@EnableMongoRepositories
@SpringBootApplication
class ControleFrotasApplication

fun main(args: Array<String>) {
    runApplication<ControleFrotasApplication>(*args)
}
