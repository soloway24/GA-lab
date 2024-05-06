package lab.metric;

import lab.Individual;

public record IndividualMetrics(Individual individual,
                                Long ones,
                                Double phenotype,
                                Double fitness) {
}
