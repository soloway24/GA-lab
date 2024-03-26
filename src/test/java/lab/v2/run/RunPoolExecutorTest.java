package lab.v2.run;

import lab.parameters.Encoding;
import lab.v2.ConvergenceIdentifier;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.encoding.DecoderV2;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.function.PowerFunction;
import lab.v2.operator.NoneOperator;
import lab.v2.operator.Operator;
import lab.v2.population.*;
import lab.v2.selection.RwsSelector;
import lab.v2.selection.Selector;
import org.junit.jupiter.api.Test;

import static lab.parameters.Encoding.STANDARD;
import static lab.v2.population.PopulationType.TEN_PERCENT_OPTIMAL;

class RunPoolExecutorTest {

    private static final int POPULATION_SIZE = 100;
    private final DecoderV2 decoder = DecoderV2.getInstance();
    private final ConvergenceIdentifier convergenceIdentifier = new ConvergenceIdentifier();
    private final RunPoolExecutor runPoolExecutor = new RunPoolExecutor(decoder, convergenceIdentifier);
    private final FitnessFunctionV2<Double, Double> quadraticFunction = new PowerFunction(
            10, 0.0, 10.23, 2, 2
    );
    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final Selector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final Operator noneOperator = new NoneOperator();
    private final PopulationType populationType = TEN_PERCENT_OPTIMAL;
    private final Encoding encoding = STANDARD;
    private final RunConfiguration runConfiguration = new RunConfiguration(quadraticFunction, rwsSelector,
            noneOperator, populationType, encoding, POPULATION_SIZE);
    private final PopulationTypeValidator populationTypeValidator = PopulationTypeValidator.getInstance();
    private final PopulationInitializer populationInitializer = new PopulationInitializer(populationTypeValidator);
    private final PopulationConfiguration populationConfiguration = new PopulationConfiguration(quadraticFunction,
            populationType, encoding, POPULATION_SIZE);
    private final Population population = populationInitializer.initializePopulation(populationConfiguration);
    private final Run run = new Run(runConfiguration, population);

    @Test
    public void whenExecuteRunTheSuccess() {
        System.out.println("Population = " + population);
        System.out.println(runPoolExecutor.executeRun(run));
    }
}