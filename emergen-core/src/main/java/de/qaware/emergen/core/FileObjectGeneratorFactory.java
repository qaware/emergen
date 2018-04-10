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
        ServiceLoader<FileObjectGenerator> generators = ServiceLoader.load(FileObjectGenerator.class);
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
