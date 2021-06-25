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


package org.apache.predictionio.data

import java.time.Instant

private[predictionio] object Utils {

  // use Instant() for strict ISO8601 format

  def stringToInstant(dt: String): Instant = {
    // We accept two formats.
    // 1. "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
    // 2. "yyyy-MM-dd'T'HH:mm:ssZZ"
    // The first one also takes milliseconds into account.
      // formatting for "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
    Instant.parse(dt)
  }

  def InstantToString(dt: Instant): String = dt.toString
    // dt.toString

}
