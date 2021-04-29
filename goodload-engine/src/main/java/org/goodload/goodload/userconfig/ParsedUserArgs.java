package org.goodload.goodload.userconfig;

import lombok.Getter;
import org.goodload.goodload.criteria.Criteria;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Configuration parsed from the actual user's input and configuration.
 * It contains objects for the configured values which can not be used as is.
 * e.g. User provides path to simulation jar file, but we need the actual jar to
 * so we can store that here after loading it.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Component
public class ParsedUserArgs {
    @Getter
    private Set<Criteria> failPassCriteria;

}
