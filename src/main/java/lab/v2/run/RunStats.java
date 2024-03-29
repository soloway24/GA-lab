package lab.v2.run;

import lab.v2.Individual;

import java.util.Map;

public record RunStats(Map<Individual, ? extends Number> finalPopulation,
                       boolean isSuccessful) {
}
