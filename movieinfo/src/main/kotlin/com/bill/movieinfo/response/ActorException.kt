package com.bill.movieinfo.response

class DuplicateActorException(message: String) : RuntimeException(message)

class MissingActorsException(message: String) : RuntimeException(message)

class ActorNotFoundException(message: String): RuntimeException(message)

class ActorInMovieException(message: String): RuntimeException(message)