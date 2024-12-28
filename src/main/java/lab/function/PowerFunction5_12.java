package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.selection.SelectorType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.pow;
import static java.util.Optional.*;
import static lab.encoding.Decoder.decode;
import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static lab.identifier.SuccessfulRunIdentifier.isSuccessfulRealFunction;
import static lab.operator.OperatorType.NONE;
import static lab.population.PopulationType.*;
import static lab.util.Constants.PRECISION_BASE;
import static lab.validators.EncodingSpaceValidator.validateEncodingSpace;

public class PowerFunction5_12 implements FitnessFunction<Double, Double> {

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
    private final double b;
    private final double bExponent;
    private final double xExponent;

    //  b ^ bExponent - x ^ xExponent;  b = 5.12, bExponent = 2, xExponent = 2
    public PowerFunction5_12(int chromosomeLength, double minX, double maxX, int argumentPrecision,
                             double b, double bExponent, double xExponent) {
        validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;
        this.b = b;
        this.bExponent = bExponent;
        this.xExponent = xExponent;
    }

    @Override
    public String getName() {
        return "(" + b + ")^" + bExponent + " - x^" + xExponent;
    }

    @Override
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    @Override
    public List<Encoding> getSupportedEncodings() {
        return List.of(
                STANDARD
//                ,
//                GRAY
        );
    }

    @Override
    public Double getMinFitness() {
        return 0.0;
    }

    @Override
    public Double getMaxFitness() {
        return pow(b, bExponent);
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
        return pow(b, bExponent) - pow(x, xExponent);
    }

    @Override
    public Optional<Double> convertToX(long decimalValue) {
        double scaledValue = decimalValue / pow(PRECISION_BASE, argumentPrecision);
        return of(minX + scaledValue);
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        if (operatorType == NONE) {
            return List.of(PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
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

    @Override
    public Optional<Integer> getCustomRunPoolSize(SelectorType selectorType) {
        return selectorType == SelectorType.SUS
                ? of(10)
                : empty();
    }
}