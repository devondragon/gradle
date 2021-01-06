/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.smoketests

import org.gradle.integtests.fixtures.ToBeFixedForConfigurationCache
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition
import spock.lang.Issue

class SpotBugsPluginSmokeTest extends AbstractSmokeTest {

    @Issue('https://plugins.gradle.org/plugin/com.github.spotbugs')
    @Requires(TestPrecondition.JDK11_OR_EARLIER)
    @ToBeFixedForConfigurationCache
    def 'spotbugs plugin'() {
        given:
        buildFile << """
            import com.github.spotbugs.snom.SpotBugsTask

            plugins {
                id 'java'
                id 'com.github.spotbugs' version '${TestedVersions.spotbugs}'
            }

            ${jcenterRepository()}

            tasks.withType(SpotBugsTask) {
                reports.create("html")
            }

            """.stripIndent()

        file('src/main/java/example/Application.java') << """
            package example;

            public class Application {
                public static void main(String[] args) {}
            }
        """.stripIndent()


        when:
        def result = runner('spotbugsMain').build()

        then:
        file('build/reports/spotbugs').isDirectory()

        expectNoDeprecationWarnings(result)
    }

}