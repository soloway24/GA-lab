package lab.v2.run;

import lab.v2.parameters.RunConfiguration;
import lab.v2.population.Population;

public record Run(RunConfiguration runConfiguration,
                  Population population) {
}
