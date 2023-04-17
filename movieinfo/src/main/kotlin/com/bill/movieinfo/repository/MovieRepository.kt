package com.bill.movieinfo.repository

import com.bill.movieinfo.model.Movie
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MovieRepository : CrudRepository<Movie, Long> {
    fun findByTitleAndReleaseDate(title: String, releaseDate: String): Optional<Movie>
    fun findByActorsName(actor:String): List<Movie>
}