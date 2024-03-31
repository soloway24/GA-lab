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

                           double minRRMin,
                           int niMinRRMin,
                           double maxRRMax,
                           int niMaxRRMax,
                           double avgRRMin,
                           double avgRRMax,
                           double avgRRAvg,
                           double minTetaMin,
                           int niMinTetaMin,
                           double maxTetaMax,
                           int niMaxTetaMax,
                           double avgTetaMin,
                           double avgTetaMax,
                           double avgTetaAvg,

                           double sigmaRRMin,
                           double sigmaRRMax,
                           double sigmaRRAvg,
                           double sigmaTetaMin,
                           double sigmaTetaMax,
                           double sigmaTetaAvg,

                           double minRRStart,
                           double maxRRStart,
                           double avgRRStart,
                           double sigmaRRStart,

                           double minTetaStart,
                           double maxTetaStart,
                           double avgTetaStart,
                           double sigmaTetaStart,

                           double avgRRFin,
                           double sigmaRRFin,
                           double avgTetaFin,
                           double sigmaTetaFin,

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

        // all functions
        // successful runs
        sb.append("suc = ").append(suc).append(", ");
        sb.append("minNI = ").append(minNI).append(", ");
        sb.append("avgNI = ").append(avgNI).append(", ");
        sb.append("sigmaNI = ").append(sigmaNI).append(", ");
        sb.append("maxNI = ").append(maxNI).append(", ");

        sb.append("minRRMin = ").append(minRRMin).append(", ");
        sb.append("niMinRRMin = ").append(niMinRRMin).append(", ");
        sb.append("maxRRMax = ").append(maxRRMax).append(", ");
        sb.append("niMaxRRMax = ").append(niMaxRRMax).append(", ");
        sb.append("avgRRMin = ").append(avgRRMin).append(", ");
        sb.append("avgRRMax = ").append(avgRRMax).append(", ");
        sb.append("avgRRAvg = ").append(avgRRAvg).append(", ");
        sb.append("minTetaMin = ").append(minTetaMin).append(", ");
        sb.append("niMinTetaMin = ").append(niMinTetaMin).append(", ");
        sb.append("maxTetaMax = ").append(maxTetaMax).append(", ");
        sb.append("niMaxTetaMax = ").append(niMaxTetaMax).append(", ");
        sb.append("avgTetaMin = ").append(avgTetaMin).append(", ");
        sb.append("avgTetaMax = ").append(avgTetaMax).append(", ");
        sb.append("avgTetaAvg = ").append(avgTetaAvg).append(", ");

        sb.append("sigmaRRMin = ").append(sigmaRRMin).append(", ");
        sb.append("sigmaRRMax = ").append(sigmaRRMax).append(", ");
        sb.append("sigmaRRAvg = ").append(sigmaRRAvg).append(", ");
        sb.append("sigmaTetaMin = ").append(sigmaTetaMin).append(", ");
        sb.append("sigmaTetaMax = ").append(sigmaTetaMax).append(", ");
        sb.append("sigmaTetaAvg = ").append(sigmaTetaAvg).append(", ");

        sb.append("minRRStart = ").append(minRRStart).append(", ");
        sb.append("maxRRStart = ").append(maxRRStart).append(", ");
        sb.append("avgRRStart = ").append(avgRRStart).append(", ");
        sb.append("sigmaRRStart = ").append(sigmaRRStart).append(", ");
        sb.append("minTetaStart = ").append(minTetaStart).append(", ");
        sb.append("maxTetaStart = ").append(maxTetaStart).append(", ");
        sb.append("avgTetaStart = ").append(avgTetaStart).append(", ");
        sb.append("sigmaTetaStart = ").append(sigmaTetaStart).append(", ");
        sb.append("avgRRFin = ").append(avgRRFin).append(", ");
        sb.append("sigmaRRFin = ").append(sigmaRRFin).append(", ");
        sb.append("avgTetaFin = ").append(avgTetaFin).append(", ");
        sb.append("sigmaTetaFin = ").append(sigmaTetaFin).append(", ");


        // all functions except FConstAll
        // non-successful but converged runs
        sb.append("nonSuc = ").append(nonSuc).append(", ");
        sb.append("nonMinNI = ").append(nonMinNI).append(", ");
        sb.append("nonMaxNI = ").append(nonMaxNI).append(", ");
        sb.append("nonAvgNI = ").append(nonAvgNI).append(", ");
        sb.append("nonSigmaNI = ").append(nonSigmaNI).append(", ");
        sb.append("nonMaxFFound = ").append(nonMaxFFound).append(", ");
        sb.append("nonAvgFFound = ").append(nonAvgFFound).append(", ");
        sb.append("nonSigmaFFound = ").append(nonSigmaFFound).append(", ");

        // successful runs
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

        // all runs
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
