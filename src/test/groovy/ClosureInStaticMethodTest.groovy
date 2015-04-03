/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package groovy

/** 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
class ClosureInStaticMethodTest extends GroovyTestCase {

    void testClosureInStaticMethod() {
        def closure = closureInStaticMethod()
        assertClosure(closure)    
    }

    void testMethodClosureInStaticMethod() {
        def closure = methodClosureInStaticMethod()
        assertClosure(closure)    
    }
    
    static def closureInStaticMethod() {
        return { println(it) }
    }

    static def methodClosureInStaticMethod() {
        System.out.&println
    }
    
    static def assertClosure(Closure block) {
        assert block != null
        block.call("hello!")
    }
    
    void testClosureInStaticMethodCallingStaticMethod() {
       assert doThing(1) == 10
       assert this.doThing(1) == 10
       assert ClosureInStaticMethodTest.doThing(1) == 10
    }
    
    
    static doThing(count) {
      def ret = count
      if (count > 2) return ret
      count.times {
        ret += doThing(count+it+1)
      }
      return ret
    }
}
