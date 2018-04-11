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
import de.qaware.emergen.core.FileObjectGenerator;
import de.qaware.emergen.core.FileObjectGeneratorFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An annotation processor implementation to generate the Builder implementation for
 * annotated with the BuilderSupport annotation.
 *
 * @author lreimer
 */
@SupportedAnnotationTypes({"de.qaware.emergen.apt.builder.BuilderSupport"})
public class BuilderSupportProcessor extends AbstractProcessor {

    private static final String BUILDER_IMPLEMENTATION_TEMPLATE = "Builder.vm";

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Map<String, Object> context = new HashMap<String, Object>();

        FileObjectGenerator generator = FileObjectGeneratorFactory.getInstance(BUILDER_IMPLEMENTATION_TEMPLATE);

        Messager messager = processingEnv.getMessager();
        Filer filer = processingEnv.getFiler();

        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                context.put("date", LocalDate.now().toString());

                BuilderSupport builderSupportAnnotation = element.getAnnotation(BuilderSupport.class);
                messager.printMessage(Diagnostic.Kind.NOTE, "Generating Builder support for " + element.getSimpleName().toString());

                Map<Element, BuilderProperty> builderProperties = getBuilderProperties(element);
                if (builderProperties.isEmpty()) {
                    // nothing to generate
                    messager.printMessage(Diagnostic.Kind.WARNING, "No builder properties defined for " + element.getSimpleName().toString());
                }

                BuilderModel builderModel = new BuilderModel(element, builderSupportAnnotation, builderProperties);
                context.put("builder", builderModel);

                PojoModel pojoModel = element.accept(new PojoModelElementVisitor(), new PojoModel());
                context.put("pojo", pojoModel);

                try {
                    JavaFileObject interfaceFileObject = filer.createSourceFile(builderModel.getCanonicalImplementationName(), element);
                    generator.generate(context, interfaceFileObject);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }

                // clear context for next execution
                context.clear();
            }
        }

        return true;
    }

    /**
     * We support the latest source version of the current execution environment. This is the alternative
     * to using the {@link javax.annotation.processing.SupportedSourceVersion} annotation.
     *
     * @return the latest source version
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * Get the list of builder properties for the given element.
     *
     * @param element the element
     * @return the list of BuilderProperty annotations
     */
    private Map<Element, BuilderProperty> getBuilderProperties(final Element element) {
        Map<Element, BuilderProperty> builderProperties = new HashMap<Element, BuilderProperty>();
        for (Element child : element.getEnclosedElements()) {
            BuilderProperty builderProperty = child.getAnnotation(BuilderProperty.class);
            if (builderProperty != null) {
                if (builderProperty.include()) {
                    builderProperties.put(child, builderProperty);
                }
            } else if (child.getKind() == ElementKind.FIELD && !child.getModifiers().contains(Modifier.STATIC)) {
                builderProperties.put(child, null);
            }
        }

        return builderProperties;
    }
}
