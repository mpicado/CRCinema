package com.gmail.crcinema.dao

import com.gmail.crcinema.domain.Cinema
import com.gmail.crcinema.domain.DataProvider
import com.gmail.crcinema.dao.impl.HtmlProviderDaoImpl

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class CinemaDataProviderFactory {

    public static ICinemaDao getCinemaData(DataProvider provider){
        if(DataProvider.HTML_PARSER.equals(provider)){
            return new HtmlProviderDaoImpl()
        }
        else if (DataProvider.WEB_SERVICE.equals(provider)){
            return null
        }
        else{
            throw new IllegalArgumentException("the provider is not implemented yet")
        }
    }
}
