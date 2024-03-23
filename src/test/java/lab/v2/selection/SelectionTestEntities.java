package lab.v2.selection;

import lab.model.Individual;

import java.util.Map;

public class SelectionTestEntities {

    public static final Individual INDIVIDUAL_1 = new Individual("0000000001");
    public static final Individual INDIVIDUAL_2 = new Individual("0000000010");
    public static final Individual INDIVIDUAL_3 = new Individual("0000000011");
    public static final Individual INDIVIDUAL_4 = new Individual("0000000100");

    public static final Map<Individual, Integer> INDIVIDUAL_TO_FITNESS_INT = Map.of(
            INDIVIDUAL_1, 2,
            INDIVIDUAL_2, 10,
            INDIVIDUAL_3, 20,
            INDIVIDUAL_4, 8
    );
    public static final Map<Individual, Double> INDIVIDUAL_TO_FITNESS_DOUBLE = Map.of(
            INDIVIDUAL_1, 2.0,
            INDIVIDUAL_2, 10.0,
            INDIVIDUAL_3, 20.0,
            INDIVIDUAL_4, 8.0
    );
    public static final Map<Individual, Double> INDIVIDUAL_TO_PERCENTAGE = Map.of(
            INDIVIDUAL_1, 0.05,
            INDIVIDUAL_2, 0.25,
            INDIVIDUAL_3, 0.5,
            INDIVIDUAL_4, 0.2
    );
    public static final Map<Individual, Double> INDIVIDUAL_TO_EXPECTED_QUANTITY = Map.of(
            INDIVIDUAL_1, 0.2,
            INDIVIDUAL_2, 1.0,
            INDIVIDUAL_3, 2.0,
            INDIVIDUAL_4, 0.8
    );
}
