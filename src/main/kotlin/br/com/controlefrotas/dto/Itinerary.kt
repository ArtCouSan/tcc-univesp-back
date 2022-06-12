package br.com.controlefrotas.dto

data class Itinerary (
    val ordem: Integer,
    val rota: String,
    val horarios: ArrayList<String>)