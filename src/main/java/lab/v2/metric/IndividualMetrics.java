package lab.v2.metric;

import lab.v2.Individual;

public record IndividualMetrics(Individual individual,
                                long ones,
                                double phenotype,
                                double fitness) {
}
