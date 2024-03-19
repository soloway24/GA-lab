package lab.v2.parameters;

import lab.v2.function.FitnessFunctionV2;
import lab.v2.population.PopulationConfiguration;
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
                .flatMap(populationSize -> createParameters(populationSize, functions, operatorsApplicationTypes))
                .toList();
    }

    private Stream<RunConfiguration> createParameters(int populationSize,
                                                      List<FitnessFunctionV2<?, ?>> functions,
                                                      List<OperatorsApplicationType> operatorsApplicationTypes) {
        return functions.stream()
                .flatMap(function -> createParameters(populationSize, function, operatorsApplicationTypes));
    }

    private Stream<RunConfiguration> createParameters(int populationSize,
                                                      FitnessFunctionV2<?, ?> function,
                                                      List<OperatorsApplicationType> operatorsApplicationTypes) {
        return operatorsApplicationTypes.stream()
                .flatMap(operatorsType -> createParameters(populationSize, function, operatorsType));
    }

    private Stream<RunConfiguration> createParameters(int populationSize,
                                                      FitnessFunctionV2<?, ?> function,
                                                      OperatorsApplicationType operatorsApplicationType) {
        return function.getSupportedPopulationConfigurations(operatorsApplicationType)
                .stream()
                .flatMap(populationConfig -> createParameters(populationSize, function, operatorsApplicationType, populationConfig));
    }

    private Stream<RunConfiguration> createParameters(int populationSize,
                                                      FitnessFunctionV2<?, ?> function,
                                                      OperatorsApplicationType operatorsApplicationType,
                                                      PopulationConfiguration populationConfig) {
        return function.getSupportedEncodings()
                .stream()
                .map(encoding -> new RunConfiguration(populationSize, function, operatorsApplicationType, populationConfig, encoding));
    }
}