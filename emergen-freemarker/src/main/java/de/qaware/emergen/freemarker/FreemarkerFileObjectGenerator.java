package de.qaware.emergen.freemarker;

import de.qaware.emergen.core.AbstractFileObjectGenerator;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A FileObjectGenerator implementation that uses the Freemarker template engine to generate
 * the file resources.
 *
 * @author lreimer
 */
public class FreemarkerFileObjectGenerator extends AbstractFileObjectGenerator {

    private Configuration configuration;

    @Override
    public void initialize(String template) {
        super.initialize(template);

        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setClassForTemplateLoading(getClass(), "/");
        configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
        configuration.setAutoFlush(true);
        configuration.setWhitespaceStripping(true);
    }

    @Override
    protected Set<String> getSupportedSuffixes() {
        return Collections.singleton(".ftl");
    }

    /**
     * Helper method to generate the content using the given context into the specified writer. If autoClose
     * is enabled the writer will be closed.
     *
     * @param context   the generator context
     * @param writer    the writer
     * @param autoClose the auto close flag
     * @throws java.io.IOException in case of a generation error
     */
    protected void generate(final Map<String, Object> context, final Writer writer, boolean autoClose) throws IOException {
        try {
            Template template = configuration.getTemplate(getTemplate());
            template.process(context, writer);
        } catch (TemplateException e) {
            throw new IOException(e);
        } finally {
            if (autoClose) {
                writer.close();
            }
        }
    }
}
