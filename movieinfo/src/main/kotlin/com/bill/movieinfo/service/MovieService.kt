package com.bill.movieinfo.service

import com.bill.movieinfo.model.Actor
import com.bill.movieinfo.model.Movie
import com.bill.movieinfo.repository.ActorRepository
import com.bill.movieinfo.repository.MovieRepository
import com.bill.movieinfo.response.*
import org.springframework.stereotype.Service
import com.bill.movieinfo.util.MovieAndActorUtil

@Service
class MovieService(
    val movieRepository: MovieRepository,
    val actorRepository: ActorRepository
) {
    private val movieAndActorUtil = MovieAndActorUtil()

    fun createMovie(movie: Movie): Movie {
        if(movie.title.equals(null)||movie.releaseDate.equals(null)){
            throw MovieAndTitleException("Movie and title can't be null or blank")
        }

        //isStringInLocalDateFormat
        if(!movieAndActorUtil.isStringInLocalDateFormat(movie.releaseDate)){
            throw ReleaseDateException("Release date is not in local date format")
        }

        if (movieAndActorUtil.checkPayloadForDuplicateActors(movie.actors)) {
            throw DuplicateActorException("Duplicate actors listed in request")
        }
        if (movieRepository.findByTitleAndReleaseDate(movie.title, movie.releaseDate).isPresent) {
            throw DuplicateMovieException("Duplicate movie entry")
        }

        if (movie.actors.isEmpty()) {
            throw MissingActorsException("no actors in this movie")
        }

        val actors = mutableListOf<Actor>()
        movie.actors.forEach {
            val existingActor = actorRepository.findActorByName(it.name)
            if (existingActor.isPresent) {
                actors.add(existingActor.get())
            } else {
                actors.add(actorRepository.save(it))
            }
        }
        movie.actors.clear()
        movie.actors.addAll(actors)

        return movieRepository.save(movie)
    }

    fun updateMovie(id: Long, updatedMovie: Movie): Movie {
        val movie = movieRepository.findById(id).orElseThrow { MovieNotFoundException("Movie not found") }

        if (updatedMovie.actors.isEmpty()) {
            throw MissingActorsException("no actors in this movie")
        }

        if (movieAndActorUtil.checkPayloadForDuplicateActors(updatedMovie.actors)) {
            throw DuplicateActorException("Duplicate actors listed in request")
        }


        val actors = mutableListOf<Actor>()
        updatedMovie.actors.forEach {
            val existingActor = actorRepository.findActorByName(it.name)
            if (existingActor.isPresent) {
                actors.add(existingActor.get())
            } else {
                actors.add(actorRepository.save(it))
            }
        }
        updatedMovie.actors.clear()
        updatedMovie.actors.addAll(actors)

        val updatedMovieObject = movie.copy(title = updatedMovie.title, releaseDate = updatedMovie.releaseDate)

        updatedMovieObject.actors.clear()
        updatedMovieObject.actors.addAll(updatedMovie.actors)

        return movieRepository.save(updatedMovieObject)
    }

    fun deleteMovie(id: Long) {
        val movie = movieRepository.findById(id).orElseThrow { MovieNotFoundException("Movie not found") }
        movieRepository.delete(movie)
    }

    fun getMoviesByActor(actor: Actor): List<Movie> {
        return movieRepository.findByActorsName(actor.name)
    }

    fun getMovieById(id: Long): Movie {
        return movieRepository.findById(id).orElseThrow { ActorNotFoundException("Actor not found") }
    }

}