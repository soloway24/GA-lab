package lab.v2.run;

import java.util.List;

public record RunPoolStats(RunConfiguration runConfiguration,
                           List<RunStats> runPoolStats) {
}
