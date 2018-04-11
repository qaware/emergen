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
import de.qaware.emergen.apt.builder.BuilderSupport;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The data model bean to represent the Builder. Used during generation.
 *
 * @author reimer
 */
public class BuilderModel {
    private static final String CANONICAL_FORMAT = "{0}.{1}";
    private static final String IMPLEMENTATION_NAME_SUFFIX = "Builder";

    private final String implementationName;
    private final String entityName;
    private final String implementationPackage;
    private final List<BuilderPropertyModel> builderProperties;

    /**
     * Initialize the builder model with the given references.
     *
     * @param element                  the annotated element
     * @param builderSupportAnnotation the annotation itself
     * @param builderPropertyElements  the annotated builder property elements
     */
    BuilderModel(final Element element, final BuilderSupport builderSupportAnnotation,
                 final Map<Element, BuilderProperty> builderPropertyElements) {
        entityName = element.getSimpleName().toString();
        implementationName = entityName.concat(IMPLEMENTATION_NAME_SUFFIX);
        if (StringUtils.isEmpty(builderSupportAnnotation.implementationPackage())) {
            String elementType = element.asType().toString();
            implementationPackage = elementType.substring(0, elementType.lastIndexOf('.'));
        } else {
            implementationPackage = builderSupportAnnotation.implementationPackage();
        }

        builderProperties = new ArrayList<>();
        for (Map.Entry<Element, BuilderProperty> entry : builderPropertyElements.entrySet()) {
            builderProperties.add(new BuilderPropertyModel(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Returns the uncapitalized entity name used for static builder method name.
     *
     * @return the uncapitalized entity name
     */
    public String getUncapitalizedPojoName() {
        return String.valueOf(Character.toLowerCase(entityName.charAt(0))) + entityName.substring(1);
    }

    public String getImplementationName() {
        return implementationName;
    }

    public String getImplementationPackage() {
        return implementationPackage;
    }

    public List<BuilderPropertyModel> getBuilderProperties() {
        return builderProperties;
    }

    /**
     * Returns the fully qualified name for this Builder.
     *
     * @return the canonical name
     */
    public String getCanonicalImplementationName() {
        return MessageFormat.format(CANONICAL_FORMAT, implementationPackage, implementationName);
    }

    /**
     * Get a view on the builder properties that are constructor args.
     *
     * @return list of constructor args
     */
    public List<BuilderPropertyModel> getConstructorArgs() {
        return builderProperties.stream()
                .filter(BuilderPropertyModel::isConstructorAccessor)
                .sorted(Comparator.comparing(BuilderPropertyModel::getConstructorPos))
                .collect(Collectors.toList());
    }

    public String getConstructorSignature() {
        List<String> names = getConstructorArgs().stream().map(BuilderPropertyModel::getName).collect(Collectors.toList());
        return String.join(", ", names);
    }
}
