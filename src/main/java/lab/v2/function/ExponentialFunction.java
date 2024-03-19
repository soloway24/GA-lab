package lab.v2.function;

import lab.model.Individual;
import lab.parameters.Encoding;
import lab.v2.parameters.OperatorsApplicationType;
import lab.v2.population.PopulationConfiguration;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;
import static java.util.Optional.of;
import static lab.model.Individual.ALL_100_ZEROS_INDIVIDUAL;
import static lab.parameters.Encoding.GRAY;
import static lab.parameters.Encoding.STANDARD;
import static lab.utils.Constants.PRECISION_BASE;
import static lab.v2.parameters.OperatorsApplicationType.NONE;
import static lab.v2.population.PopulationConfiguration.*;
import static lab.v2.validators.EncodingSpaceValidator.validateEncodingSpace;

public class ExponentialFunction implements FitnessFunctionV2<Double, Double> {

    private final Integer chromosomeLength;
    private final Double minX;
    private final Double maxX;
    private final int argumentPrecision;
    private final double exponent;

    public ExponentialFunction(int chromosomeLength, double minX, double maxX, int argumentPrecision, double exponent) {
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
    public Individual getOptimalIndividual(Encoding encoding) {
        return ALL_100_ZEROS_INDIVIDUAL;
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
    public List<PopulationConfiguration> getSupportedPopulationConfigurations(OperatorsApplicationType operatorsApplicationType) {
        if (operatorsApplicationType == NONE) {
            return List.of(ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
        }

        return List.of(ZERO_OPTIMAL, ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
    }
}