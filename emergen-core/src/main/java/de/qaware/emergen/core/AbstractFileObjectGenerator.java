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
