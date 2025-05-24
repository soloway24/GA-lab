package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.identifier.SuccessfulRunIdentifier;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.selection.SelectorType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.*;
import static java.util.Optional.*;
import static lab.encoding.Decoder.decode;
import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static lab.util.Constants.PRECISION_BASE;
import static lab.validators.EncodingSpaceValidator.validateEncodingSpace;

public class TestSin2Function implements FitnessFunction<Double, Double> {

    private static final Individual OPTIMAL_STANDARD_INDIVIDUAL = new Individual("1100100001", STANDARD);
    private static final Individual OPTIMAL_GRAY_INDIVIDUAL = new Individual("1010110001", GRAY);
    private static final double EXPONENT = 2;

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_STANDARD_INDIVIDUAL,
            GRAY, OPTIMAL_GRAY_INDIVIDUAL
    );

    private final Integer chromosomeLength;
    private final Double minX;
    private final Double maxX;
    private final int argumentPrecision;

    public TestSin2Function(int chromosomeLength, double minX, double maxX, int argumentPrecision) {
        validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;
    }

    @Override
    public String getName() {
        return "TestF 2";
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
        return 0.0309908;
    } // x = 4.95

    @Override
    public Double getMaxFitness() {
        return 1.98784791269293;
    } // x = 8.01

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
        return of(8.01);
    }

    @Override
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return ofNullable(ENCODING_TO_OPTIMAL.get(encoding))
                .map(Individual::new);
    }

    @Override
    public Double evaluate(Individual individual) {
        Double x = decode(individual, this);
        return 1 + sin(x) * pow(sin(pow(x, 2) / PI), EXPONENT);
    }

    @Override
    public Optional<Double> convertToX(long decimalValue) {
        double scaledValue = decimalValue / pow(PRECISION_BASE, argumentPrecision);
        return of(minX + scaledValue);
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        if (operatorType == OperatorType.NONE) {
            return List.of(PopulationType.ONE_OPTIMAL);
//            return List.of(PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
        }

        return List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL);
//        return List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        return SuccessfulRunIdentifier.isSuccessfulRealFunction(this, individualToFitness, hasConverged);
    }

//    @Override
//    public Optional<Integer> getCustomRunPoolSize(SelectorType selectorType, OperatorType operatorType) {
//        return selectorType == SelectorType.SUS && operatorType == OperatorType.MUTATION
//                ? of(20)
//                : empty();
//    }
}