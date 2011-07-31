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

        import _root_.scala.xml.{NodeSeq,Text}

        import _root_.net.liftweb._
        import http._
        import S._
        import common._
        import util._
        import Helpers._

        import _root_.org.scala_libs.jdo._
        import _root_.org.scala_libs.jdo.criterion._
        import _root_.javax.jdo.JDOUserException

        import com.perevillega.model._
        import Model._

        class EmbassamentOps {
            def list (xhtml : NodeSeq) : NodeSeq = {
                val embassaments = Model.withPM{ from(_, classOf[Embassament]).resultList }

                embassaments.flatMap(embassament =>
                    bind("emb", xhtml,
                         "nom" -> Text(embassament.nom),
                         "variable" -> Text(embassament.variable),
                         "unitats" -> Text(embassament.unitats),
                         "latitud" -> Text(embassament.latitud.toString),
                         "longitud" -> Text(embassament.longitud.toString),
                         "capacitat" -> Text(embassament.capacitat.toString),
                         "registres" -> SHtml.link("/registres/search.html", {() =>
                                RegistreOps.resultVar( Model.withPM{ from(_, classOf[Registre]).where(eqC("embassament", embassament)).resultList })
                            }, Text(embassament.registres.size.toString)),
                         "edit" -> SHtml.link("add.html", () => embassamentVar(embassament), Text(?("Editar"))),
                         "delete" -> SHtml.link("list.html",
                                                () => Model.withPM{ _.deletePersistent(embassament)},
                                                Text(?("Eliminar")))
                    ))
            }


            // Set up a requestVar to track the object for edits and adds
            object embassamentVar extends RequestVar(new Embassament())
            def embassament = embassamentVar.is

            def add (xhtml : NodeSeq) : NodeSeq = {
                def doAdd () = {
                    if (embassament.nom.length == 0) {
                        error("emptyEmbassament", "L'embassament ha de tenir un nom")
                    } else if(embassament.variable.length == 0) {
                        error("emptyVariable", "L'embassament ha de tenir una variable")
                    } else {
                        try{
                            Model.withPM{ _.makePersistent(embassament) }
                            redirectTo("list.html")
                        } catch {
                            case pe : JDOUserException => error("Error en afegir l'embassament"); Log.error("Error en afegir l'embassament", pe)
                        }
                    }
                }

                // Hold a val here so that the "id" closure holds it when we re-enter this method
                val currentId = embassament.id

                bind("emb", xhtml,
                     "id" -> SHtml.hidden(() => embassament.id = currentId),
                     "nom" -> SHtml.text(embassament.nom, embassament.nom = _),
                     "variable" -> SHtml.text(embassament.variable, embassament.variable = _),
                     "unitats" -> SHtml.text(embassament.unitats, embassament.unitats = _),
                     "latitud" -> SHtml.text(embassament.latitud.toString, x => embassament.latitud = x.toDouble),
                     "longitud" -> SHtml.text(embassament.longitud.toString, x => embassament.longitud = x.toDouble),
                     "capacitat" -> SHtml.text(embassament.capacitat.toString, x => embassament.capacitat = x.toDouble),
                     "submit" -> SHtml.submit(?("Salvar"), doAdd))
            }
        }

    }
}
