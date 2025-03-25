# Quad solutions programmeeropdracht

Opdracht omschrijving: https://www.quad.team/assignment

Deze repo bestaat uit een back-end API in Java die trivia vragen en antwoorden ophaalt via de Open Trivia API, en een front-end in Python dash die de vragen laat zien en gebruikers een antwoord laat selecteren en nakijken. 

De Java back-end is gebouwd met behulp van Spring Boot.

## Prerequisites
Java 21 (OpenJDK 21.0.6)
Maven 3.9.9
Python 3.6.8
dash 2.15.0

## Build Instructions
De back-end applicatie kan gebouwd worden via de command line:
```
mvn package
```
En vervolgens kun je de back-end runnen vanuit de hoofdmap (locatie van deze README) via de command line:
```
java -jar .\target\trivia-api-0.0.2.jar
```
of
```
mvn spring-boot:run
```

Open dan een nieuw terminal om de front-end te runnen via de command line:
```
py .\front-end\app.py
```
In de terminal verschijnt dan de link naar de UI (`Dash is running on [link]`)