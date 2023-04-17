package com.bill.movieinfo.controller

import com.bill.movieinfo.model.Movie
import com.bill.movieinfo.repository.MovieRepository
import com.bill.movieinfo.response.DuplicateMovieException
import com.bill.movieinfo.response.MovieNotFoundException
import com.bill.movieinfo.service.ActorService
import com.bill.movieinfo.service.MovieService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/movies")
class MovieInfoController(
    val movieRepository: MovieRepository,
    val movieService: MovieService,
    val actorService: ActorService) {
    private val logger: Logger = LogManager.getLogger(MovieInfoController::class.java)

    @PostMapping
    fun createMovie(@RequestBody movie: Movie): ResponseEntity<Any> {
        logger.info("Creating movie with data: $movie")
        try {
            movieService.createMovie(movie)
            return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED.reasonPhrase)
        }catch (exception: DuplicateMovieException) {
            val message = exception.message
            val response = mapOf("data" to movie, "message" to message, "statusCode" to HttpStatus.CONFLICT.value())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        }catch(exception:Exception){
            val message = exception.message
            val response = mapOf("data" to movie, "message" to message, "statusCode" to HttpStatus.BAD_REQUEST.value())
            logger.error("Error occurred while creating movie: $response")
            return ResponseEntity.badRequest().body(response)
        }
    }

    @PutMapping("/{id}")
    fun updateMovie(@PathVariable id: Long, @RequestBody updatedMovie: Movie): ResponseEntity<Any> {
        logger.info("Updating movie with id $id and data: $updatedMovie")
        try {
            movieService.updateMovie(id, updatedMovie)
            return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED.reasonPhrase)
        }catch (exception: MovieNotFoundException) {
            val response = mapOf(
                "data" to updatedMovie,
                "message" to exception.message,
                "statusCode" to HttpStatus.NOT_FOUND.value()
            )
            logger.error("Error updating movie with ID $id: $response")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }catch(exception:Exception){
            val message = exception.message
            val response = mapOf("data" to updatedMovie, "message" to message, "statusCode" to HttpStatus.BAD_REQUEST.value())
            logger.error("Error occurred while updating movie: $response")
            return ResponseEntity.badRequest().body(response)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMovie(@PathVariable id: Long): ResponseEntity<Any> {
        logger.info("Deleting movie with id: $id")
        try {
            val movie = movieRepository.findById(id).orElseThrow { MovieNotFoundException("Movie not found") }
            movieService.deleteMovie(movie.id)
            return ResponseEntity.noContent().build()
        } catch (exception: MovieNotFoundException) {
            val message = exception.message
            val response = mapOf("message" to message, "statusCode" to HttpStatus.NOT_FOUND.value())
            logger.error("Error deleting movie with ID $id: $response")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }catch (ex: RuntimeException) {
            val message = ex.message
            val response = mapOf("message" to message, "statusCode" to HttpStatus.BAD_REQUEST.value())
            logger.error("Error occurred while deleting movie: $response")
            return ResponseEntity.badRequest().body(response)
        }
    }

    @GetMapping("/{id}")
    fun getMovieById(@PathVariable id:Long): ResponseEntity<Movie> {
        logger.info("Getting movie by ID: $id")
        try {
            val movie = movieService.getMovieById(id)
            return ResponseEntity.ok(movie)
        } catch (exception: MovieNotFoundException) {
            logger.error("Movie not found: $id")
            return ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/actors/{actorId}/movies")
    fun getMoviesByActor(@PathVariable actorId: Long): List<Movie> {
        logger.info("Getting movie by ID: $actorId")
        val actor = actorService.getActorById(actorId)
        return movieService.getMoviesByActor(actor)
    }
}

