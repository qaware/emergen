/*
 * MIT License
 *
 * Copyright (c) 2018 QAware GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.qaware.emergen.apt.enforcer.internal

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import spock.lang.Specification

import static com.google.testing.compile.Compiler.javac

class DesignEnforcerProcessorSpec extends Specification {
    def "Process EnforcerSupport annotation on SomeClass"() {
        given:
        def compilation = javac()
                .withProcessors(new DesignEnforcerProcessor())
                .withOptions("-Aenforcer.annotations=de.qaware.emergen.apt.enforcer.EnforcerSupport")
                .compile(JavaFileObjects.forResource("SomeClass.java"))

        expect:
        compilation.status() == Compilation.Status.SUCCESS
    }

    def "Process EnforcerSupport annotation on SkippedClass"() {
        given:
        def compilation = javac()
                .withProcessors(new DesignEnforcerProcessor())
                .compile(JavaFileObjects.forResource("SkippedClass.java"))

        expect:
        compilation.status() == Compilation.Status.SUCCESS
    }

    def "Process EnforcerSupport annotation on TestBean with test-rules.js"() {
        given:
        def compilation = javac()
                .withProcessors(new DesignEnforcerProcessor())
                .withOptions(
                "-Aenforcer.annotations=de.qaware.emergen.apt.enforcer.EnforcerSupport", "-Aenforcer.rules=src/test/resources/test-rules.js")
                .compile(JavaFileObjects.forResource("TestBean.java"))

        expect:
        compilation.status() == Compilation.Status.FAILURE
    }

    def "Process EnforcerSupport annotation on TestBean with unknown method"() {
        given:
        def compilation = javac()
                .withProcessors(new DesignEnforcerProcessor())
                .compile(JavaFileObjects.forResource("TestBean.java"))

        expect:
        compilation.status() == Compilation.Status.FAILURE
    }
}
