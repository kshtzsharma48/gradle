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



package org.gradle.integtests.fixtures

import org.junit.Assert
import org.gradle.internal.SystemProperties

/**
 * Checks that all lines contained in the expected output are present in the actual output, in any order.
 */
class ParallelOutputMatcher extends SequentialOutputMatcher {
    private static final String NL = SystemProperties.lineSeparator

    protected void assertOutputLinesMatch(List<String> expectedLines, List<String> actualLines, boolean ignoreExtraLines) {
        List<String> unmatchedLines = new ArrayList<String>(actualLines)
        expectedLines.removeAll('')
        unmatchedLines.removeAll('')

        expectedLines.each { expectedLine ->
            def matchedLine = unmatchedLines.find { actualLine ->
                compare(expectedLine, actualLine)
            }
            if (matchedLine) {
                unmatchedLines.remove(matchedLine)
            } else {
                Assert.fail("Line missing from output.${NL}${expectedLine}${NL}---")
            }
        }

        if (!(ignoreExtraLines || unmatchedLines.empty)) {
            Assert.fail("Extra lines in output.${NL}" + unmatchedLines.join('\n'))
        }
    }
}