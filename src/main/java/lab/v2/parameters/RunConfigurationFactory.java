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

    public List<RunConfiguration> createAll(List<Integer> populationSizes,
                                            List<FitnessFunctionV2<?, ?>> functions,
                                            List<OperatorsApplicationType> operatorsApplicationTypes) {
        return populationSizes.stream()
                .flatMap(populationSize -> createConfiguration(populationSize, functions, operatorsApplicationTypes))
                .toList();
    }

    private Stream<RunConfiguration> createConfiguration(int populationSize,
                                                         List<FitnessFunctionV2<?, ?>> functions,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes) {
        return functions.stream()
                .flatMap(function -> createConfiguration(populationSize, function, operatorsApplicationTypes));
    }

    private Stream<RunConfiguration> createConfiguration(int populationSize,
                                                         FitnessFunctionV2<?, ?> function,
                                                         List<OperatorsApplicationType> operatorsApplicationTypes) {
        return operatorsApplicationTypes.stream()
                .flatMap(operatorsType -> createConfiguration(populationSize, function, operatorsType));
    }

    private Stream<RunConfiguration> createConfiguration(int populationSize,
                                                         FitnessFunctionV2<?, ?> function,
                                                         OperatorsApplicationType operatorsApplicationType) {
        return function.getSupportedPopulationConfigurations(operatorsApplicationType)
                .stream()
                .flatMap(populationConfig -> createConfiguration(populationSize, function, operatorsApplicationType, populationConfig));
    }

    private Stream<RunConfiguration> createConfiguration(int populationSize,
                                                         FitnessFunctionV2<?, ?> function,
                                                         OperatorsApplicationType operatorsApplicationType,
                                                         PopulationType populationConfig) {
        return function.getSupportedEncodings()
                .stream()
                .map(encoding -> new RunConfiguration(populationSize, function, operatorsApplicationType, populationConfig, encoding));
    }
}