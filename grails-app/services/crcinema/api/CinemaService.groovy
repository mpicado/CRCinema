package crcinema.api

import com.gmail.crcinema.domain.Cinema
import grails.plugin.cache.Cacheable

class CinemaService {
    def cinemaHtmlDao
    //this can be used if the cinemas provide us an endpoint to work with!!
    def cinemaWebServiceDao

    @Cacheable("singleCinema")
    def getCinemaData(String cinemaType) {
        cinemaHtmlDao.getCinemaData(Cinema.CinemaType.valueOf(cinemaType))
    }

    def getCinemas() {
        cinemaHtmlDao.getCinemas()
    }
}
