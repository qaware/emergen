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
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.Diagnostic;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * An annotation processor implementation to enforcer certain design rules.
 *
 * @author lreimer
 */
@SupportedAnnotationTypes({"de.qaware.emergen.apt.enforcer.EnforcerSupport"})
public class DesignEnforcerProcessor extends AbstractProcessor {

    private ScriptEngine engine;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Invocable invocable = (Invocable) engine;
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                EnforcerSupport enforcerSupport = element.getAnnotation(EnforcerSupport.class);
                if (!enforcerSupport.value()) {
                    // skip this element
                    continue;
                }

                try {
                    Boolean valid = (Boolean) invocable.invokeFunction("enforce", typeElement, element);
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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(new InputStreamReader(getClass().getResourceAsStream("/rules.js")));
        } catch (ScriptException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
