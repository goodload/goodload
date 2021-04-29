package org.goodload.goodload.userconfig;

import lombok.Getter;
import lombok.Setter;
import org.goodload.goodload.criteria.Criteria;
import org.springframework.stereotype.Component;

import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Configuration parsed or generated from the actual user's input and configuration.
 * It contains objects generated based on the configured values to help in execution.
 * e.g. User provides path to simulation jar file, but we need the actual jar to
 * so we can store that here after loading it.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Component
@Getter
@Setter
public class ParsedUserArgs {
    /**
     * Criteria for goodload.fail-when property
     */
    private Set<Criteria> failPassCriteria;

    /**
     * The loader used to load classes from user's simulation jar file.
     * @since 1.0
     */
    private URLClassLoader userSimulationsClassLoader;

    /**
     * Thread pool to be used for execution of simulations.
     * @since 1.0
     */
    private ExecutorService simulationExecutorService;
}
