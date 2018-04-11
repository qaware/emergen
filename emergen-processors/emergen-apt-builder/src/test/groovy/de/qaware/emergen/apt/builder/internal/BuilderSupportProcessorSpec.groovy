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
package de.qaware.emergen.apt.builder.internal

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import spock.lang.Specification
import spock.lang.Title

import javax.lang.model.SourceVersion

import static com.google.testing.compile.Compiler.javac

@Title('Test specification for the Builder support annotation processor')
class BuilderSupportProcessorSpec extends Specification {

    static String TEST_POJO = """
    package test;
    
    @de.qaware.emergen.apt.builder.BuilderSupport
    public class TestPojo {
    
        @de.qaware.emergen.apt.builder.BuilderProperty
        private String hello;
     
        public String getHello() {
            return hello;
        }

        public void setHello(String hello) {
            this.hello = hello;
        }
    }
    """

    def "Get supported SourceVersion"() {
        expect:
        new BuilderSupportProcessor().supportedSourceVersion == SourceVersion.latestSupported()
    }

    def "Annotation processing TestPojo"() {
        given:
        def compilation = javac()
                .withProcessors(new BuilderSupportProcessor())
                .compile(JavaFileObjects.forSourceString("test.TestPojo", TEST_POJO))

        expect:
        compilation.status() == Compilation.Status.SUCCESS
        compilation.generatedSourceFile("test.TestPojoBuilder").isPresent()
    }

    def "Annotation processing ComplexPojo"() {
        given:
        def compilation = javac()
                .withProcessors(new BuilderSupportProcessor())
                .compile(JavaFileObjects.forResource("ComplexPojo.java"))

        expect:
        compilation.status() == Compilation.Status.SUCCESS
        compilation.generatedSourceFile("de.qaware.emergen.apt.builder.ComplexPojoBuilder").isPresent()
    }
}
