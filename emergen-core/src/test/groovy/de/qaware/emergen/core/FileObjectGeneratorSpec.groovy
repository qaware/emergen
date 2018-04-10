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
package de.qaware.emergen.core

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import javax.tools.FileObject
import javax.tools.JavaFileObject

@Title("Test specification for the FileObjectGenerator and abstract implementation")
class FileObjectGeneratorSpec extends Specification {

    @Subject
    FileObjectGenerator generator

    void setup() {
        generator = FileObjectGeneratorFactory.getInstance("helloworld.test")
    }

    def "Generate FileObject content"() {
        setup:
        def fileObject = Stub(FileObject)
        def writer = new StringWriter()
        fileObject.openWriter() >> writer

        when:
        generator.generate(["name": "FileObject"], fileObject)

        then:
        writer.toString() == "Hello FileObject"
    }

    def "Generate JavaFileObject content"() {
        setup:
        def javaFileObject = Stub(JavaFileObject)
        def writer = new StringWriter()
        javaFileObject.openWriter() >> writer

        when:
        generator.generate(["name": "JavaFileObject"], javaFileObject)

        then:
        writer.toString() == "Hello JavaFileObject"
    }

    def "Generate Writer content"() {
        setup:
        def writer = new StringWriter()

        when:
        generator.generate(["name": "StringWriter"], writer)

        then:
        writer.toString() == "Hello StringWriter"
    }
}
