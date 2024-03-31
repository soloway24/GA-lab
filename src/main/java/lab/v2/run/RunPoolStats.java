package lab.v2.run;

import lombok.Builder;

import java.util.List;
import java.util.stream.IntStream;

@Builder(setterPrefix = "with")
public record RunPoolStats(RunConfiguration runConfiguration,
                           List<RunStats> runPoolStats,

                           // successful runs
                           double suc,
                           int minNI,
                           int maxNI,
                           double avgNI,
                           double sigmaNI,

                           // non-successful but converged runs
                           double nonSuc,
                           int nonMinNI,
                           int nonMaxNI,
                           double nonAvgNI,
                           double nonSigmaNI,
                           double nonMaxFFound,
                           double nonAvgFFound,
                           double nonSigmaFFound
) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RunPoolStats[\n");
        sb.append("runConfiguration=").append(runConfiguration).append("\n");
        sb.append("runStats=").append("\n");

        IntStream.range(0, runPoolStats.size())
                .forEach(i -> sb.append("Run ").append(i + 1).append(": ").append(runPoolStats.get(i)).append("\n"));

        sb.append("suc = ").append(suc).append(", ");
        sb.append("minNI = ").append(minNI).append(", ");
        sb.append("avgNI = ").append(avgNI).append(", ");
        sb.append("sigmaNI = ").append(sigmaNI).append(", ");
        sb.append("maxNI = ").append(maxNI).append(", ");

        sb.append("nonSuc = ").append(nonSuc).append(", ");
        sb.append("nonMinNI = ").append(nonMinNI).append(", ");
        sb.append("nonMaxNI = ").append(nonMaxNI).append(", ");
        sb.append("nonAvgNI = ").append(nonAvgNI).append(", ");
        sb.append("nonSigmaNI = ").append(nonSigmaNI).append(", ");
        sb.append("nonMaxFFound = ").append(nonMaxFFound).append(", ");
        sb.append("nonAvgFFound = ").append(nonAvgFFound).append(", ");
        sb.append("nonSigmaFFound = ").append(nonSigmaFFound);
        sb.append("]");

        return sb.toString();
    }
}
