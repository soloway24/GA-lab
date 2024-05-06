package lab.function;

import lab.Individual;
import lab.encoding.Decoder;
import lab.identifier.SuccessfulRunIdentifier;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.encoding.Encoding;
import lab.validators.EncodingSpaceValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static lab.util.Constants.PRECISION_BASE;

public class ExponentialFunction implements FitnessFunction<Double, Double> {

    private static final Individual OPTIMAL_STANDARD_INDIVIDUAL = new Individual("1111111111", STANDARD);
    private static final Individual OPTIMAL_GRAY_INDIVIDUAL = new Individual("1000000000", GRAY);

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_STANDARD_INDIVIDUAL,
            GRAY, OPTIMAL_GRAY_INDIVIDUAL
    );

    private final Integer chromosomeLength;
    private final Double minX;
    private final Double maxX;
    private final int argumentPrecision;
    private final double factor;

    public ExponentialFunction(int chromosomeLength, double minX, double maxX, int argumentPrecision, double factor) {
        EncodingSpaceValidator.validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;
        this.factor = factor;
    }

    @Override
    public String getName() {
        return "e^" + factor + "x";
    }

    @Override
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    @Override
    public List<Encoding> getSupportedEncodings() {
        return List.of(STANDARD, GRAY);
//        return List.of(STANDARD);
//        return List.of(GRAY);
    }

    @Override
    public Double getMinFitness() {
        return 1.0;
    }

    @Override
    public Double getMaxFitness() {
        return exp(factor * maxX);
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
        return of(maxX);
    }

    @Override
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return ofNullable(ENCODING_TO_OPTIMAL.get(encoding))
                .map(Individual::new);
    }

    @Override
    public Double evaluate(Individual individual) {
        Double x = Decoder.decodeV2(individual, this);
        return exp(factor * x);
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

        return List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        return SuccessfulRunIdentifier.isSuccessfulRealFunction(this, individualToFitness, hasConverged);
    }
}