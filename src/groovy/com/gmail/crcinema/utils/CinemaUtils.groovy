package com.gmail.crcinema.utils

import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT
import groovyx.net.http.HTTPBuilder

/**
 * Created with IntelliJ IDEA.
 * User: mpicado
 * Date: 6/14/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class CinemaUtils {
     public static String getHtmlFromUrl(String url, String path){

         if(url != null && path != null){
            try{
                 def http = new HTTPBuilder()
                 String result = null
                 http.request( url, GET, TEXT ) { req ->
                     uri.path = path
                     //headers.'User-Agent' = "Mozilla/5.0 (Windows NT 6.1; rv:21.0) Gecko/20100101 Firefox/21.0"
                     response.success = { resp, reader ->
                         assert resp.statusLine.statusCode == 200
                         //println "Got response: ${resp.statusLine}"
                         //println "Content-Type: ${resp.headers.'Content-Type'}"
                         result = reader.text
                     }

                     response.'404' = {
                         println 'Not found'
                     }
                 }

                 return result
             }
             catch (Exception e){
                 e.printStackTrace()
                 return null
             }
         }else{
             //Returning null when url and path are null
             //TODO add LOGGER to display this messages
             return null
         }
     }
}
