import com.bill.movieinfo.controller.MovieInfoController
import com.bill.movieinfo.model.Actor
import com.bill.movieinfo.model.Movie
import com.bill.movieinfo.repository.MovieRepository
import com.bill.movieinfo.response.MovieNotFoundException
import com.bill.movieinfo.service.ActorService
import com.bill.movieinfo.service.MovieService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

import org.mockito.Mockito.*
import org.springframework.http.ResponseEntity
import java.util.*

class MovieInfoControllerTest {

    private val movieService = mock(MovieService::class.java)
    private val movieRepository = mock(MovieRepository::class.java)
    private val actorService = mock(ActorService::class.java)
    private val movieController = MovieInfoController(movieRepository, movieService, actorService)

    @Test
    fun `createMovie should return CREATED status code and reason phrase when movie is created successfully`() {
        val actors = mutableListOf(
            Actor(id = 1, name = "John Doe"),
            Actor(id = 2, name = "Jane Smith")
        )
        val movie = Movie(title = "The Matrix", releaseDate = "1999-03-31", actors = actors)
        `when`(movieService.createMovie(movie)).thenReturn(movie)

        val response = movieController.createMovie(movie)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(HttpStatus.CREATED.reasonPhrase, response.body)
        verify(movieService, times(1)).createMovie(movie)
    }

    @Test
    fun `createMovie should return BAD_REQUEST status code and error message when movie creation fails`() {
        val actors = mutableListOf(
            Actor(id = 1, name = "John Doe"),
            Actor(id = 2, name = "Jane Smith")
        )
        val movie = Movie(title = "The Matrix", releaseDate = "1999-03-31", actors = actors)
        val errorMessage = "Duplicate entry for title"
        `when`(movieService.createMovie(movie)).thenThrow(RuntimeException(errorMessage))

        val response = movieController.createMovie(movie)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val responseBody = response.body as Map<*, *>
        assertEquals(movie, responseBody["data"])
        assertEquals(errorMessage, responseBody["message"])
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody["statusCode"])
        verify(movieService, times(1)).createMovie(movie)
    }

    @Test
    fun `updateMovie should return BAD_REQUEST status code and error message when movie update fails`() {
        val movieId = 1L
        val updatedMovie = Movie(title = "The Matrix Reloaded", releaseDate = "2003-05-15")
        val errorMessage = "Movie not found"
        `when`(movieService.updateMovie(movieId, updatedMovie)).thenThrow(RuntimeException(errorMessage))

        val result = movieController.updateMovie(movieId, updatedMovie)

        val expected = mapOf("data" to updatedMovie, "message" to errorMessage, "statusCode" to 400)
        val actual = result.body as Map<*, *>
        assertEquals(expected, actual)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)

        verify(movieService, times(1)).updateMovie(movieId, updatedMovie)
    }

    @Test
    fun `updateMovie should return CREATED status code and success message when movie update succeeds`() {
        val movieId = 1L
        val updatedMovie = Movie(title = "The Matrix Reloaded", releaseDate = "2003-05-15")
        val movie = Movie(id = movieId, title = "The Matrix", releaseDate = "1999-03-31")
        `when`(movieService.updateMovie(movieId, updatedMovie)).thenReturn(movie)

        val response = movieController.updateMovie(movieId, updatedMovie)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(HttpStatus.CREATED.reasonPhrase, response.body)
        verify(movieService, times(1)).updateMovie(movieId, updatedMovie)
    }

    @Test
    fun `deleteMovie should return NO_CONTENT status code when movie is deleted successfully`() {
        val movieId = 1L
        val movie = Movie(id = movieId, title = "The Matrix", releaseDate = "1999-03-31")
        `when`(movieRepository.findById(movieId)).thenReturn(Optional.of(movie))
        doNothing().`when`(movieService).deleteMovie(movieId)

        val response = movieController.deleteMovie(movieId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        verify(movieService, times(1)).deleteMovie(movieId)
    }

    @Test
    fun `deleteMovie should return NOT_FOUND status code and error message when movie is not found`() {
        val movieId = 1L
        val errorMessage = "Movie not found"
        `when`(movieRepository.findById(movieId)).thenReturn(Optional.empty())

        val result = movieController.deleteMovie(movieId)
        val actual = result.body as Map<*, *>
        val message = actual["message"]
        verify(movieRepository, times(1)).findById(movieId)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(errorMessage, message)
    }

    @Test
    fun `test getMovieById when movie exists`() {
        val movieId = 1L
        val movie = Movie(id = movieId, title = "The Godfather", releaseDate = "1972-03-24")
        movieController.createMovie(movie)
        assertNotNull(movieController.getMovieById(movieId))
    }

}