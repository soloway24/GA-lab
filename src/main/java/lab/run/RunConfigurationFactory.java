package lab.run;

import lab.function.FitnessFunction;
import lab.operator.Operator;
import lab.population.PopulationType;
import lab.selection.Selector;
import lab.selection.SelectorType;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Getter
public class RunConfigurationFactory {

    private static RunConfigurationFactory instance;

    private RunConfigurationFactory() {
    }

    public static RunConfigurationFactory getInstance() {
        return ofNullable(instance)
                .orElseGet(() -> {
                    instance = new RunConfigurationFactory();
                    return instance;
                });
    }

    public List<RunPoolConfiguration> createPoolConfigurations(List<FitnessFunction<?, ?>> functions,
                                                               List<Selector> selectors,
                                                               List<Operator> operators,
                                                               List<Integer> populationSizes) {
        List<RunConfiguration> runConfigurations = createAll(functions, selectors, operators, populationSizes);

        return runConfigurations.stream()
                .map(runConfig -> new RunPoolConfiguration(runConfig, getRunPoolSize(runConfig)))
                .toList();
    }

    private int getRunPoolSize(RunConfiguration runConfiguration) {
        return runConfiguration.function().getCustomRunPoolSize(runConfiguration.selector().getSelectorType())
                .orElse(100);
    }

    public List<RunConfiguration> createAll(List<FitnessFunction<?, ?>> functions,
                                            List<Selector> selectors,
                                            List<Operator> operators,
                                            List<Integer> populationSizes) {
        return populationSizes.stream()
                .flatMap(populationSize -> createConfiguration(functions, selectors, operators, populationSize))
                .toList();
    }

    private Stream<RunConfiguration> createConfiguration(List<FitnessFunction<?, ?>> functions,
                                                         List<Selector> selectors,
                                                         List<Operator> operators,
                                                         int populationSize) {
        return functions.stream()
                .flatMap(function -> createConfiguration(function, selectors, operators, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunction<?, ?> function,
                                                         List<Selector> selectors,
                                                         List<Operator> operators,
                                                         int populationSize) {
        return selectors.stream()
                .flatMap(selector -> createConfiguration(function, selector, operators, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunction<?, ?> function,
                                                         Selector selector,
                                                         List<Operator> operators,
                                                         int populationSize) {
        return operators.stream()
                .flatMap(operatorsType -> createConfiguration(function, selector, operatorsType, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunction<?, ?> function,
                                                         Selector selector,
                                                         Operator operator,
                                                         int populationSize) {
        return function.getSupportedPopulationConfigurations(operator.getOperatorType())
                .stream()
                .flatMap(populationConfig -> createConfiguration(function, selector, operator,
                        populationConfig, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunction<?, ?> function,
                                                         Selector selector,
                                                         Operator operator,
                                                         PopulationType populationType,
                                                         int populationSize) {
        return function.getSupportedEncodings()
                .stream()
                .map(encoding -> new RunConfiguration(function, selector, operator, populationType,
                        encoding, populationSize))
                .filter(this::isSupportedSelectorType);
    }

    private boolean isSupportedSelectorType(RunConfiguration runConfiguration) {
        List<SelectorType> unsupportedSelectorTypes = runConfiguration.function()
                .getUnsupportedSelectorTypes(runConfiguration.operator().getOperatorType());
        SelectorType selectorType = runConfiguration.selector().getSelectorType();
        return !unsupportedSelectorTypes.contains(selectorType);
    }
}