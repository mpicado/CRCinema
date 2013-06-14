package com.gmail.crcinema.utils

import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.domain.Cinema.CinemaType
import com.gmail.crcinema.domain.Movie
import com.gmail.crcinema.domain.MovieGuideDetail
import com.gmail.crcinema.domain.MovieGuide
import grails.converters.JSON

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlParser {

    public static Cinema parseHtml(CinemaType cinemaType, String html){
        if(CinemaType.NOVA_CINEMAS.equals(cinemaType)){
            return parseNovaHtml(html)
        }
        else if(CinemaType.CINEMARK.equals(cinemaType)){
            return parseCinemarkHtml(html)
        }

        return null
    }

    private static Cinema parseNovaHtml(String html){
        //aqui deberia ir el parseo del html para empezar a armar el objeto
        println(html)
        //datos de prueba para el flujo entre las capas!
        Movie movie = new Movie()
        movie.name = "Despues de la tierra"
        movie.genres = ["Ciencia ficcion"]
        movie.minutes = 100
        movie.restrictions = "Mayores de 15 años"
        movie.stars = ["Will Smith", "Jaden Smith"]

        MovieGuideDetail movieGuideDetail = new MovieGuideDetail()
        movieGuideDetail.cinemaLocation = "Sala 2 digital 3D"
        movieGuideDetail.times = [new Date()]
        movieGuideDetail.movie = movie

        MovieGuide guide = new MovieGuide()
        guide.date = new Date()
        guide.movieDetails = [movieGuideDetail]

        Cinema cinema = new Cinema()
        cinema.id=1
        cinema.name = "Nova Cinemas"
        cinema.address="Avenida Escazu"
        cinema.movieGuide = guide
        return cinema
    }

    private static Cinema parseCinemarkHtml(String html){
        return new Cinema()
    }

}
