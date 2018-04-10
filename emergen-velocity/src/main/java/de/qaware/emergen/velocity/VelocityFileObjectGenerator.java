package de.qaware.emergen.velocity;

import de.qaware.emergen.core.AbstractFileObjectGenerator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A FileObjectGenerator implementation that uses the Velocity template engine to generate
 * the file resources.
 *
 * @author lreimer
 */
public class VelocityFileObjectGenerator extends AbstractFileObjectGenerator {

    private VelocityEngine velocityEngine;

    @Override
    public void initialize(String template) {
        super.initialize(template);

        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        try {
            props.load(url.openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Error loading velocity.properties");
        }

        // and initialize the engine
        velocityEngine = new VelocityEngine(props);
        velocityEngine.init();
    }

    @Override
    protected Set<String> getSupportedSuffixes() {
        return Collections.singleton(".vm");
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
            Template vt = velocityEngine.getTemplate(getTemplate());
            vt.merge(new VelocityContext(context), writer);
        } finally {
            if (autoClose) {
                writer.close();
            }
        }
    }
}
