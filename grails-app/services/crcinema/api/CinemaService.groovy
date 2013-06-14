package crcinema.api

import com.gmail.crcinema.dao.ICinemaDao
import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.dao.CinemaDataProviderFactory
import com.gmail.crcinema.domain.DataProvider

class CinemaService {
    def cinemaHtmlDao = CinemaDataProviderFactory.getCinemaData(DataProvider.HTML_PARSER)
    //this can be used if the cinemas provide us an endpoint to work with!!
    def cinemaWebServiceDao = CinemaDataProviderFactory.getCinemaData(DataProvider.WEB_SERVICE)

    def getCinemaData(String cinemaType) {
        cinemaHtmlDao.getCinemaData(Cinema.CinemaType.valueOf(cinemaType))
    }
}
