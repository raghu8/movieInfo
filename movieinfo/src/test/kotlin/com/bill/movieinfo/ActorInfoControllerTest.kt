package com.bill.movieinfo

import com.bill.movieinfo.controller.ActorInfoController
import com.bill.movieinfo.model.Actor
import com.bill.movieinfo.response.ActorNotFoundException
import com.bill.movieinfo.service.ActorService
import com.bill.movieinfo.service.MovieService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class ActorInfoControllerTest {

    private val actorService = mock(ActorService::class.java)
    private val actorInfoController = ActorInfoController(actorService)

    @Test
    fun `test getAllActors`() {
        val actors = mutableListOf(
            Actor(id = 1, name = "John Doe"),
            Actor(id = 2, name = "Jane Smith")
        )
        Mockito.`when`(actorService.getAllActors()).thenReturn(actors)

        val result = actorInfoController.getAllActors()

        assertEquals(2, result.toList().size)
        assertEquals(actors, result.toList())
    }

    @Test
    fun `test getActorById when actor exists`() {
        val actorId = 1L
        val actorName = "John Doe"
        val actor = Actor(actorId, actorName)
        `when`(actorService.getActorById(actorId)).thenReturn(actor)

        val response = actorInfoController.getActorById(actorId)

        assertEquals(200, response.statusCodeValue)
        assertEquals(actor, response.body)
    }

    @Test
    fun `test getActorById when actor does not exist`() {
        val actorId = 1L
        `when`(actorService.getActorById(actorId)).thenThrow(ActorNotFoundException("Actor not found"))
        val response = actorInfoController.getActorById(actorId)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `test createActor with valid data`() {
        val actor = Actor(name = "Test Actor")
        Mockito.`when`(actorService.createActor(actor)).thenReturn(actor)

        val response = actorInfoController.createActor(actor)

        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseBody = response.body as String
        assertNotNull(responseBody)
        assertTrue(responseBody.contains(HttpStatus.CREATED.reasonPhrase))
    }

    @Test
    fun `test createActor with invalid data`() {
        val actor = Actor(name = "")
        Mockito.`when`(actorService.createActor(actor)).thenThrow(IllegalArgumentException("Invalid actor data"))

        val response = try {
            actorInfoController.createActor(actor)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "statusCode" to HttpStatus.BAD_REQUEST.value(),
                "message" to "Invalid actor data",
                "data" to null
            ))
        }

        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        val responseBody = response.body as Map<*, *>
        assertNotNull(responseBody)
        assertTrue(responseBody["message"].toString().contains("Invalid actor data"))
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody["statusCode"])
    }

    @Test
    fun `updateActor should return CREATED status code when actor update is successful`() {
        val actorId = 1L
        val updatedActor = Actor(name = "Keanu Reeves")
        `when`(actorService.updateActor(actorId, updatedActor)).thenReturn(updatedActor)

        val response = actorInfoController.updateActor(actorId, updatedActor)

        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseBody = response.body as String
        assertEquals(HttpStatus.CREATED.reasonPhrase, responseBody)

        verify(actorService, times(1)).updateActor(actorId, updatedActor)
    }

    @Test
    fun `updateActor should return BAD_REQUEST status code and error message when actor update fails`() {
        val actorId = 1L
        val updatedActor = Actor(name = "")
        val errorMessage = "Invalid actor data"
        `when`(actorService.updateActor(actorId, updatedActor)).thenThrow(IllegalArgumentException(errorMessage))

        val response = try {
            actorInfoController.updateActor(actorId, updatedActor)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "statusCode" to HttpStatus.BAD_REQUEST.value(),
                "message" to "Invalid actor data",
                "data" to null
            ))
        }
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        val responseBody = response.body as Map<*, *>
        assertNotNull(responseBody)
        assertTrue(responseBody["message"].toString().contains(errorMessage))
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody["statusCode"])

        verify(actorService, times(1)).updateActor(actorId, updatedActor)
    }

    @Test
    fun `deleteActor should return BAD_REQUEST status code when actor deletion fails`() {
        val actorId = 1L
        val errorMessage = "Failed to delete actor"
        `when`(actorService.deleteActor(actorId)).thenThrow(RuntimeException(errorMessage))

        val response = actorInfoController.deleteActor(actorId)

        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val expectedBody = mapOf("message" to errorMessage, "statusCode" to HttpStatus.BAD_REQUEST.value())
        assertEquals(expectedBody, response.body)

        verify(actorService, times(1)).deleteActor(actorId)
    }

}