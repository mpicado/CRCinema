package crcinema.api

import com.gmail.crcinema.domain.Cinema
import grails.converters.JSON

public class CinemaController {

    def cinemaService

    def cinemaData = {
        Cinema cinema = cinemaService.getCinemaData(params.cinemaType)
        render cinema as JSON
    }
}
