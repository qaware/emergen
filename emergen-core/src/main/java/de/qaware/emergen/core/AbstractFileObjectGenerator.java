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

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * An abstract FileObjectGenerator implementation to provide the basic boiler plate code
 * for generating files. Subclasses only need to add the template engine specific parts.
 *
 * @author lreimer
 */
public abstract class AbstractFileObjectGenerator implements FileObjectGenerator {

    private String template;

    /**
     * Generates the content for a Resource FileObject with the given parameters.
     *
     * @param context    the generator context
     * @param fileObject the file object
     */
    @Override
    public final void generate(final Map<String, Object> context, final FileObject fileObject) throws IOException {
        generate(context, fileObject.openWriter(), true);
    }

    /**
     * Generates the content for a Java source FileObject with the given parameters.
     *
     * @param context        the generator context
     * @param javaFileObject the Java source file object
     */
    @Override
    public final void generate(final Map<String, Object> context, final JavaFileObject javaFileObject) throws IOException {
        generate(context, javaFileObject.openWriter(), true);
    }

    /**
     * Generate the content using the given context into the specified writer.
     *
     * @param context the generator context
     * @param writer  the writer
     * @throws IOException in case of a generation error
     */
    @Override
    public final void generate(final Map<String, Object> context, final Writer writer) throws IOException {
        generate(context, writer, false);
    }

    @Override
    public boolean supports(String template) {
        String suffix = template.substring(template.lastIndexOf('.')).toLowerCase(Locale.ENGLISH);
        return getSupportedSuffixes().contains(suffix);
    }

    /**
     * Returns a set of supported suffixes by the concrete subclass. All suffixes
     * must be lowercase and start with a dot.
     *
     * @return the set of supported suffixes
     */
    protected abstract Set<String> getSupportedSuffixes();

    /**
     * Helper method to generate the content using the given context into the specified writer. If autoClose
     * is enabled the writer will be closed.
     *
     * @param context   the generator context
     * @param writer    the writer
     * @param autoClose the auto close flag
     * @throws IOException in case of a generation error
     */
    protected abstract void generate(final Map<String, Object> context, final Writer writer, boolean autoClose) throws IOException;

    @Override
    public void initialize(final String template) {
        assert (template != null);
        assert (supports(template));

        this.template = template;
    }

    /**
     * Get the template name.
     *
     * @return the template name
     */
    protected final String getTemplate() {
        return template;
    }
}
