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

import spock.lang.Specification
import spock.lang.Title

import javax.lang.model.element.*

@Title('Test specification for the PojoModel and associated element visitor')
class PojoModelSpec extends Specification {
    def "Check PojoModel properties"() {
        given:
        def pojo = new PojoModel()
        pojo.simpleName = 'PojoModel'
        pojo.qualifiedName = 'de.qaware.emergen.apt.builder.internal.PojoModel'

        expect:
        pojo.simpleName == 'PojoModel'
        pojo.qualifiedName == 'de.qaware.emergen.apt.builder.internal.PojoModel'
    }

    def "Visit class"() {
        given:
        def visitor = new PojoModelElementVisitor()
        def element = Mock(TypeElement)

        when:
        def model = visitor.visit(element)

        then:
        1 * element.kind >> ElementKind.CLASS
        1 * element.getSimpleName() >> new TestName(name: "Class")
        1 * element.getQualifiedName() >> new TestName(name: "package.Class")
        1 * element.getEnclosedElements() >> Collections.emptyList()

        model
        model.simpleName == 'Class'
        model.qualifiedName == 'package.Class'
    }

    def "Visit field"() {
        given:
        def visitor = new PojoModelElementVisitor()
        def element = Mock(VariableElement)

        when:
        def model = visitor.visit(element)

        then:
        1 * element.kind >> ElementKind.FIELD
        0 * element._
        model
    }

    def "Visit method"() {
        given:
        def visitor = new PojoModelElementVisitor()
        def element = Mock(ExecutableElement)

        when:
        def model = visitor.visit(element)

        then:
        1 * element.kind >> ElementKind.METHOD
        0 * element._
        model
    }

    def "Visit unknown element"() {
        given:
        def visitor = new PojoModelElementVisitor()
        def element = Mock(Element)

        when:
        def model = visitor.visitUnknown(element, new PojoModel())

        then:
        model
        0 * element._
    }

    def "Visit package element"() {
        given:
        def visitor = new PojoModelElementVisitor()
        def element = Mock(PackageElement)

        when:
        def model = visitor.visitPackage(element, new PojoModel())

        then:
        model
        0 * element._
    }

    def "Visit type parameter element"() {
        given:
        def visitor = new PojoModelElementVisitor()
        def element = Mock(TypeParameterElement)

        when:
        def model = visitor.visitTypeParameter(element, new PojoModel())

        then:
        model
        0 * element._
    }

    class TestName implements Name {

        String name

        @Override
        boolean contentEquals(CharSequence cs) {
            return false
        }

        @Override
        int length() {
            return name.length()
        }

        @Override
        char charAt(int index) {
            return name.charAt(index)
        }

        @Override
        CharSequence subSequence(int start, int end) {
            return name.subSequence(start, end
            )
        }

        @Override
        String toString() {
            return name
        }
    }
}
