package lab.function.f;

import lab.Individual;
import lab.encoding.Encoding;
import lab.function.FitnessFunction;
import lab.identifier.SuccessfulRunIdentifier;
import lab.operator.OperatorType;
import lab.population.PopulationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.pow;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lab.encoding.Decoder.decodeMultipleArguments;
import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static lab.util.Constants.PRECISION_BASE;
import static lab.validators.EncodingSpaceValidator.validateEncodingSpace;

public class F1Function implements FitnessFunction<Double, Double> {

    private static final String OPTIMAL_STANDARD_CODE = "1000000000";
    private static final String OPTIMAL_GRAY_CODE = "1100000000";

    private final Map<Encoding, String> encodingToOptimalCode;

    private final int arity;
    private final Integer chromosomeLength;
    private final Double minX;
    private final Double maxX;
    private final int argumentPrecision;

    public F1Function(int arity, int chromosomeLength, double minX, double maxX, int argumentPrecision) {
        validateEncodingSpace(chromosomeLength, minX, maxX, argumentPrecision);
        this.arity = arity;
        this.chromosomeLength = chromosomeLength;
        this.minX = minX;
        this.maxX = maxX;
        this.argumentPrecision = argumentPrecision;

        this.encodingToOptimalCode = Map.of(
                STANDARD, requireNonNull(buildOptimalBinaryCode(STANDARD)),
                GRAY, requireNonNull(buildOptimalBinaryCode(GRAY))
        );
    }

    @Override
    public String getName() {
        return "F1 " + arity;
    }

    @Override
    public int getArity() {
        return arity;
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
        return 0.0;
    }

    @Override
    public Double getMaxFitness() {
        return pow(5.12, 2) * arity;
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
        return ofNullable(encodingToOptimalCode.get(encoding))
                .map(code -> new Individual(code, encoding));
    }

    @Override
    public Double evaluate(Individual individual) {
        List<Double> xs = decodeMultipleArguments(individual, this);
        double sumX2 = xs.stream()
                .map(x -> pow(x, 2))
                .reduce(0.0, Double::sum);

        return pow(5.12, 2) * arity - sumX2;
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

    private String buildOptimalBinaryCode(Encoding encoding) {
        if (encoding == STANDARD) {
            return buildOptimalBinaryCode(OPTIMAL_STANDARD_CODE);
        } else if (encoding == GRAY) {
            return buildOptimalBinaryCode(OPTIMAL_GRAY_CODE);
        }
        return null;
    }

    private String buildOptimalBinaryCode(String code) {
        return code.repeat(arity);
    }
}