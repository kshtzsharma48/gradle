/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.gradle.integtests.fixture

import org.gradle.integtests.fixtures.executer.GradleDistribution
import org.junit.Rule
import spock.lang.Specification

/**
 * by Szczepan Faber, created at: 3/14/12
 */
class DistroTempDirIsUniquePerTestSpec extends Specification {

    @Rule GradleDistribution dist = new GradleDistribution();
    static tests = new HashSet()
    static tmpDirs = new HashSet()

    def setup() {
        //it's very important we try to access the test dir in the setup()
        dist.testDir
    }
    
    def "testOne"() {
        when:
        tests << "testOne"
        tmpDirs << dist.testDir
        
        then:
        tests.size() == tmpDirs.size()
    }

    def "testTwo"() {
        when:
        tests << "testTwo"
        tmpDirs << dist.testDir

        then:
        tests.size() == tmpDirs.size()
    }
}
