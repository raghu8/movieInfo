
 
 
 # Movie Information application

This is a spring boot application that performs CRUD operations on a movie database. This database has a movie that contains titile, release date, a list of actor and a separate table for actors.

Environment:

 jdk: coretto-17.0.6 

 db: H2


Note: Since it's an in memory database. Tables will not have data initially. Insert seed data via the post movies endpoint. Example listed below

# Endpoints and sample payloads:

db console endpoint: http://localhost:8080/h2-console


# Post movies endpoint: localhost:8080/api/movies 
![img.png](img.png)



# Put movies endpoint: localhost:8080/api/movies/1

![img_1.png](img_1.png)


# Get movies endpoint: localhost:8080/api/movies/1

![img_2.png](img_2.png)


# Get movies actor are in endpoint:http://localhost:8080/api/movies/actors/2/movies

![img_3.png](img_3.png)




# Post actors endpoint: localhost:8080/api/actors
![img_5.png](img_5.png)



# Update actors endpoint: localhost:8080/api/1

![img_6.png](img_6.png)


# Get actors endpoint: localhost:8080/api/actors/1

![img_8.png](img_8.png)

