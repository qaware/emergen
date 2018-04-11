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
package de.qaware.emergen.apt.builder.internal;

import javax.lang.model.element.*;

/**
 * A ElementVisitor implementation to create an PojoModel by visiting the different Element types.
 *
 * @author lreimer
 */
public class PojoModelElementVisitor implements ElementVisitor<PojoModel, PojoModel> {

    /**
     * Visits an element.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visit(Element e, PojoModel pojoModel) {
        ElementKind kind = e.getKind();
        if (kind == ElementKind.CLASS) {
            visitType((TypeElement) e, pojoModel);
        } else if (kind == ElementKind.FIELD) {
            visitVariable((VariableElement) e, pojoModel);
        } else if (kind == ElementKind.METHOD) {
            visitExecutable((ExecutableElement) e, pojoModel);
        }
        return pojoModel;
    }

    /**
     * A convenience method equivalent to {@code v.visit(e, null)}.
     *
     * @param e the element to visit
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visit(Element e) {
        return visit(e, new PojoModel());
    }

    /**
     * Visits a package element.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visitPackage(PackageElement e, PojoModel pojoModel) {
        return pojoModel;
    }

    /**
     * Visits a type element.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visitType(TypeElement e, PojoModel pojoModel) {
        pojoModel.setSimpleName(e.getSimpleName().toString());
        pojoModel.setQualifiedName(e.getQualifiedName().toString());

        for (Element child : e.getEnclosedElements()) {
            child.accept(this, pojoModel);
        }

        return pojoModel;
    }

    /**
     * Visits a variable element. For example: fields.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visitVariable(VariableElement e, PojoModel pojoModel) {
        return pojoModel;
    }

    /**
     * Visits an executable element. For example: methods.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visitExecutable(ExecutableElement e, PojoModel pojoModel) {
        return pojoModel;
    }

    /**
     * Visits a type parameter element.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     */
    @Override
    public PojoModel visitTypeParameter(TypeParameterElement e, PojoModel pojoModel) {
        return pojoModel;
    }

    /**
     * Visits an unknown kind of element.
     * This can occur if the language evolves and new kinds
     * of elements are added to the {@code Element} hierarchy.
     *
     * @param e         the element to visit
     * @param pojoModel a visitor-specified parameter
     * @return a visitor-specified result
     * @throws javax.lang.model.element.UnknownElementException a visitor implementation may optionally throw this exception
     */
    @Override
    public PojoModel visitUnknown(Element e, PojoModel pojoModel) {
        return pojoModel;
    }

}
