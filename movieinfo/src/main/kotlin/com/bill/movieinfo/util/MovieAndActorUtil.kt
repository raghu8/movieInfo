package com.bill.movieinfo.util

import com.bill.movieinfo.model.Actor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MovieAndActorUtil {

    fun checkPayloadForDuplicateActors(actors: MutableList<Actor>): Boolean {
        val setOfActors = mutableSetOf<String>()
        actors.stream().forEach { element ->
            setOfActors.add(formatActorName(element.name))
        }
        return setOfActors.size != actors.size
    }

    fun isStringInLocalDateFormat(releaseDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd")
        try {
            LocalDate.parse(releaseDate, formatter)
            return true
        } catch (ex: DateTimeParseException) {
            return false
        }
    }

    private fun formatActorName(actorName:String):String{
        val formattedActorName = StringBuilder()
        actorName.forEach { element ->
            run {
                if (element.isLetter()) {
                    formattedActorName.append(element.lowercase())
                }
            }
        }
        return formattedActorName.toString()
    }
}