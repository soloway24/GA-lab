package lab.v2.run;

import lombok.Builder;

import java.util.List;
import java.util.stream.IntStream;

@Builder(setterPrefix = "with")
public record RunPoolStats(RunConfiguration runConfiguration,
                           List<RunStats> runPoolStats,

                           // all functions
                           // successful runs
                           double suc,
                           int minNI,
                           int maxNI,
                           double avgNI,
                           double sigmaNI,


                           // all functions except FConstAll
                           // non-successful but converged runs
                           double nonSuc,
                           int nonMinNI,
                           int nonMaxNI,
                           double nonAvgNI,
                           double nonSigmaNI,
                           double nonMaxFFound,
                           double nonAvgFFound,
                           double nonSigmaFFound,

                           // successful runs
                           double minSMin,
                           int niSMin,
                           double maxSMax,
                           int niSMax,
                           double avgSMin,
                           double avgSMax,
                           double avgSAvg,
                           double minSStart,
                           double maxSStart,
                           double avgSStart,
                           double sigmaSStart,

                           // all runs
                           int niWithLoose,
                           double avgNILoose,
                           double sigmaNILoose,
                           double avgNumLoose,
                           double sigmaNumLoose,
                           double avgOptSavedNILoose,
                           double sigmaOptSavedNILoose,
                           double avgMaxOptSavedNILoose,
                           double sigmaMaxOptSavedNILoose
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
        sb.append("nonSigmaFFound = ").append(nonSigmaFFound).append(", ");

        sb.append("minSMin = ").append(minSMin).append(", ");
        sb.append("niSMin = ").append(niSMin).append(", ");
        sb.append("maxSMax = ").append(maxSMax).append(", ");
        sb.append("niSMax = ").append(niSMax).append(", ");
        sb.append("avgSMin = ").append(avgSMin).append(", ");
        sb.append("avgSMax = ").append(avgSMax).append(", ");
        sb.append("avgSAvg = ").append(avgSAvg).append(", ");
        sb.append("minSStart = ").append(minSStart).append(", ");
        sb.append("maxSStart = ").append(maxSStart).append(", ");
        sb.append("avgSStart = ").append(avgSStart).append(", ");
        sb.append("sigmaSStart = ").append(sigmaSStart).append(", ");

        sb.append("niWithLoose = ").append(niWithLoose).append(", ");
        sb.append("avgNILoose = ").append(avgNILoose).append(", ");
        sb.append("sigmaNILoose = ").append(sigmaNILoose).append(", ");
        sb.append("avgNumLoose = ").append(avgNumLoose).append(", ");
        sb.append("sigmaNumLoose = ").append(sigmaNumLoose).append(", ");
        sb.append("avgOptSavedNILoose = ").append(avgOptSavedNILoose).append(", ");
        sb.append("sigmaOptSavedNILoose = ").append(sigmaOptSavedNILoose).append(", ");
        sb.append("avgMaxOptSavedNILoose = ").append(avgMaxOptSavedNILoose).append(", ");
        sb.append("sigmaMaxOptSavedNILoose = ").append(sigmaMaxOptSavedNILoose);

        sb.append("]");

        return sb.toString();
    }
}
