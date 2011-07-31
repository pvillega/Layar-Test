/*
 * Copyright 2008 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.perevillega {
    package snippet {

        import _root_.java.text.{ParseException,SimpleDateFormat}

        import _root_.scala.xml.{NodeSeq,Text}

        import _root_.net.liftweb.http.{RequestVar,S,SHtml}
        import _root_.net.liftweb.common.{Box,Empty,Full}
        import _root_.net.liftweb.util.{Helpers,Log}
        import S._
        import Helpers._

        import com.perevillega.model._
        import Model._

        import _root_.com.google.appengine.api.datastore.Key
        import _root_.com.google.appengine.api.datastore.KeyFactory._
        import _root_.javax.jdo.JDOUserException

        import _root_.org.scala_libs.jdo._
        import _root_.org.scala_libs.jdo.criterion._


        // Make an object so that other pages can access (ie Authors)
        object RegistreOps {
            // Object to hold search results
            object resultVar extends RequestVar[List[Registre]](Nil)
        }

        class RegistreOps {
            val formatter = new _root_.java.text.SimpleDateFormat("yyyyMMdd")

            def list (xhtml : NodeSeq) : NodeSeq = {
                val registres = Model.withPM{ from(_, classOf[Registre]).resultList }

                registres.flatMap(registre =>
                    bind("reg", xhtml,
                         "embassament" -> Text(registre.embassament.nom),
                         "quantitat" -> Text(registre.quantitat.toString),
                         "data" -> Text(formatter.format(registre.data)),
                         "edit" -> SHtml.link("add.html", () => registreVar(registre), Text(?("Edit"))),
                         "delete" -> SHtml.link("list.html", () => Model.withPM{ _.deletePersistent(registre)}, Text(?("Eliminar")))
                    ))
            }

            // Set up a requestVar to track the book object for edits and adds
            object registreVar extends RequestVar(new Registre())
            def registre = registreVar.is

            // Utility methods for processing a submitted form
            def is_valid_Registre_? (toCheck : Registre) : Boolean =
            List((if (toCheck.quantitat < 0) { S.error("Has de proporcionar una quantitat major o igual a 0"); false } else true),
                 (if (toCheck.data == null) { S.error("Has d'introduir una data"); false } else true)).forall(_ == true)

            def setDate (input : String, toSet : Registre) {
                try {
                    toSet.data = formatter.parse(input)
                } catch {
                    case pe : ParseException => S.error("Error al afegir la data")
                }
            }

            // The add snippet method
            def add (xhtml : NodeSeq) : NodeSeq = {
                def doAdd () =
                if (is_valid_Registre_?(registre)) {
                    try {
                        Model.withPM{ pm =>
                            registre.id match {
                                case null =>
                                    registre.embassament.registres.add(registre)
                                    pm.makePersistent(registre.embassament)
                                case _ =>
                                    pm.makePersistent(registre)
                            }
                        }
                        redirectTo("list.html")
                    } catch {
                        case pe : JDOUserException => error("Error en afegir el registre"); Log.error("Error en afegir el registre", pe)
                    }
                }


                // Hold a val here so that the closure holds it when we re-enter this method
                val currentId = registre.id

                val embassaments =  Model.withPM{ from(_, classOf[Embassament]).resultList }
                val choices = embassaments.map(embassament => (keyToString(embassament.id) -> embassament.nom)).toList
                val default = if (registre.embassament != null) { Full(keyToString(registre.embassament.id)) } else { Empty }

                bind("reg", xhtml,
                     "id" -> SHtml.hidden(() => registre.id = currentId),
                     "quantitat" -> SHtml.text(registre.quantitat.toString, x => registre.quantitat = x.toDouble),
                     "data" -> SHtml.text(formatter.format(registre.data), setDate(_, registre)) % ("id" -> "published"),
                     "embassament" -> (if(registre.embassament != null) { Text(registre.embassament.nom) } else { SHtml.select(choices, default, (id) => registre.embassament = findEmbassamentById(id)) }),
                     "save" -> SHtml.submit(?("Salvar"), doAdd))
            }

            //support methods
            def findEmbassament(e:Embassament):Embassament = findEmbassamentById(e.id)
            def findEmbassamentById(id:String):Embassament = findEmbassamentById(stringToKey(id))
            def findEmbassamentById(id:Key):Embassament = {
                Model.withPM{ pm =>
                    getObjectById[Embassament](pm, classOf[Embassament], id) match {
                        case Some(embassament) =>
                            pm.detachCopyAll(embassament.registres)
                            embassament
                        case _ => null
                    }
                }
            }

            //search is slightly different than list (all vs subset), so it gets its own method and html page
            def searchResults (xhtml : NodeSeq) : NodeSeq = RegistreOps.resultVar.is.flatMap(registre =>
                bind("reg", xhtml,
                     "embassament" -> Text(registre.embassament.nom),
                     "quantitat" -> Text(registre.quantitat.toString),
                     "data" -> Text(formatter.format(registre.data)),
                     "edit" -> SHtml.link("add.html", () => registreVar(registre), Text(?("Edit"))),
                     "delete" -> SHtml.link("list.html", () => Model.withPM{ _.deletePersistent(registre)}, Text(?("Eliminar")))
                )
            )
        }
    }
}
