package com.gmail.crcinema.dao

import com.gmail.crcinema.domain.Cinema

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ICinemaDao {

    public Cinema getCinemaData(Cinema.CinemaType cinemaType)

}