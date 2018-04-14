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
package de.qaware.emergen.apt.enforcer.internal;

import de.qaware.emergen.apt.enforcer.EnforcerSupport;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.Diagnostic;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * An annotation processor implementation to enforcer certain design rules.
 *
 * @author lreimer
 */
@SupportedOptions({DesignEnforcerProcessor.ENFORCER_ANNOTATIONS, DesignEnforcerProcessor.ENFORCER_RULES})
public class DesignEnforcerProcessor extends AbstractProcessor {

    /**
     * The annotations the enforcer APT should check. Comma separated list of fully
     * qualified annotation class names.
     */
    public static final String ENFORCER_ANNOTATIONS = "enforcer.annotations";

    /**
     * The path to a JavaScript file that implements the rules to enforce. The file must define one method:
     * <code>
     * var enforce = function (annotation, element) {
     * if (annotation.getQualifiedName() == "de.qaware.emergen.apt.enforcer.EnforcerSupport") {
     * // everything OK
     * return true;
     * } else {
     * // not OK
     * return false;
     * }
     * };
     * </code>
     */
    public static final String ENFORCER_RULES = "enforcer.rules";

    private ScriptEngine engine;
    private Set<String> supportedAnnotationTypes;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Invocable invocable = (Invocable) engine;
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                EnforcerSupport enforcerSupport = element.getAnnotation(EnforcerSupport.class);
                if (!shouldEnforce(enforcerSupport)) {
                    continue;
                }

                try {
                    String functionName = getFunctionName(enforcerSupport);
                    Boolean valid = (Boolean) invocable.invokeFunction(functionName, typeElement, element);
                    if (!valid) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error enforcing design rules.", element);
                    }
                } catch (ScriptException | NoSuchMethodException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
                    return false;
                }
            }
        }

        return true;
    }

    private String getFunctionName(EnforcerSupport enforcerSupport) {
        if (enforcerSupport != null) {
            return Objects.toString(enforcerSupport.rule(), "enforce");
        } else {
            return "enforce";
        }
    }

    private boolean shouldEnforce(EnforcerSupport enforcerSupport) {
        return enforcerSupport == null || enforcerSupport.value();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        engine = new ScriptEngineManager().getEngineByName("nashorn");

        // read the options for this processor
        Map<String, String> options = processingEnv.getOptions();

        String annotations = options.getOrDefault(ENFORCER_ANNOTATIONS, "de.qaware.emergen.apt.enforcer.EnforcerSupport");
        this.supportedAnnotationTypes = new HashSet<>(Arrays.asList(annotations.split(",")));

        String rules = options.get(ENFORCER_RULES);
        InputStream inputStream;
        try {
            if (rules == null) {
                inputStream = getClass().getResourceAsStream("/default-rules.js");
            } else {
                inputStream = new FileInputStream(rules);
            }

            // now initialize the JavaScript engine with the rules
            engine.eval(new InputStreamReader(inputStream));
        } catch (ScriptException | FileNotFoundException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
