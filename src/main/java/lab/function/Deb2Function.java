package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.operator.OperatorType;
import lab.population.PopulationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.*;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lab.encoding.Decoder.decode;
import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static lab.identifier.SuccessfulRunIdentifier.isSuccessfulRealFunction;
import static lab.operator.OperatorType.NONE;
import static lab.population.PopulationType.*;
import static lab.util.Constants.PRECISION_BASE;
import static lab.validators.EncodingSpaceValidator.validateEncodingSpace;

public class Deb2Function implements FitnessFunction<Double, Double> {

    private static final Individual OPTIMAL_STANDARD_INDIVIDUAL = new Individual("0001100100", STANDARD);
    private static final Individual OPTIMAL_GRAY_INDIVIDUAL = new Individual("0001010110", GRAY);

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_STANDARD_INDIVIDUAL,
            GRAY, OPTIMAL_GRAY_INDIVIDUAL
    );

    private final Integer chromosomeLength;
    private final Double minX;
    private final Double maxX;
    private final int argumentPrecision;

    public Deb2Function(int chromosomeLength, double minX, double maxX, int argumentPrecision) {
        validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;
    }

    @Override
    public String getName() {
        return "Deb2";
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
        return 0.0;
    }

    @Override
    public Double getMaxFitness() {
        return 1.0;
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
        return of(0.1);
    }

    @Override
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return ofNullable(ENCODING_TO_OPTIMAL.get(encoding))
                .map(Individual::new);
    }

    @Override
    public Double evaluate(Individual individual) {
        Double x = decode(individual, this);
        double exponentPower = -2 * log(2) * pow((x - 0.1) / 0.8, 2);
        return exp(exponentPower) * pow(sin(5 * PI * x), 6);
    }

    @Override
    public Optional<Double> convertToX(long decimalValue) {
        double scaledValue = decimalValue / pow(PRECISION_BASE, argumentPrecision);
        return of(minX + scaledValue);
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        if (operatorType == NONE) {
//            return List.of(ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
//            return List.of(ONE_OPTIMAL);
            return List.of(FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
//            return List.of(TEN_PERCENT_OPTIMAL);
        }

        return List.of(ZERO_OPTIMAL, ONE_OPTIMAL, FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness,
                                OperatorType operatorType,
                                boolean hasConverged) {
        return isSuccessfulRealFunction(this, individualToFitness, hasConverged);
    }

//    @Override
//    public Optional<Integer> getCustomRunPoolSize(SelectorType selectorType) {
//        return selectorType == SUS
//                ? of(10)
//                : empty();
//    }
}