/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.internal.tasks.testing.junit;

import org.gradle.api.internal.tasks.testing.TestClassProcessor;
import org.gradle.api.internal.tasks.testing.TestClassRunInfo;
import org.gradle.api.internal.tasks.testing.TestResultProcessor;
import org.gradle.api.internal.tasks.testing.processors.CaptureTestOutputTestResultProcessor;
import org.gradle.api.internal.tasks.testing.results.AttachParentTestResultProcessor;
import org.gradle.internal.TimeProvider;
import org.gradle.internal.TrueTimeProvider;
import org.gradle.internal.id.IdGenerator;
import org.gradle.listener.ListenerBroadcast;
import org.gradle.logging.StandardOutputRedirector;
import org.gradle.messaging.actor.Actor;
import org.gradle.messaging.actor.ActorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JUnitTestClassProcessor implements TestClassProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitTestClassProcessor.class);
    private final File testResultsDir;
    private final IdGenerator<?> idGenerator;
    private final ActorFactory actorFactory;
    private final StandardOutputRedirector outputRedirector;
    private final TimeProvider timeProvider = new TrueTimeProvider();
    private JUnitTestClassExecuter executer;
    private Actor resultProcessorActor;

    public JUnitTestClassProcessor(File testResultsDir, IdGenerator<?> idGenerator, ActorFactory actorFactory,
                                   StandardOutputRedirector standardOutputRedirector) {
        this.testResultsDir = testResultsDir;
        this.idGenerator = idGenerator;
        this.actorFactory = actorFactory;
        this.outputRedirector = standardOutputRedirector;
    }

    public void startProcessing(TestResultProcessor resultProcessor) {
        // Build a result processor chain
        ClassLoader applicationClassLoader = Thread.currentThread().getContextClassLoader();
        ListenerBroadcast<TestResultProcessor> processors = new ListenerBroadcast<TestResultProcessor>(TestResultProcessor.class);
        processors.add(new JUnitXmlReportGenerator(testResultsDir));
        processors.add(resultProcessor);
        TestResultProcessor resultProcessorChain = new AttachParentTestResultProcessor(new CaptureTestOutputTestResultProcessor(processors.getSource(), outputRedirector));
        TestClassExecutionEventGenerator eventGenerator = new TestClassExecutionEventGenerator(resultProcessorChain, idGenerator, timeProvider);

        // Wrap the result processor chain up in a blocking actor, to make the whole thing thread-safe
        resultProcessorActor = actorFactory.createBlockingActor(eventGenerator);
        TestResultProcessor threadSafeResultProcessor = resultProcessorActor.getProxy(TestResultProcessor.class);
        TestClassExecutionListener threadSafeTestClassListener = resultProcessorActor.getProxy(TestClassExecutionListener.class);

        // Build the JUnit adaptor stuff
        JUnitTestEventAdapter junitEventAdapter = new JUnitTestEventAdapter(threadSafeResultProcessor, timeProvider, idGenerator);
        executer = new JUnitTestClassExecuter(applicationClassLoader, junitEventAdapter, threadSafeTestClassListener);
    }

    public void processTestClass(TestClassRunInfo testClass) {
        LOGGER.debug("Executing test class {}", testClass.getTestClassName());
        executer.execute(testClass.getTestClassName());
    }

    public void stop() {
        resultProcessorActor.stop();
    }
}
