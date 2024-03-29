package lab.v2.run;

import lab.v2.function.FitnessFunctionV2;
import lab.v2.operator.Operator;
import lab.v2.population.PopulationType;
import lab.v2.selection.Selector;
import lab.v2.selection.SelectorType;
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

    public List<RunPoolConfiguration> createPoolConfigurations(List<FitnessFunctionV2<?, ?>> functions,
                                                               List<Selector> selectors,
                                                               List<Operator> operators,
                                                               List<Integer> populationSizes,
                                                               int runPoolSize) {
        List<RunConfiguration> runConfigurations = createAll(functions, selectors, operators, populationSizes);

        return runConfigurations.stream()
                .map(runConfig -> new RunPoolConfiguration(runConfig, runPoolSize))
                .toList();
    }

    public List<RunConfiguration> createAll(List<FitnessFunctionV2<?, ?>> functions,
                                            List<Selector> selectors,
                                            List<Operator> operators,
                                            List<Integer> populationSizes) {
        return populationSizes.stream()
                .flatMap(populationSize -> createConfiguration(functions, selectors, operators, populationSize))
                .toList();
    }

    private Stream<RunConfiguration> createConfiguration(List<FitnessFunctionV2<?, ?>> functions,
                                                         List<Selector> selectors,
                                                         List<Operator> operators,
                                                         int populationSize) {
        return functions.stream()
                .flatMap(function -> createConfiguration(function, selectors, operators, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         List<Selector> selectors,
                                                         List<Operator> operators,
                                                         int populationSize) {
        return selectors.stream()
                .flatMap(selector -> createConfiguration(function, selector, operators, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         Selector selector,
                                                         List<Operator> operators,
                                                         int populationSize) {
        return operators.stream()
                .flatMap(operatorsType -> createConfiguration(function, selector, operatorsType, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         Selector selector,
                                                         Operator operator,
                                                         int populationSize) {
        return function.getSupportedPopulationConfigurations(operator.getOperatorType())
                .stream()
                .flatMap(populationConfig -> createConfiguration(function, selector, operator,
                        populationConfig, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
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