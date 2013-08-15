package com.gmail.crcinema.domain

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/11/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cinema {

    public enum CinemaType {
        NOVA_CINEMAS,
        CINEMARK
    }

    int id
    String name
    String address
    MovieGuide movieGuide

    //Android UI values needed
    String buttonSelectorName
}
