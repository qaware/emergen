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

import de.qaware.emergen.apt.builder.BuilderProperty;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * The data model bean to represent the BuilderProperty. Used during generation.
 *
 * @author lreimer
 */
public class BuilderPropertyModel {

    private String name;
    private String type;
    private String defaultValue;
    private boolean withDefaultValue = true;
    private String methodName;
    private boolean directAccessor;
    private boolean setterAccessor;
    private String setterName;
    private boolean constructorAccessor;
    private int constructorPos;

    /**
     * Initialize the model bean with the property element and annotation.
     *
     * @param builderPropertyElement    the builder property element
     * @param builderPropertyAnnotation the builder property annotation
     */
    BuilderPropertyModel(final Element builderPropertyElement, final BuilderProperty builderPropertyAnnotation) {
        this.name = builderPropertyElement.getSimpleName().toString();

        TypeMirror typeMirror = builderPropertyElement.asType();
        this.type = typeMirror.toString();

        this.methodName = getPrefix(builderPropertyAnnotation).concat(StringUtils.capitalize(name));

        String annotationDefaultValue = getDefaultValue(builderPropertyAnnotation);
        boolean defaultValueIsEmpty = StringUtils.isEmpty(annotationDefaultValue);
        boolean defaultValueIsNotEmpty = StringUtils.isNotEmpty(annotationDefaultValue);

        if (typeMirror.getKind().isPrimitive() && defaultValueIsNotEmpty) {
            this.defaultValue = annotationDefaultValue;
        } else if (typeMirror.getKind().isPrimitive() && defaultValueIsEmpty) {
            this.defaultValue = StringUtils.EMPTY;
            this.withDefaultValue = false;
        } else if (type.contains("String") && defaultValueIsNotEmpty) {
            this.defaultValue = "\"".concat(annotationDefaultValue).concat("\"");
        } else if ((typeMirror.getKind() == TypeKind.ARRAY || typeMirror.getKind() == TypeKind.DECLARED) && defaultValueIsNotEmpty) {
            this.defaultValue = annotationDefaultValue;
        } else {
            this.defaultValue = "null";
        }

        BuilderProperty.AccessStrategy accessStrategy = getPropertyAccess(builderPropertyAnnotation);
        if (accessStrategy == BuilderProperty.AccessStrategy.DIRECT) {
            this.directAccessor = true;
            this.setterAccessor = false;
            this.constructorAccessor = false;
            this.setterName = null;
        } else if (accessStrategy == BuilderProperty.AccessStrategy.SETTER) {
            this.directAccessor = false;
            this.setterAccessor = true;
            this.constructorAccessor = false;
            this.setterName = "set".concat(StringUtils.capitalize(name));
        } else if (accessStrategy == BuilderProperty.AccessStrategy.CONSTRUCTOR) {
            this.directAccessor = false;
            this.setterAccessor = false;
            this.constructorAccessor = true;
            this.setterName = null;
            this.constructorPos = builderPropertyAnnotation.constructorPos();
        } else {
            this.directAccessor = false;
            this.setterAccessor = false;
            this.constructorAccessor = false;
            this.setterName = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isWithDefaultValue() {
        return withDefaultValue;
    }

    public boolean isDirectAccessor() {
        return directAccessor;
    }

    public boolean isSetterAccessor() {
        return setterAccessor;
    }

    public boolean isConstructorAccessor() {
        return constructorAccessor;
    }

    public String getSetterName() {
        return setterName;
    }

    public int getConstructorPos() {
        return constructorPos;
    }

    private String getDefaultValue(final BuilderProperty annotation) {
        if (annotation != null) {
            return annotation.defaultValue();
        } else {
            try {
                return (String) BuilderProperty.class.getMethod("defaultValue").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private String getPrefix(final BuilderProperty annotation) {
        if (annotation != null) {
            return annotation.prefix();
        } else {
            try {
                return (String) BuilderProperty.class.getMethod("prefix").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private BuilderProperty.AccessStrategy getPropertyAccess(final BuilderProperty annotation) {
        if (annotation != null) {
            return annotation.propertyAccess();
        } else {
            try {
                return (BuilderProperty.AccessStrategy) BuilderProperty.class.getMethod("propertyAccess").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
