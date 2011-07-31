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
    package model {

        import javax.jdo.annotations._
        import com.google.appengine.api.datastore.Key


        /**
         An author is someone who writes books.
         */
        //TODO: we have to check uniqueness by hand, GAE doesn't support it'
        @PersistenceCapable{val identityType = IdentityType.APPLICATION,
                            val detachable="true"}
        class Embassament {
             @PrimaryKey
            @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
            var id : Key = _

            @Persistent
            var nom : String = ""

            @Persistent
            var variable : String = ""

            //TODO: this should be a custom type
            @Persistent
            var unitats : String = "hm3"

            @Persistent
            var latitud : Double = 0.0

            @Persistent
            var longitud : Double = 0.0

            @Persistent
            var capacitat : Double = 0.0

            @Persistent{val mappedBy = "embassament",
              val defaultFetchGroup="true"}
            var registres : java.util.Set[Registre] = new java.util.HashSet[Registre]()
        }
    }
}
