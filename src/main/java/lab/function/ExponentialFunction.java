package lab.function;

import lab.model.Individual;
import lab.parameters.Encoding;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;
import static java.util.Optional.of;
import static lab.model.Individual.ALL_100_ZEROS_INDIVIDUAL;
import static lab.parameters.Encoding.GRAY;
import static lab.parameters.Encoding.STANDARD;
import static lab.utils.EncodingSpaceValidator.validateEncodingSpace;

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
    public List<Encoding> getEncodings() {
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

}