package org.divsgaur.goodload;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserArgs {
    private String configFileName;
    private String jarFileName;
    private String[] simulationsToExecute;
}
