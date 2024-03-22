package lab.v2.function;

import lab.model.Individual;
import lab.parameters.Encoding;
import lab.v2.parameters.OperatorsApplicationType;
import lab.v2.population.PopulationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.pow;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lab.parameters.Encoding.GRAY;
import static lab.parameters.Encoding.STANDARD;
import static lab.utils.Constants.PRECISION_BASE;
import static lab.v2.parameters.OperatorsApplicationType.NONE;
import static lab.v2.population.PopulationType.*;
import static lab.v2.validators.EncodingSpaceValidator.validateEncodingSpace;

public class PowerFunction implements FitnessFunctionV2<Double, Double> {

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
    private final double exponent;

    public PowerFunction(int chromosomeLength, double minX, double maxX, int argumentPrecision, double exponent) {
        validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;
        this.exponent = exponent;
    }

    @Override
    public String getName() {
        return "Quadratic";
    }

    @Override
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    @Override
    public List<Encoding> getSupportedEncodings() {
        return List.of(STANDARD, GRAY);
    }

    @Override
    public Double getMinFitness() {
        return 0.0;
    }

    @Override
    public Double getMaxFitness() {
        return pow(maxX, exponent);
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
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return ofNullable(ENCODING_TO_OPTIMAL.get(encoding));

    }

    @Override
    public Double evaluate(Double x) {
        return pow(x, exponent);
    }

    @Override
    public Optional<Double> convertToX(long decimalValue) {
        double scaledValue = decimalValue / pow(PRECISION_BASE, argumentPrecision);
        return of(minX + scaledValue);
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorsApplicationType operatorsApplicationType) {
        if (operatorsApplicationType == NONE) {
            return List.of(ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
        }

        return List.of(ZERO_OPTIMAL, ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
    }
}