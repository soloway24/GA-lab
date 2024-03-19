package lab.v2.function;

import lab.model.Individual;
import lab.parameters.Encoding;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static lab.model.Individual.ALL_100_ZEROS_INDIVIDUAL;
import static lab.parameters.Encoding.STANDARD;

public class FConstAllFunction implements FitnessFunctionV2<Number, Integer> {

    public static final FConstAllFunction F_CONST_ALL_FUNCTION = new FConstAllFunction();

    private FConstAllFunction() {
    }

    @Override
    public String getName() {
        return "FconstALL";
    }

    @Override
    public int getChromosomeLength() {
        return 100;
    }

    @Override
    public List<Encoding> getEncodings() {
        return List.of(STANDARD);
    }

    @Override
    public Integer getMinFitness() {
        return 100;
    }

    @Override
    public Integer getMaxFitness() {
        return 100;
    }

    @Override
    public Optional<Number> getMinX() {
        return of(0);
    }

    @Override
    public Optional<Number> getMaxX() {
        return empty();
    }

    @Override
    public Individual getOptimalIndividual(Encoding encoding) {
        return ALL_100_ZEROS_INDIVIDUAL;
    }

    @Override
    public Integer evaluate(Number x) {
        return 100;
    }

    @Override
    public Optional<Number> convertToX(long decimalValue) {
        return empty();
    }
}