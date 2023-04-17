package com.bill.movieinfo.repository

import com.bill.movieinfo.model.Actor
import com.bill.movieinfo.model.Movie
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface ActorRepository : CrudRepository<Actor, Long> {
    fun findActorByName(name: String): Optional<Actor>

    @Query("SELECT m FROM Movie m JOIN m.actors a WHERE a.name = :actorId")
    fun findMoviesByActorId(@Param("actorId") actorId: Long): List<Movie>

}