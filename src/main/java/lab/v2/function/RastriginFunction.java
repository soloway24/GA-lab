package lab.v2.function;

import lab.parameters.Encoding;
import lab.v2.Individual;
import lab.v2.operator.OperatorType;
import lab.v2.population.PopulationType;
import lab.v2.selection.SelectorType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.*;
import static java.util.Optional.*;
import static lab.parameters.Encoding.GRAY;
import static lab.parameters.Encoding.STANDARD;
import static lab.utils.Constants.PRECISION_BASE;
import static lab.v2.encoding.DecoderV2.decodeV2;
import static lab.v2.identifier.SuccessfulRunIdentifier.isSuccessfulRealFunction;
import static lab.v2.operator.OperatorType.NONE;
import static lab.v2.population.PopulationType.*;
import static lab.v2.selection.SelectorType.SUS;
import static lab.v2.validators.EncodingSpaceValidator.validateEncodingSpace;

public class RastriginFunction implements FitnessFunctionV2<Double, Double> {

    private static final Individual OPTIMAL_STANDARD_INDIVIDUAL = new Individual("1000000000", STANDARD);
    private static final Individual OPTIMAL_GRAY_INDIVIDUAL = new Individual("1100000000", GRAY);

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_STANDARD_INDIVIDUAL,
            GRAY, OPTIMAL_GRAY_INDIVIDUAL
    );

    private final Integer chromosomeLength;
    private final Double minX;
    private final Double maxX;
    private final int argumentPrecision;
    private final double a;

    public RastriginFunction(int chromosomeLength, double minX, double maxX, int argumentPrecision, double a) {
        validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;
        this.a = a;
    }

    @Override
    public String getName() {
        return "Rastrigin";
    }

    @Override
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    @Override
    public List<Encoding> getSupportedEncodings() {
//        return List.of(STANDARD, GRAY);
//        return List.of(STANDARD);
        return List.of(GRAY);
    }

    @Override
    public Double getMinFitness() {
        return 20.0753;
    }

    @Override
    public Double getMaxFitness() {
        return 49.0;
    }

    @Override
    public Optional<Double> getMinX() {
        return of(minX);
    }

    @Override
    public Optional<Double> getMaxX() {
        return of(maxX);
    }

    @Override
    public Optional<Double> getOptimalX() {
        return of(0.0);
    }

    @Override
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return ofNullable(ENCODING_TO_OPTIMAL.get(encoding))
                .map(Individual::new);
    }

    @Override
    public Double evaluate(Individual individual) {
        Double x = decodeV2(individual, this);
        return abs(10 * cos(2 * PI * a) - pow(a, 2)) + 10 * cos(2 * PI * x) - pow(x, 2);
    }

    @Override
    public Optional<Double> convertToX(long decimalValue) {
        double scaledValue = decimalValue / pow(PRECISION_BASE, argumentPrecision);
        return of(minX + scaledValue);
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        if (operatorType == NONE) {
            return List.of(ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
//            return List.of(ONE_OPTIMAL);
//            return List.of(FIVE_PERCENT_OPTIMAL);
//            return List.of(TEN_PERCENT_OPTIMAL);
        }

        return List.of(ZERO_OPTIMAL, ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        return isSuccessfulRealFunction(this, individualToFitness, hasConverged);
    }

//    @Override
//    public Optional<Integer> getCustomRunPoolSize(SelectorType selectorType) {
//        return selectorType == SUS
//                ? of(10)
//                : empty();
//    }
}