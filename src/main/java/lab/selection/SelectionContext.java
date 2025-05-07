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

    public SelectionContext(Map<Individual, ? extends Number> individualToFitness) {
        this.individualToFitness = individualToFitness;
    }

    public SelectionContext(Map<Individual, ? extends Number> individualToFitness,
                            Integer ni,
                            Double worstFitness) {
        this.individualToFitness = individualToFitness;
        this.ni = ni;
        this.worstFitness = worstFitness;
    }
}