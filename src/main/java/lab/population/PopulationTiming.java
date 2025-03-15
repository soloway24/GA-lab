package lab.population;

import lombok.Getter;

@Getter
public class PopulationTiming {

    private final int iteration;
    private final double fMaxPercentage;

    public PopulationTiming(int iteration, double fMaxPercentage) {
        this.iteration = iteration;
        this.fMaxPercentage = fMaxPercentage;
    }
}