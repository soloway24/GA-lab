package lab.v2.run;

import lab.v2.parameters.RunConfiguration;

public record RunPoolConfiguration(RunConfiguration runConfiguration,
                                   int runPoolSize) {
}