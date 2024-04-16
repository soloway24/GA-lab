package lab.v2.metric;

import lab.v2.Individual;

public record IndividualMetrics(Individual individual,
                                Long ones,
                                Double phenotype,
                                Double fitness) {
}
