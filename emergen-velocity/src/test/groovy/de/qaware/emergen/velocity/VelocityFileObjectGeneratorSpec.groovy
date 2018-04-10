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
package de.qaware.emergen.velocity

import de.qaware.emergen.core.FileObjectGenerator
import de.qaware.emergen.core.FileObjectGeneratorFactory
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("Test specification for the VelocityFileObjectGenerator implementation")
class VelocityFileObjectGeneratorSpec extends Specification {
    @Subject
    FileObjectGenerator generator

    void setup() {
        generator = FileObjectGeneratorFactory.getInstance("test.vm")
    }

    def "Supports suffixes"() {
        expect:
        generator.supports("sometemplate.vm")
        generator.supports("uppercase.VM")
        generator.supports("mixed.Vm")
        !generator.supports("another.unknown")
    }

    def "Generate content from Velocity template"() {
        setup:
        def writer = new StringWriter()

        when:
        generator.generate(["greeting": "Hello Apache Velocity."], writer)

        then:
        writer.toString() == "Content: Hello Apache Velocity."
    }
}
