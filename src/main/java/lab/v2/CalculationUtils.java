package lab.v2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CalculationUtils {

    public static <T extends Number> double getAverage(Collection<T> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElseThrow(() -> new IllegalStateException("Cannot calculate average of the values: " + values + " !"));
    }

    public static <T extends Number> double getMedian(Collection<T> values) {
        List<T> sortedValues = values.stream()
                .sorted()
                .toList();
        int size = sortedValues.size();
        if (size % 2 == 1) {
            return sortedValues.get(size / 2).doubleValue();
        }
        return getAverage(sortedValues.get(size / 2 - 1), sortedValues.get(size / 2));
    }

    private static <T extends Number> double getAverage(T first, T second) {
        return (first.doubleValue() + second.doubleValue()) / 2.0;
    }

    public static List<Double> getDoubleValues(Map<Individual, ? extends Number> individualToValue) {
        return individualToValue.values().stream()
                .map(Number::doubleValue)
                .toList();
    }

    public static double getValueSum(List<Double> values) {
        return values.stream()
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException("Provided values list is empty! Values = "
                        + values + " ."));
    }
}
