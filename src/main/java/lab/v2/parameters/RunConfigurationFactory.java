package lab.v2.parameters;

import lab.v2.function.FitnessFunctionV2;
import lab.v2.population.PopulationType;
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
                .orElse(new RunConfigurationFactory());
    }

    public List<RunConfiguration> createAll(List<FitnessFunctionV2<?, ?>> functions,
                                            List<OperatorsApplicationType> operatorsApplicationTypes,
                                            List<Integer> populationSizes) {
        return populationSizes.stream()
                .flatMap(populationSize -> createConfiguration(functions, operatorsApplicationTypes, populationSize))
                .toList();
    }

    private Stream<RunConfiguration> createConfiguration(List<FitnessFunctionV2<?, ?>> functions,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes,
                                                         int populationSize) {
        return functions.stream()
                .flatMap(function -> createConfiguration(function, operatorsApplicationTypes, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes,
                                                         int populationSize) {
        return operatorsApplicationTypes.stream()
                .flatMap(operatorsType -> createConfiguration(function, operatorsType, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         OperatorsApplicationType operatorsApplicationType,
                                                         int populationSize) {
        return function.getSupportedPopulationConfigurations(operatorsApplicationType)
                .stream()
                .flatMap(populationConfig -> createConfiguration(function, operatorsApplicationType, populationConfig, populationSize));
    }

    private Stream<RunConfiguration> createConfiguration(FitnessFunctionV2<?, ?> function,
                                                         OperatorsApplicationType operatorsApplicationType,
                                                         PopulationType populationType,
                                                         int populationSize) {
        return function.getSupportedEncodings()
                .stream()
                .map(encoding -> new RunConfiguration(function, operatorsApplicationType, populationType, encoding, populationSize));
    }
}