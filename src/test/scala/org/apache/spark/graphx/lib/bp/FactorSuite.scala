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

package org.apache.spark.graphx.lib.bp

import org.apache.spark.LocalSparkContext
import org.scalatest.FunSuite

class FactorSuite extends FunSuite with LocalSparkContext {

  // TODO: add 3d factor test
  test("marginalize") {
    /*
    *       x1    margin over x0
    *       1 4    5
    *  x0   2 5    7
    *       3 6    9
    *
    *       6 15 <- margin over x1
    */
    val factor = Factor(Array(3, 2), Array(1, 2, 3, 4, 5, 6))
    val margin1 = factor.marginalize(0)
    assert(margin1.deep == Array(5.0, 7.0, 9.0).deep,
      "Marginalization over the first variable is (5, 7, 9)")
    val margin2 = factor.marginalize(1)
    assert(margin2.deep == Array(6.0, 15.0).deep,
      "Marginalization over the second variable is (6, 15)")
  }

  test("product") {
    /*
    *       x1    msg to x0   product
    *       1 4    1           1 4
    *  x0   2 5    2           4 10
    *       3 6    3           9 18
    *
    *       msg to x1   product
    *       1           1 8
    *       2           2 10
    *                   3 12
    */
    val factor = Factor(Array(3, 2), Array(1, 2, 3, 4, 5, 6))
    val product1 = factor.product(Array(1, 2, 3), 0)
    assert(product1.values.deep == Array[Double](1, 4, 9, 4, 10, 18).deep,
      "Product should be (1, 4, 9, 1, 10, 18)")
    val product2 = factor.product(Array(1, 2), 1)
    assert(product2.values.deep == Array[Double](1, 2, 3, 8, 10, 12).deep,
      "Product should be (1, 2, 3, 8, 10, 12)")
  }

  test("marginalize the product") {
    val factor = Factor(Array(3, 2), Array(1, 2, 3, 4, 5, 6))
    val message1 = Array[Double](1, 2, 3)
    val trueMargin1 = factor.product(message1, 0).marginalize(0)
    val margin1 = factor.marginalOfProduct(message1, 0)
    assert(trueMargin1.deep == margin1.deep)
    val message2 = Array[Double](1, 2)
    val trueMargin2 = factor.product(message2, 1).marginalize(1)
    val margin2 = factor.marginalOfProduct(message2, 1)
    assert(trueMargin2.deep == margin2.deep)
  }
}