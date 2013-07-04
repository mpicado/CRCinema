package com.gmail.crcinema.utils

import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.domain.Cinema.CinemaType
import com.gmail.crcinema.domain.Movie
import com.gmail.crcinema.domain.MovieGuideDetail
import com.gmail.crcinema.domain.MovieGuide
import grails.converters.JSON
import groovyx.net.http.HTTPBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlParser {

    public static Cinema parseHtml(CinemaType cinemaType, String url, String baseUrl, String baseMovieUrl){
        if(url && url != ""){
            if(CinemaType.NOVA_CINEMAS.equals(cinemaType)){
                return parseNovaHtml(url,baseUrl,baseMovieUrl)
            }
            else if(CinemaType.CINEMARK.equals(cinemaType)){
                return parseCinemarkHtml(url)
            }
        }

        return null
    }

    private static Cinema parseNovaHtml(String url, String baseUrl, String baseMovieUrl){
        def http = new HTTPBuilder(url)
        def html = http.get([:])
        def cinemaListing = html."**".findAll {
            it.@class?.toString().contains("MovieSummaryRow")
        }

        def firstDetails = cinemaListing.collect {
            [
                    name: it.TD[1].TABLE[0].TR[1].TD[1].A[0].text(),
                    movieDetailsUrl: it.TD[1].TABLE.TR[1].TD[1].A.@href.text()
            ]
        }
        def listMovies = []
        firstDetails.each { detail->
            def movieDetailsHttp = new HTTPBuilder(baseUrl+baseMovieUrl+detail.movieDetailsUrl)
            def movieDetailsHtml = movieDetailsHttp.get([:])
            def moviesDetails = movieDetailsHtml."**".findAll {
                it.@class?.toString().contains("MovieInfoText") ||
                it.@class?.toString().contains("SessionRow")    ||
                it.@class?.toString().contains("SessionRowAlt")
            }

            Movie movie = new Movie()
            String stars = moviesDetails[1][0]?.children[0]
            String duration = moviesDetails[2][0]?.children[0]

            movie.name = detail.name
            movie.description = moviesDetails[0][0]?.children[0]?.replace("_________________________________________________","")?.trim()
            movie.restrictions = moviesDetails[0][0]?.children[2]?.children[0]
            movie.genre = moviesDetails[3][0]?.children[0]

            movie.stars = stars?.substring(stars?.indexOf("Reparto:") + 7)?.split(",")
            movie.minutes = duration?.split(" ")[0] as int

            def dates = []
            def scheduleNodes =  moviesDetails.findAll {md ->
                    md && md[0]?.attributes && ("SessionRow".equals(md[0]?.attributes?.class) ||
                    "SessionRowAlt".equals(md[0]?.attributes?.class))
            }
            scheduleNodes?.each { dn ->
                String dateText = dn[0]?.children[0]?.children[0]
                def hours = []
                dn[0]?.children[1]?.children?.each { ch ->
                    if("A".equals(ch?.name) && ch?.children){
                        hours.add(ch?.children[0])
                    }
                }

                Locale locale = new Locale("es");
                DateFormat dtFormat = new SimpleDateFormat("EEE dd MMMM yyyy",locale);
                DateFormat dtFormat1 = new SimpleDateFormat("dd/MM/yyyy",locale);
                Date d =  dtFormat1.parse("10/07/2013");
                String s = dtFormat.format(d)
                def splitText = dateText?.split(" ")
                if(splitText && splitText?.length > 2){
                    if (splitText[0]?.length() > 3){ //friday and wednesday, TODO, improve it because wednesday falls into the third case below
                        dateText = "${splitText[0]?.substring(0,3)} ${splitText[1]} ${splitText[2]}"
                    }
                    if ("Sab".equals(splitText[0]) || "sab".equals(splitText[0])){
                        dateText = "sáb ${splitText[1]} ${splitText[2]}"
                    }
                    else if ("Mier".equals(splitText[0]) || "mier".equals(splitText[0])){
                        dateText = "mié ${splitText[1]} ${splitText[2]}"
                    }
                    //TODO, provide logic for handling schedules between years, for instance, schedules of end of year
                    dateText += " ${Calendar.getInstance()?.get(Calendar.YEAR)}"
                    hours?.each { hour  ->
                        def splitHour = hour.split(":")
                        Date date = dtFormat.parse(dateText);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date)
                        cal.set(Calendar.HOUR,splitHour[0] as int);
                        cal.set(Calendar.MINUTE,splitHour[1].substring(0,2) as int);
                        if("AM".equals(splitHour[1].substring(2,4))){
                            cal.set( Calendar.AM_PM, Calendar.AM )
                        }
                        else{
                            cal.set( Calendar.AM_PM, Calendar.PM )
                        }

                        cal.setTimeZone(TimeZone.default)

                        dates.add(cal.getTime());
                    }
                }
            }
            println("dates: ${dates}")
            listMovies.add(movie)
        }

        listMovies?.each {
            println(it as String)
        }

        return mockedCinemaResponse()
    }

    private static Cinema parseCinemarkHtml(String html){
        return mockedCinemaResponse()
    }

    private static Cinema mockedCinemaResponse(){
        Movie movie = new Movie()
        movie.name = "Despues de la tierra"
        movie.genre = "Ciencia ficcion"
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
