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
package bootstrap.liftweb

import _root_.java.util.Locale

import _root_.net.liftweb.common.{Box,Empty,Full}
import _root_.net.liftweb.util.{LoanWrapper,LogBoot}
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import com.perevillega.model._
import S.?

import com.perevillega.restapi._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
    def boot {

        // where to search snippet
        LiftRules.addToPackages("com.perevillega")

        // Set up a site map
        val entries = SiteMap(Menu(Loc("Home", "index" :: Nil , ?("Home"))),
                              Menu(Loc("Embassaments", "embassaments" :: "list" :: Nil, ?("Llista Embassaments"))),
                              Menu(Loc("Afegir embassament", "embassaments" :: "add" :: Nil, ?("Afegir Embassament"), Hidden)),
                              Menu(Loc("Registres", "registres" :: "list" :: Nil, ?("Llista Registres"))),
                              Menu(Loc("Afegir registres", "registres" :: "add" :: Nil, ?("Afegir Registres"), Hidden)),
                              Menu(Loc("Cercar registres", "registres" :: "search" :: Nil, ?("Cercar registres"), Hidden))
                              )

        LiftRules.setSiteMap(entries)

        LiftRules.dispatch.prepend(RestAPI.dispatch)

        LiftRules.early.append(makeUtf8) 
    }

    /**
     * Force the request to be UTF-8
     */
    private def makeUtf8(req: HTTPRequest) {
        req.setCharacterEncoding("UTF-8")
    }

}

