/*
 * RestAPI.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.perevillega {
    package restapi {
        import scala.xml._

        import net.liftweb._
        import http._
        import http.rest._
        import S._
        import common._
        import util._
        import Helpers._

        import _root_.com.google.appengine.api.datastore.Key
        import _root_.com.google.appengine.api.datastore.KeyFactory._
        import _root_.javax.jdo.JDOUserException

        import _root_.org.scala_libs.jdo._
        import _root_.org.scala_libs.jdo.criterion._

        import model._
        import Model._

        import json._
        import JsonAST._
        import js._
        import JsCmds._
        import JE._
        import JsonDSL._
        import JsonParser._


        object RestAPI {
            def dispatch: LiftRules.DispatchPF = {
                case req @ Req(List("api", "get"), _, GetRequest) =>
                    () => wrap(req, poi)
                    
                    // Invalid API request - route to our error handler
                case req @ Req(List("api", x), "", _) => 
                    () => wrap(req, failure)
            }

            def wrap(req: Req, f: (Req) => JsExp ): Box[LiftResponse] = {
                val (json, code) =  (f(req), 200)
                Full(JsonResponse(json, req.headers, req.cookies, code))
            }           

            //returns the corresponding poi. It ignores many of the params of the request
            def poi(req: Req): JsExp = {

                def distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double) = {
                    val R = 6371 // km earth
                    Math.acos(Math.sin(lat1)*Math.sin(lat2) +
                              Math.cos(lat1)*Math.cos(lat2) *
                              Math.cos(lon2-lon1)) * R;
                }
                
                val layerName = req.param("layerName").openOr("")
                val lat = req.param("lat").openOr("0.0").toDouble
                val lon = req.param("lon").openOr("0.0").toDouble
                val radius = req.param("radius").openOr("1000").toInt
                var errorCode = 0
                var errorString = "ok"
                val morePages = false
                val nextPageKey = null

                //select points at a distance minor or equal to radius from
                //point sent by layar
                //var R = 6371;  km
                //var d = Math.acos(Math.sin(lat1)*Math.sin(lat2) +
                //  Math.cos(lat1)*Math.cos(lat2) *
                //  Math.cos(lon2-lon1)) * R;

                val all = Model.withPM{ from(_, classOf[Embassament]).resultList }

                val embassaments = all.filter(emb => distance(lat, lon,emb.latitud, emb.longitud) <= radius)

                val json = 
                ("hotspots" -> embassaments.map{ emb =>
                        (("id" -> emb.id.getId) ~
                         ("distance" -> 0)~
                         ("lat" -> emb.latitud*10e5)~
                         ("lon" -> emb.longitud*10e5)~
                         ("actions" -> List(
                          ("label" -> "Variable com a mail") ~
                          ("uri" -> ("mailto:"+emb.variable))
                            )) ~
                         ("imageURL" -> "http://aca-web.gencat.cat/aca/documents/ca/piv/aca.jpg") ~
                         ("title" -> "Layar ACA test")~
                         ("line2" -> "line 2")~
                         ("line3" -> "line 3 %distance%")~
                         ("line4" -> "line 4")~
                         ("attribution" -> "layar test for ACA")~
                         ("type" -> 0) ~
                         ("dimension" -> 1))
                    } ) ~
                ("layer" -> layerName) ~
                ("errorString" -> errorString) ~
                ("morePages" -> morePages) ~
                ("errorCode" -> errorCode) ~
                ("radius" -> radius)

                JsRaw(compact(JsonAST.render(json)))
            }
           

            def failure(req: Req): JsExp = {
                val json = ("error" -> "Wrong service requested")
                JsRaw(compact(JsonAST.render(json)))
            }
           
        }
    }
}



