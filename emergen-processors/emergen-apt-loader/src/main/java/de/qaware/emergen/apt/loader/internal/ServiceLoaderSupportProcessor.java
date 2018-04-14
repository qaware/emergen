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
package de.qaware.emergen.apt.loader.internal;

import de.qaware.emergen.apt.loader.ServiceLoaderSupport;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An annotation processor implementation to generate the Java ServiceLoader files
 * for any types annotated with the ServiceLoaderSupport annotation.
 *
 * @author lreimer
 */
@SupportedAnnotationTypes({"de.qaware.emergen.apt.loader.ServiceLoaderSupport"})
public class ServiceLoaderSupportProcessor extends AbstractProcessor {

    private static final String EMPTY_PACKAGE = "";
    private static final String BASEPATH = "META-INF/services/";
    private static final String LINE_SEPARATOR = "line.separator";

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Map<String, List<String>> services = new HashMap<>();
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                Set<String> serviceInterfaces = getServiceInterfaces(element);
                String implementationName = ((TypeElement) element).getQualifiedName().toString();

                for (String serviceInterface : serviceInterfaces) {
                    if (!services.containsKey(serviceInterface)) {
                        services.put(serviceInterface, new ArrayList<>());
                    }

                    // add the annotated type element full qualified name
                    services.get(serviceInterface).add(implementationName);
                }
            }
        }

        // now process all the services with their implementation
        Messager messager = processingEnv.getMessager();
        Filer filer = processingEnv.getFiler();
        for (Map.Entry<String, List<String>> service : services.entrySet()) {
            writeServiceFile(service, filer, messager);
        }

        return true;
    }

    private Set<String> getServiceInterfaces(Element element) {
        ServiceLoaderSupport serviceAnnotation = element.getAnnotation(ServiceLoaderSupport.class);
        String defaultInterface = serviceAnnotation.value();

        Set<String> serviceInterfaces;
        if ("".equals(defaultInterface)) {
            serviceInterfaces = getAllServiceInterfaces((TypeElement) element);
        } else {
            serviceInterfaces = Collections.singleton(defaultInterface);
        }

        return serviceInterfaces;
    }

    private void writeServiceFile(Map.Entry<String, List<String>> service, Filer filer, Messager messager) {
        String filename = BASEPATH.concat(service.getKey());
        messager.printMessage(Diagnostic.Kind.OTHER, "Writing service file " + filename);

        try (Writer writer = filer.createResource(StandardLocation.SOURCE_OUTPUT, EMPTY_PACKAGE, filename).openWriter()) {
            for (String implementation : service.getValue()) {
                writer.write(implementation);
                writer.write(System.getProperty(LINE_SEPARATOR));
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            throw new IllegalArgumentException("Error writing service file.", e);
        }
    }

    private Set<String> getAllServiceInterfaces(TypeElement element) {
        List<? extends TypeMirror> interfaces = element.getInterfaces();

        return interfaces.stream().map(interfaceMirror -> {
            Element interfaceElement = ((DeclaredType) interfaceMirror).asElement();
            return ((TypeElement) interfaceElement).getQualifiedName().toString();
        }).collect(Collectors.toSet());
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
}
