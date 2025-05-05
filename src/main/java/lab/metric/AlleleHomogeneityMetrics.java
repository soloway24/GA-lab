package lab.metric;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record AlleleHomogeneityMetrics(
        int minNiAlH,
        int maxNiAlH,
        double avgNiAlH,
        double sigmaNiAlH,

        double minFAlH,
        double maxFAlH,
        double avgFAlH,
        double sigmaFAlH
) {
}