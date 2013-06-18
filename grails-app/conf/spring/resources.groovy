import crcinema.api.CinemaService
import com.gmail.crcinema.dao.impl.HtmlProviderDaoImpl

// Place your Spring DSL code here
beans = {

    htmlProviderDaoImpl(HtmlProviderDaoImpl) {
        grailsApplication = ref("grailsApplication")
    }
    cinemaService(CinemaService) {
        cinemaHtmlDao = ref("htmlProviderDaoImpl")
        //cinemaWebServiceDao = ref("wsProviderDaoImpl")
    }
}
