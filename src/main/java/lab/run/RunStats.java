package lab.run;

import lab.Individual;
import lab.export.Homogeneity;
import lab.metric.IndividualMetrics;
import lab.metric.SingleHomogeneityMetrics;
import lab.population.PopulationSnapshot;
import lab.population.PopulationTimingType;
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

                       double iStart,
                       double iMin,
                       int niImin,
                       double iMax,
                       int niImax,
                       double iAvg,

                       double grStart,
                       double grEarly,
                       double grAvg,
                       double grLate,
                       int niGrLate,

                       double prStart,
                       double prMin,
                       int niPrMin,
                       double prMax,
                       int niPrMax,
                       double prAvg,

                       int niFHM,
                       int niFHSM,

                       int ni25of,
                       int ni50of,
                       int ni75of,
                       int ni90of,

                       double fishStart,
                       double fishMin,
                       int niFishMin,
                       double fishMax,
                       int niFishMax,
                       double fishAvg,

                       double kendallStart,
                       double kendallMin,
                       int niKendallMin,
                       double kendallMax,
                       int niKendallMax,
                       double kendallAvg,

                       List<Double> avgFs,
                       List<Double> maxFs,
                       List<Double> sigmaFs,
                       List<Double> optimalRatios,
                       List<Double> bestRatios,
                       List<Double> ss,
                       List<Double> rrs,
                       List<Double> tetas,
                       List<Integer> uniques,
                       List<Double> is,
                       List<Double> prs,
                       List<Double> grs,
                       List<Double> fishes,
                       List<Double> kendalls,
                       Map<Integer, List<IndividualMetrics>> generationToIndMetrics,
                       Map<Homogeneity, List<IndividualMetrics>> homogeneityToIndMetrics,
                       Map<PopulationTimingType, PopulationSnapshot> timingTypeToPopulationSnapshot,
                       SingleHomogeneityMetrics singleHomogeneityMetrics
) {
}
