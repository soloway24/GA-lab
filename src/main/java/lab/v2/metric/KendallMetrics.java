package lab.v2.metric;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record KendallMetrics(double minKendallMin,
                             int niMinKendallMin,
                             double maxKendallMax,
                             int niMaxKendallMax,
                             double avgKendallMin,
                             double avgKendallMax,
                             double avgKendallAvg,
                             double sigmaKendallMin,
                             double sigmaKendallMax,
                             double sigmaKendallAvg,
                             double minKendallStart,
                             double maxKendallStart,
                             double avgKendallStart,
                             double sigmaKendallStart) {
}
