package crcinema.api

import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.domain.MovieGuideDetail
import com.gmail.crcinema.domain.Movie
import com.gmail.crcinema.domain.MovieGuide
import grails.converters.JSON

class SampleController {

    def index() {

    }

    def test = {
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

        render cinema as JSON
    }
}
