package com.gmail.crcinema.utils

import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.domain.Cinema.CinemaType
import com.gmail.crcinema.domain.Movie
import com.gmail.crcinema.domain.MovieGuide
import com.gmail.crcinema.domain.MovieGuideDetail
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
        def cinemaListing = html?."**".findAll {
            it.@class?.toString().contains("MovieSummaryRow")
        }

        def firstDetails = cinemaListing?.collect {
            [
                    name: it.TD[1].TABLE[0].TR[1].TD[1].A[0].text(),
                    movieDetailsUrl: it.TD[1].TABLE.TR[1].TD[1].A.@href.text()
            ]
        }
        def listMoviesGuides = []
        firstDetails?.each { detail->
            def movieDetailsHttp = new HTTPBuilder(baseUrl+baseMovieUrl+detail.movieDetailsUrl)
            def movieDetailsHtml = movieDetailsHttp?.get([:])
            def moviesDetails = movieDetailsHtml?."**".findAll {
                it.@class?.toString().contains("MovieInfoText")     ||
                it.@class?.toString().contains("DirectorNameLabel") ||
                it.@class?.toString().contains("MovieInfoImage")    ||
                it.@class?.toString().contains("SessionRow")        ||
                it.@class?.toString().contains("SessionRowAlt")
            }

            if(moviesDetails){
                //MovieGuide details
                MovieGuideDetail movieGuideDetail = new MovieGuideDetail()
                movieGuideDetail.times = [new Date()]

                //Movie details
                Movie movie = new Movie()
                String stars = validateMovieDetailsIndex(moviesDetails,2) ? moviesDetails[2][0].children[0] : ""
                String duration = validateMovieDetailsIndex(moviesDetails,3) ? moviesDetails[3][0].children[0] : ""

                movie.name = detail.name
                //Nova cinemas is using the same image for the thumbnail and the normal image, they just change the width and height
                movie.imageUrl = validateMovieDetailsIndex(moviesDetails,0,false) ? baseUrl + baseMovieUrl + moviesDetails[0][0].attributes?.src : ""
                movie.thumbnailUrl = movie.imageUrl

                movie.description = validateMovieDetailsIndex(moviesDetails,1) ? moviesDetails[1][0].children[0]?.replace("_________________________________________________","")?.trim() : ""
                movie.restrictions = validateMovieDetailsIndex(moviesDetails,1) ? moviesDetails[1][0].children[2]?.children[0] : ""
                movie.genre = validateMovieDetailsIndex(moviesDetails,4) ? moviesDetails[4][0]?.children[0] : ""

                movie.stars = stars?.substring(stars?.indexOf("Reparto:") + 7)?.split(",")
                movie.minutes = duration?.split(" ")[0] as int
                String directPlusCinLocat = validateMovieDetailsIndex(moviesDetails,5) ? moviesDetails[5][0]?.children[0] : ""
                if(directPlusCinLocat){
                    int directorIndex = directPlusCinLocat.indexOf("-")
                    int locationIndex = directPlusCinLocat.lastIndexOf("--")

                    if(directorIndex >= 0 && directorIndex < directPlusCinLocat.length()){
                        movie.director = directPlusCinLocat.substring(0,directorIndex)
                    }
                    if(locationIndex >= 0 && locationIndex + 2 < directPlusCinLocat.length()){
                        movieGuideDetail.cinemaLocation = directPlusCinLocat.substring(locationIndex+2,directPlusCinLocat.length())
                    }
                }

                def dates = []
                def scheduleNodes =  moviesDetails?.findAll {md ->
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
                        if (splitText[0]?.length() > 3){ //friday and wednesday, TODO, improve it because wednesday falls into the third case as well
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
                movieGuideDetail.times = dates
                movieGuideDetail.movie = movie
                listMoviesGuides.add(movieGuideDetail)
            }
        }

        return novaCinemaDetails(listMoviesGuides)
    }

    private static Cinema parseCinemarkHtml(String url){
        def http = new HTTPBuilder(url)
        def html = http.get([:])
        def cinemaListing = html."**".findAll {
            it.@class?.toString().contains("span-16")
        }
        cinemaListing.remove(0);//first object is not movie information

        def firstDetails = cinemaListing?.collect {
            [
                    imageUrl: it.DIV[0].IMG[0].@src.text(),
                    spanishName: it.DIV[1].DIV[0].SPAN[0].SPAN[0].text(),
                    englishName: it.DIV[1].DIV[0].SPAN[1].SPAN[0].text(),
                    format: it.DIV[1].DIV[1].SPAN[1].SPAN[0].text(),
                    runtime: it.DIV[1].DIV[1].SPAN[2].SPAN[0].text(),
                    genre: it.DIV[1].DIV[1].SPAN[4].SPAN[0].text(),
                    ageRate: it.DIV[1].DIV[1].SPAN[6].SPAN[0].text(),
                    language: it.DIV[1].DIV[1].SPAN[8].text(),
                    date: it.DIV[2].DIV[0].SPAN[0].text(),
                    time: it.DIV[2].SPAN[0].SPAN[0].text(),
            ]
        }

        def listMoviesGuides = []

        firstDetails.each {
            print("Image URL: " + it.imageUrl)
            print("Spanish Name: " + it.spanishName)
            print("English Name: " + it.englishName)
            print("Format: " + it.format)
            print("Runtime: " + it.runtime)
            print("Genre: " + it.genre)
            print("Age Rate: " + it.ageRate)
            print("Language: " + it.language)
            print("Date: " + it.date)
            print("Time: " + it.time)
            print("--------------")
        }

        return mockedCinemaResponse()
    }

    private static Cinema novaCinemaDetails(List<MovieGuideDetail> movieGuideDetailList){
        MovieGuide guide = new MovieGuide()
        guide.date = new Date()
        guide.movieDetails = movieGuideDetailList

        //We need to have the cinemas in a database and retrieve the value here, so we don't have it hardcoded
        Cinema cinema = new Cinema()
        cinema.id=1
        cinema.name = "Nova Cinemas"
        cinema.address="Avenida Escazu"
        cinema.movieGuide = guide
        return cinema
    }

    private static Cinema cinemarkCinemaDetails(List<MovieGuideDetail> movieGuideDetailList){
        MovieGuide guide = new MovieGuide()
        guide.date = new Date()
        guide.movieDetails = movieGuideDetailList

        //We need to have the cinemas in a database and retrieve the value here, so we don't have it hardcoded
        Cinema cinema = new Cinema()
        cinema.id=2
        cinema.name = "Cinemark Escazu"
        cinema.address = "Multiplaza Escazu"
        cinema.movieGuide = guide
        return cinema
    }

    /* Validation method to avoid some errors while
     * trying to gather the movies details
     *  */
    private static boolean validateMovieDetailsIndex(def movieDetails, int index, boolean validateChildren = true){
        if(movieDetails && movieDetails.size() > index){
            if(movieDetails[index].size()>0 && movieDetails[index][0])
            {
                if(validateChildren){
                    if(movieDetails[index][0].children?.size() > 0){
                        return true
                    }
                }
                else{
                    return true
                }
            }
        }
        return false
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
