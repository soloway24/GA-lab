package lab.metric;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record NiOfMetrics(int minNi25of,
                          int maxNi25of,
                          double avgNi25of,
                          double sigmaNi25of,

                          int minNi50of,
                          int maxNi50of,
                          double avgNi50of,
                          double sigmaNi50of,

                          int minNi75of,
                          int maxNi75of,
                          double avgNi75of,
                          double sigmaNi75of,

                          int minNi90of,
                          int maxNi90of,
                          double avgNi90of,
                          double sigmaNi90of) {
}