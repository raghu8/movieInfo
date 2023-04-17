package com.bill.movieinfo.controller

import com.bill.movieinfo.model.Actor
import com.bill.movieinfo.response.ActorNotFoundException
import com.bill.movieinfo.response.DuplicateActorException
import com.bill.movieinfo.service.ActorService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/actors")
class ActorInfoController(private val actorService: ActorService) {
    private val logger: Logger = LogManager.getLogger(ActorInfoController::class.java)

    @GetMapping
    fun getAllActors(): MutableIterable<Actor> {
        logger.info("Getting all actors")
        return actorService.getAllActors()
    }

    @GetMapping("/{id}")
    fun getActorById(@PathVariable id: Long): ResponseEntity<Actor> {
        logger.info("Getting actor by ID: $id")
        return try {
            val actor = actorService.getActorById(id)
            ResponseEntity.ok(actor)
        } catch (exception: ActorNotFoundException) {
            logger.error("Actor not found: $id")
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createActor(@RequestBody actor: Actor): ResponseEntity<Any> {
        try {
            actorService.createActor(actor)
            return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED.reasonPhrase)
        } catch (exception: DuplicateActorException) {
            val message = exception.message
            val response = mapOf("data" to actor, "message" to message, "statusCode" to HttpStatus.CONFLICT.value())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        } catch (exception: Exception) {
            val message = exception.message
            val response = mapOf("data" to actor, "message" to message, "statusCode" to HttpStatus.BAD_REQUEST.value())
            return ResponseEntity.badRequest().body(response)
        }
    }

    @PutMapping("/{id}")
    fun updateActor(@PathVariable id: Long, @RequestBody updatedActor: Actor): ResponseEntity<Any> {
        try {
            actorService.updateActor(id, updatedActor)
            logger.info("Updated actor with ID: $id")
            return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED.reasonPhrase)
        } catch (exception: ActorNotFoundException) {
            val response = mapOf(
                "data" to updatedActor,
                "message" to exception.message,
                "statusCode" to HttpStatus.NOT_FOUND.value()
            )
            logger.error("Error updating actor with ID $id: $response")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        } catch (exception: Exception) {
            val message = exception.message
            val response = mapOf(
                "data" to updatedActor,
                "message" to message,
                "statusCode" to HttpStatus.BAD_REQUEST.value()
            )
            logger.error("Error updating actor with ID $id: $response")
            return ResponseEntity.badRequest().body(response)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteActor(@PathVariable id: Long): ResponseEntity<Any> {
        try {
            actorService.deleteActor(id)
            logger.info("Deleted actor with ID: $id")
            return ResponseEntity.noContent().build()
        } catch (exception: ActorNotFoundException) {
            val message = exception.message
            val response = mapOf("message" to message, "statusCode" to HttpStatus.NOT_FOUND.value())
            logger.error("Error deleting actor with ID $id: $response")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        } catch (exception: RuntimeException) {
            val message = exception.message
            val response = mapOf("message" to message, "statusCode" to HttpStatus.BAD_REQUEST.value())
            logger.error("Error deleting actor with ID $id: $response")
            return ResponseEntity.badRequest().body(response)
        }
    }
}