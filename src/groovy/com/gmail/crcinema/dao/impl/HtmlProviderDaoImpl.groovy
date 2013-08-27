package com.gmail.crcinema.dao.impl

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.gmail.crcinema.dao.ICinemaDao
import com.gmail.crcinema.domain.Cinema
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import com.gmail.crcinema.utils.HtmlParser
import com.gmail.crcinema.utils.CinemaUtils

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlProviderDaoImpl implements ICinemaDao {

    def grailsApplication

    @Override
    Cinema getCinemaData(Cinema.CinemaType cinemaType) {
        Cinema cinema
        String html
        if(Cinema.CinemaType.NOVA_CINEMAS.equals(cinemaType)){
            String novaUrl = grailsApplication.config.grails.cinema.nova.url
            String baseMoviesUrl = grailsApplication.config.grails.cinema.nova.baseMoviesUrl
            String novaMovieListingPath = grailsApplication.config.grails.cinema.nova.movieListingUrl
            //html = CinemaUtils.getHtmlFromUrl(novaUrl,novaMovieListingPath)
            cinema = HtmlParser.parseHtml(cinemaType,novaUrl+baseMoviesUrl+novaMovieListingPath,novaUrl,baseMoviesUrl)

        }
        if(Cinema.CinemaType.CINEMARK.equals(cinemaType)){
            String novaUrl = grailsApplication.config.grails.cinema.cinemark.url
            String baseMoviesUrl = grailsApplication.config.grails.cinema.cinemark.baseMoviesUrl
            String novaMovieListingPath = grailsApplication.config.grails.cinema.cinemark.movieListingUrl
            cinema = HtmlParser.parseHtml(cinemaType,novaUrl+baseMoviesUrl+novaMovieListingPath,novaUrl,baseMoviesUrl)
        }
        return cinema
    }

    @Override
    List<Cinema> getCinemas(){
        //this should be going to a database, but since we do not have
        //a db yet, then we are hardcoding the values
        List<Cinema> list = []
        Cinema novaCinemas = new Cinema()
        novaCinemas.id = 1
        novaCinemas.name = "Nova Cinemas"
        novaCinemas.address = "Avenida Escazu"
        novaCinemas.cinemaImageName = "logo_novacinemas.9.png"

        Cinema multiplazaEscazu = new Cinema()
        multiplazaEscazu.id = 2
        multiplazaEscazu.name = "Cinemark Multiplaza Escazu"
        multiplazaEscazu.address = "Guachipelín de Escazú"
        multiplazaEscazu.cinemaImageName = "logo_cinemark.9.png"

        Cinema multiplazaDelEste = new Cinema()
        multiplazaDelEste.id = 3
        multiplazaDelEste.name = "Cinemark Multiplaza del Este"
        multiplazaDelEste.address = "Zapote"
        multiplazaDelEste.cinemaImageName = "logo_cinemark.9.png"

        list.add(novaCinemas)
        list.add(multiplazaEscazu)
        list.add(multiplazaDelEste)

        return list;
    }
}
