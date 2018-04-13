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
package de.qaware.emergen.apt.loader.internal

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import de.qaware.emergen.apt.loader.ServiceImplementation
import de.qaware.emergen.apt.loader.ServiceInterface
import spock.lang.Specification
import spock.lang.Title

import javax.lang.model.SourceVersion
import javax.tools.StandardLocation

import static com.google.testing.compile.Compiler.javac

@Title("Test specification for the ServiceLoaderSupport annotation processor")
class ServiceLoaderSupportProcessorSpec extends Specification {

    def "Generate META-INF/services/de.qaware.emergen.apt.lang.ServiceInterface"() {
        given:
        def loader = ServiceLoader.load(ServiceInterface)
        def iterator = loader.iterator()

        expect:
        iterator.hasNext()
        iterator.next() instanceof ServiceImplementation
    }

    def "Get supported SourceVersion"() {
        given:
        def processor = new ServiceLoaderSupportProcessor()

        expect:
        processor.supportedSourceVersion == SourceVersion.latestSupported()
    }

    def "Annotation processing"() {
        given:
        def compilation = javac()
                .withProcessors(new ServiceLoaderSupportProcessor())
                .compile(JavaFileObjects.forResource("ServiceInterface.java"),
                JavaFileObjects.forResource("ServiceImplementation.java"))

        expect:
        compilation.status() == Compilation.Status.SUCCESS
        compilation.generatedFiles().size() == 3
        compilation.generatedFile(StandardLocation.SOURCE_OUTPUT, "META-INF/services/de.qaware.emergen.apt.lang.ServiceInterface").isPresent()
    }

}
