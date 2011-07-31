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

        import com.google.appengine.api.datastore.Key
        import java.util.Date
        import javax.jdo.annotations._


        /**
         This class represents data collected
         */
        //TODO: we should do embassament id and data the PK of the class. but we need to check by hand
        @PersistenceCapable{val identityType = IdentityType.APPLICATION,
                            val detachable="true"}
        class Registre {
            @PrimaryKey
            @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
            var id : Key = _

            @Persistent
            var quantitat : Double = 0.0

            @Persistent
            var data : Date = new Date()

            @Persistent{ val defaultFetchGroup="true"}
            var embassament : Embassament = _
        }
    }
}
