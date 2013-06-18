package com.gmail.crcinema.utils

import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.domain.Cinema.CinemaType
import com.gmail.crcinema.domain.Movie
import com.gmail.crcinema.domain.MovieGuideDetail
import com.gmail.crcinema.domain.MovieGuide
import grails.converters.JSON
import groovyx.net.http.HTTPBuilder

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlParser {

    public static Cinema parseHtml(CinemaType cinemaType, String url){
        if(html && html != ""){
            if(CinemaType.NOVA_CINEMAS.equals(cinemaType)){
                return parseNovaHtml(url)
            }
            else if(CinemaType.CINEMARK.equals(cinemaType)){
                return parseCinemarkHtml(url)
            }
        }

        return null
    }

    private static Cinema parseNovaHtml(String url){
        def http = new HTTPBuilder(url)
        def html = http.get([:])
        def cinemaListing = html."**".findAll {
            it.@class?.toString().contains("MovieSummaryRow")
        }

        def test = cinemaListing.collect {
            [
                    Name: it.TD[1].TABLE[0].TR[1].TD[1].A[0].text(),
                    MovieDetailsUrl: it.TD[1].TABLE.TR[1].TD[1].A.@href.text()
            ]
        }
        cinemaListing.each {
            println(it)
        }

        return mockedCinemaResponse()
    }

    private static Cinema parseCinemarkHtml(String html){
        return mockedCinemaResponse()
    }

    private static Cinema mockedCinemaResponse(){
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

}
