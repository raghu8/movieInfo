package com.bill.movieinfo.service

import com.bill.movieinfo.model.Actor
import com.bill.movieinfo.model.Movie
import com.bill.movieinfo.repository.ActorRepository
import com.bill.movieinfo.response.ActorInMovieException
import com.bill.movieinfo.response.ActorNotFoundException
import com.bill.movieinfo.response.DuplicateActorException
import org.springframework.stereotype.Service

@Service
class ActorService(private val actorRepository: ActorRepository) {
    fun getAllActors(): MutableIterable<Actor> {
        val actors = actorRepository.findAll()
        if (actors.toList().isEmpty()) {
            throw ActorNotFoundException("No actors found")
        }
        return actors
    }

    fun getActorById(id: Long): Actor {
        return actorRepository.findById(id).orElseThrow { ActorNotFoundException("Actor not found") }
    }

    fun createActor(actor: Actor): Actor {
        if (actorRepository.findActorByName(actor.name).isPresent) {
            throw DuplicateActorException("Actor already exists")
        }
        return actorRepository.save(actor)
    }

    fun updateActor(id: Long, updatedActor: Actor): Actor {
        val actor = actorRepository.findById(id).orElseThrow { ActorNotFoundException("Actor does not exist") }

        if (updatedActor.name != actor.name && actorRepository.findActorByName(updatedActor.name).isPresent) {
            throw DuplicateActorException("Actor already exists")
        }

        val updatedActorObject = actor.copy(name = updatedActor.name)
        return actorRepository.save(updatedActorObject)
    }

    fun deleteActor(id: Long) {
        val actor = actorRepository.findById(id).orElseThrow { ActorNotFoundException("Actor not found") }

        if (actorRepository.findMoviesByActorId(id).isNotEmpty()) {
            throw ActorInMovieException("Actor is in a movie and cannot be deleted")
        }
        actorRepository.delete(actor)
    }

    fun getMoviesByActor(actorId: Long): List<Movie> {
        return actorRepository.findMoviesByActorId(actorId)
    }
}