package lab.v2.run;

import lab.v2.Individual;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder(setterPrefix = "with")
public record RunStats(Map<Individual, ? extends Number> finalPopulation,

                       // metrics for all functions
                       int ni,
                       boolean hasConverged,
                       boolean isSuc,

                       double rrStart,
                       double rrFin,
                       double rrAvg,
                       double rrMin,
                       int niRrMin,
                       double rrMax,
                       int niRrMax,

                       double tetaStart,
                       double tetaFin,
                       double tetaAvg,
                       double tetaMin,
                       int niTetaMin,
                       double tetaMax,
                       int niTetaMax,

                       int uniqueXStart,
                       int uniqueXFin,


                       // metrics for all functions except FConstAll
                       double fFound,
                       double fAvg,

                       int niLoose,
                       int numLoose,
                       int optSavedNILoose,
                       int maxOptSavedNILoose,

                       double sStart,
                       double sFin,
                       double sAvg,
                       double sMin,
                       int niSMin,
                       double sMax,
                       int niSMax,

                       List<Double> avgFs,
                       List<Double> maxFs,
                       List<Double> sigmaFs,
                       List<Double> optimalRatios,
                       List<Double> bestRatios,
                       List<Double> ss,
                       List<Double> rrs,
                       List<Double> tetas,
                       List<Integer> uniques
                       ) {
}
