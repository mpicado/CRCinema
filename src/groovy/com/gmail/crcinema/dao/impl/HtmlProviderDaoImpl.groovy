package com.gmail.crcinema.dao.impl

import com.gmail.crcinema.dao.ICinemaDao
import com.gmail.crcinema.domain.Cinema
import org.codehaus.groovy.grails.commons.ConfigurationHolder
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

    def novaUrl = ConfigurationHolder.getConfig().grails.cinema.nova.url
    def novaMovieListingPath = ConfigurationHolder.getConfig().grails.cinema.nova.movieListingUrl

    @Override
    Cinema getCinemaData(Cinema.CinemaType cinemaType) {
        String html = CinemaUtils.getHtmlFromUrl(novaUrl,novaMovieListingPath)
        Cinema cinema = HtmlParser.parseHtml(cinemaType,html)
        return cinema
    }
}
