package de.qaware.emergen.core;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * The FileObject generator interface defines methods to generate different
 * types of FileObjects.
 *
 * @author lreimer
 */
public interface FileObjectGenerator {
    /**
     * Generates the content for a Resource FileObject with the given parameters.
     *
     * @param context    the generator context
     * @param fileObject the file object
     * @throws IOException in case of an error writing the resource file
     */
    void generate(Map<String, Object> context, final FileObject fileObject) throws IOException;

    /**
     * Generates the content for a Java source FileObject with the given parameters.
     *
     * @param context        the generator context
     * @param javaFileObject the Java source file object
     * @throws IOException in case of an error writing the java source file
     */
    void generate(Map<String, Object> context, JavaFileObject javaFileObject) throws IOException;

    /**
     * Generates the content using the given context and writer. The caller is responsible for
     * the writer, this method will not close the writer.
     *
     * @param context the generator context
     * @param writer  the writer to generate into
     * @throws IOException in case of an error writing the content
     */
    void generate(Map<String, Object> context, Writer writer) throws IOException;

    /**
     * Initialize the generator instance with the template name.
     *
     * @param template the template name
     */
    void initialize(final String template);

    /**
     * Checks of the given template is supported by the concrete generator.
     *
     * @param template the template name
     * @return true of supported, otherwise false
     */
    boolean supports(String template);
}
