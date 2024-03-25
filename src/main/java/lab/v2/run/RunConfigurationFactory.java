package lab.v2.run;

import lab.v2.function.FitnessFunctionV2;
import lab.v2.parameters.OperatorsApplicationType;
import lab.v2.population.PopulationType;
import lab.v2.selection.Selector;
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

    public List<RunConfiguration> createAll(List<FitnessFunctionV2<?, ?>> functions,
                                            List<Selector> selectors,
                                            List<OperatorsApplicationType> operatorsApplicationTypes,
                                            List<Integer> populationSizes) {
        return populationSizes.stream()
                .flatMap(populationSize -> createConfiguration(functions, selectors, operatorsApplicationTypes, populationSize))
                .toList();
    }

    private Stream<RunConfiguration> createConfiguration(List<FitnessFunctionV2<?, ?>> functions,
                                                         List<Selector> selectors,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes,
                                                         int populationSize) {
        return functions.stream()
                .flatMap(function -> createConfiguration(function, selectors, operatorsApplicationTypes, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         List<Selector> selectors,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes,
                                                         int populationSize) {
        return selectors.stream()
                .flatMap(selector -> createConfiguration(function, selector, operatorsApplicationTypes, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         Selector selector,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes,
                                                         int populationSize) {
        return operatorsApplicationTypes.stream()
                .flatMap(operatorsType -> createConfiguration(function, selector, operatorsType, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         Selector selector,
                                                         OperatorsApplicationType operatorsApplicationType,
                                                         int populationSize) {
        return function.getSupportedPopulationConfigurations(operatorsApplicationType)
                .stream()
                .flatMap(populationConfig -> createConfiguration(function, selector, operatorsApplicationType,
                        populationConfig, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         Selector selector,
                                                         OperatorsApplicationType operatorsApplicationType,
                                                         PopulationType populationType,
                                                         int populationSize) {
        return function.getSupportedEncodings()
                .stream()
                .map(encoding -> new RunConfiguration(function, selector, operatorsApplicationType, populationType,
                        encoding, populationSize));
    }
}