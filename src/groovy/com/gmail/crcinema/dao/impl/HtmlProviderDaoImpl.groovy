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
            String novaMovieListingPath = grailsApplication.config.grails.cinema.nova.movieListingUrl
            //html = CinemaUtils.getHtmlFromUrl(novaUrl,novaMovieListingPath)
            cinema = HtmlParser.parseHtml(cinemaType,novaUrl+novaMovieListingPath)

        }
        if(Cinema.CinemaType.CINEMARK.equals(cinemaType)){
            html = ""
            cinema = null
        }
        return cinema
    }
}
