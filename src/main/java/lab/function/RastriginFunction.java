package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.identifier.SuccessfulRunIdentifier;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.validators.EncodingSpaceValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.*;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lab.encoding.Decoder.decode;
import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static lab.util.Constants.PRECISION_BASE;

public class RastriginFunction implements FitnessFunction<Double, Double> {

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
        EncodingSpaceValidator.validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
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
        return List.of(STANDARD);
//        return List.of(GRAY);
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
        Double x = decode(individual, this);
        return abs(10 * cos(2 * PI * a) - pow(a, 2)) + 10 * cos(2 * PI * x) - pow(x, 2);
    }

    @Override
    public Optional<Double> convertToX(long decimalValue) {
        double scaledValue = decimalValue / pow(PRECISION_BASE, argumentPrecision);
        return of(minX + scaledValue);
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        if (operatorType == OperatorType.NONE) {
            return List.of(PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
//            return List.of(ONE_OPTIMAL);
//            return List.of(FIVE_PERCENT_OPTIMAL);
//            return List.of(TEN_PERCENT_OPTIMAL);
        }

//        return List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
//        return List.of(PopulationType.ZERO_OPTIMAL);
//        return List.of(PopulationType.ONE_OPTIMAL);
        return List.of(PopulationType.FIVE_PERCENT_OPTIMAL);
//        return List.of(PopulationType.TEN_PERCENT_OPTIMAL);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        return SuccessfulRunIdentifier.isSuccessfulRealFunction(this, individualToFitness, hasConverged);
    }

//    @Override
//    public Optional<Integer> getCustomRunPoolSize(SelectorType selectorType) {
//        return selectorType == SUS
//                ? of(10)
//                : empty();
//    }
}