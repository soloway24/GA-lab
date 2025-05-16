package lab.selection;

import lab.Individual;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder(setterPrefix = "with")
@Getter
public class SelectionContext {

    private final Map<Individual, ? extends Number> individualToFitness;
    private Integer ni;
    private Double worstFitness;
    private Double s0;

    public SelectionContext(Map<Individual, ? extends Number> individualToFitness) {
        this.individualToFitness = individualToFitness;
    }

    public SelectionContext(Map<Individual, ? extends Number> individualToFitness,
                            Integer ni,
                            Double worstFitness,
                            Double s0) {
        this.individualToFitness = individualToFitness;
        this.ni = ni;
        this.worstFitness = worstFitness;
        this.s0 = s0;
    }
}