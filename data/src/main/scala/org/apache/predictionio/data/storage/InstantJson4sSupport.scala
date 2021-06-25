/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.predictionio.data.storage

import org.apache.predictionio.annotation.DeveloperApi
import org.json4s._

import java.time.Instant

/** :: DeveloperApi ::
  * JSON4S serializer for Joda-Time
  *
  * @group Common
  */
@DeveloperApi
object InstantJson4sSupport {

  @transient lazy implicit val formats = DefaultFormats

  /** Serialize Instant to JValue */
  def serializeToJValue: PartialFunction[Any, JValue] = {
    case d: Instant => JString(d.toString)
  }

  /** Deserialize JValue to Instant */
  def deserializeFromJValue: PartialFunction[JValue, Instant] = {
    case jv: JValue => Instant.parse(jv.extract[String])
  }

  /** Custom JSON4S serializer for Joda-Time */
  class Serializer extends CustomSerializer[Instant](format => (
    deserializeFromJValue, serializeToJValue))

}
