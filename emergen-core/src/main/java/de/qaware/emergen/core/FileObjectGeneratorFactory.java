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
package de.qaware.emergen.core;

import java.util.ServiceLoader;

/**
 * The interface definition for a FileObjectGeneratorFactory. Defines methods
 * to get template specific FileObjectGenerator instances.
 *
 * @author lreimer
 */
public interface FileObjectGeneratorFactory {
    /**
     * Create a FileObjectGenerator for the given template name.
     *
     * @param template the template
     * @return a suitable file object generator
     * @throws IllegalArgumentException of the template is not supported
     */
    static FileObjectGenerator getInstance(final String template) {
        ServiceLoader<FileObjectGenerator> generators = ServiceLoader.load(FileObjectGenerator.class, FileObjectGenerator.class.getClassLoader());
        generators.reload();

        for (FileObjectGenerator generator : generators) {
            if (generator.supports(template)) {
                generator.initialize(template);
                return generator;
            }
        }

        throw new IllegalArgumentException("Unsupported template " + template);
    }


}
