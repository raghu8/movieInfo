package com.bill.movieinfo.model

import jakarta.persistence.*

@Entity
data class Actor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String
)
