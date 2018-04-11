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
package de.qaware.emergen.apt.builder;

import java.lang.annotation.*;

/**
 * An annotation to mark fields as Builder property. The fields must be accessible by the builder.
 * Possible values are direct field access, setter and constructor initialization.
 *
 * @author lreimer
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value = {ElementType.FIELD})
public @interface BuilderProperty {
    /**
     * The property access strategy for the builder.
     */
    enum AccessStrategy {
        DIRECT, SETTER, CONSTRUCTOR
    }

    /**
     * The prefix used of the Builder property accessor. Default is: with.
     *
     * @return builder property accessor prefix
     */
    String prefix() default "with";

    /**
     * The default value for this member.
     *
     * @return the default value
     */
    String defaultValue() default "";

    /**
     * How will the property be accessed.
     *
     * @return the type of property access
     */
    AccessStrategy propertyAccess() default AccessStrategy.SETTER;

    /**
     * The position of the constructor argument of this member.
     * Only applicable if {@link #propertyAccess()} is CONSTRUCTOR.
     *
     * @return the constructorPos.
     */
    int constructorPos() default 0;

    /**
     * Whether this property will be included by the builder.
     *
     * @return if property will be included
     */
    boolean include() default true;
}
