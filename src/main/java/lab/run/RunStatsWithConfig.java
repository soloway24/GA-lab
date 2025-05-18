package lab.run;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record RunStatsWithConfig(
        RunConfiguration runConfiguration,
        RunStats runStats,
        int runIndex,
        int runPoolIndex
) {
}