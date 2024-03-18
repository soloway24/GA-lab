package lab.model;

import lab.parameters.ContestType;
import lab.parameters.Encoding;
import lab.function.FitnessFunction;
import lab.parameters.GeneticOperatorsApplication;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RunPool {

    private final List<Run> runs;

    public int Min_NI = Integer.MAX_VALUE, Max_NI = Integer.MIN_VALUE;

    public int nSuccessfulRuns = 0, NI_I_min = Integer.MAX_VALUE, NI_I_max = Integer.MIN_VALUE;
    public double Avg_NI = 0, Suc = 0, Sigma_NI;
    public double Min_I_min = Float.MAX_VALUE, Max_I_max = Float.MIN_VALUE, Avg_I_min = 0, Avg_I_max = Float.MIN_VALUE, Avg_I_avg = 0;
    public double Sigma_I_max = Float.MIN_VALUE, Sigma_I_min = Float.MAX_VALUE, Sigma_I_avg = 0;

    public double Min_GR_early = Float.MAX_VALUE, Max_GR_early = Float.MIN_VALUE, Avg_GR_early = 0;
    public double Min_GR_late = Float.MAX_VALUE, Max_GR_late = Float.MIN_VALUE, Avg_GR_late = 0;
    public double Min_GR_avg = Float.MAX_VALUE, Max_GR_avg = Float.MIN_VALUE, Avg_GR_avg = 0;

    public double Min_RR_min = Float.MAX_VALUE, Max_RR_max = Float.MIN_VALUE, Avg_RR_min = Float.MAX_VALUE, Avg_RR_max = Float.MIN_VALUE, Avg_RR_avg;
    public long NI_RR_min = Long.MAX_VALUE, NI_RR_max = Long.MIN_VALUE;
    public double Sigma_RR_max = Float.MIN_VALUE, Sigma_RR_min = Float.MAX_VALUE, Sigma_RR_avg;

    public double Min_Teta_min = Float.MAX_VALUE, Max_Teta_max = Float.MIN_VALUE, Avg_Teta_min = Float.MAX_VALUE, Avg_Teta_max = Float.MIN_VALUE, Avg_Teta_avg;
    public long NI_Teta_min = Long.MAX_VALUE, NI_Teta_max = Long.MIN_VALUE;
    public double Sigma_Teta_max = Float.MAX_VALUE, Sigma_Teta_min = Float.MIN_VALUE, Sigma_Teta_avg;

    public double Min_s_min = Float.MAX_VALUE, Max_s_max = Float.MIN_VALUE, Avg_s_min = Float.MAX_VALUE, Avg_s_max = Float.MIN_VALUE, Avg_s_avg;
    public long NI_s_min = Long.MAX_VALUE, NI_s_max = Long.MIN_VALUE;


    public RunPool(int nRuns, Encoding encoding, ContestType contestType, FitnessFunction function, GeneticOperatorsApplication geneticOperatorsApplication, List<Individual> initialPopulation) {
        runs = new ArrayList<>();
        for (int i = 0; i < nRuns; i++) {
            List<Individual> list = new ArrayList<>();
            for (Individual individual : initialPopulation) {
                Individual clone = individual.clone();
                list.add(clone);
            }
            runs.add(new Run(encoding, contestType, function, geneticOperatorsApplication, list));
        }
    }

    public void runProcesses() {
        for (Run run : runs) {
            run.run();
        }
    }

    public void calculateIterationsStatistics() {
        List<Double> ni = new ArrayList<>();

        List<Double> imax = new ArrayList<>();
        List<Double> imin = new ArrayList<>();
        List<Double> iavg = new ArrayList<>();

        List<Double> rr_min = new ArrayList<>();
        List<Double> rr_avg = new ArrayList<>();
        List<Double> rr_max = new ArrayList<>();

        List<Double> teta_min = new ArrayList<>();
        List<Double> teta_avg = new ArrayList<>();
        List<Double> teta_max = new ArrayList<>();

        double totalIMin = 0, totalIMax = 0, totalIAvg = 0;
        double totalGREarly = 0, totalGRLate = 0, totalGRAvg = 0;
        double totalRRMin = 0, totalRRMax = 0, totalRRAvg = 0;
        double totalTetaMin = 0, totalTetaMax = 0, totalTetaAvg = 0;
        double totalSMin = 0, totalSMax = 0, totalSAvg = 0;

        for (Run run : runs) {

            if (!run.Suc()) {
                continue;
            }

            if (run.nIterationBeforeStop > Max_NI) {
                Max_NI = run.nIterationBeforeStop;
            }

            if (run.nIterationBeforeStop < Min_NI) {
                Min_NI = run.nIterationBeforeStop;
            }

            ni.add((double) run.nIterationBeforeStop);
            Avg_NI += run.nIterationBeforeStop;

            if (run.I_max > Max_I_max) {
                Max_I_max = run.I_max;
                NI_I_max = run.NI_I_max;
            }

            if (run.I_min < Min_I_min) {
                Min_I_min = run.I_min;
                NI_I_min = run.NI_I_min;
            }

            if (run.GR_early > Max_GR_early) {
                Max_GR_early = run.GR_early;
            }

            if (run.GR_early < Min_GR_early) {
                Min_GR_early = run.GR_early;
            }

            if (run.GR_late > Max_GR_late) {
                Max_GR_late = run.GR_late;
            }

            if (run.GR_late < Min_GR_late) {
                Min_GR_late = run.GR_late;
            }

            if (run.GR_avg > Max_GR_avg) {
                Max_GR_avg = run.GR_avg;
            }

            if (run.GR_avg < Min_GR_avg) {
                Min_GR_avg = run.GR_avg;
            }

            if (run.RR_min < Min_RR_min) {
                Min_RR_min = run.RR_min;
                NI_RR_min = run.NI_RR_min;
            }

            if (run.RR_max > Max_RR_max) {
                Max_RR_max = run.RR_max;
                NI_RR_max = run.NI_RR_max;
            }

            if (run.Teta_min < Min_Teta_min) {
                Min_Teta_min = run.Teta_min;
                NI_Teta_min = run.NI_Teta_min;
            }

            if (run.Teta_max > Max_Teta_max) {
                Max_Teta_max = run.Teta_max;
                NI_Teta_max = run.NI_Teta_max;
            }

            if (run.s_min < Min_s_min) {
                Min_s_min = run.s_min;
                NI_s_min = run.NI_s_min;
            }

            if (run.s_max > Max_s_max) {
                Max_s_max = run.s_max;
                NI_s_max = run.NI_s_max;
            }

            totalIMin += run.I_min;
            imin.add(run.I_min);

            totalIMax += run.I_max;
            imax.add(run.I_max);

            totalIAvg += run.I_avg;
            iavg.add(run.I_avg);

            totalGREarly += run.GR_early;
            totalGRLate += run.GR_late;
            totalGRAvg += run.GR_early;

            totalRRAvg += run.RR_avg;
            totalRRMin += run.RR_min;
            totalRRMax += run.RR_max;

            totalTetaAvg += run.Teta_avg;
            totalTetaMin += run.Teta_min;
            totalTetaMax += run.Teta_max;

            totalSAvg += run.s_avg;
            totalSMin += run.s_min;
            totalSMax += run.s_max;

            rr_min.add(run.RR_min);
            rr_max.add(run.RR_max);
            rr_avg.add(run.RR_avg);

            teta_min.add(run.Teta_min);
            teta_max.add(run.Teta_max);
            teta_avg.add(run.Teta_avg);

            nSuccessfulRuns++;
        }
        Suc = (float) nSuccessfulRuns / runs.size();
        Avg_NI /= nSuccessfulRuns;
        Sigma_NI = countSigma(ni, Avg_NI);

        Avg_I_min = totalIMin / nSuccessfulRuns;
        Avg_I_max = totalIMax / nSuccessfulRuns;
        Avg_I_avg = totalIAvg / nSuccessfulRuns;

        Avg_GR_early = totalGREarly / nSuccessfulRuns;
        Avg_GR_late = totalGRLate / nSuccessfulRuns;
        Avg_GR_avg = totalGRAvg / nSuccessfulRuns;

        Avg_RR_avg = totalRRAvg / nSuccessfulRuns;
        Avg_RR_min = totalRRMin / nSuccessfulRuns;
        Avg_RR_max = totalRRMax / nSuccessfulRuns;

        Avg_Teta_avg = totalTetaAvg / nSuccessfulRuns;
        Avg_Teta_min = totalTetaMin / nSuccessfulRuns;
        Avg_Teta_max = totalTetaMax / nSuccessfulRuns;

        Avg_s_avg = totalSAvg / nSuccessfulRuns;
        Avg_s_min = totalSMin / nSuccessfulRuns;
        Avg_s_max = totalSMax / nSuccessfulRuns;

        Sigma_I_min = countSigma(imin, Avg_I_min);
        Sigma_I_max = countSigma(imax, Avg_I_max);
        Sigma_I_avg = countSigma(iavg, Avg_I_avg);

        Sigma_RR_min = countSigma(rr_min, Avg_RR_min);
        Sigma_RR_max = countSigma(rr_max, Avg_RR_max);
        Sigma_RR_avg = countSigma(rr_avg, Avg_RR_avg);

        Sigma_Teta_min = countSigma(teta_min, Avg_Teta_min);
        Sigma_Teta_max = countSigma(teta_max, Avg_Teta_max);
        Sigma_Teta_avg = countSigma(teta_avg, Avg_Teta_avg);
    }

    private double countSigma(List<Double> values, double average) {
        float total = 0;
        for (double value : values) {
            total += Math.pow(value - average, 2);
        }
        return (float) Math.sqrt(total / (values.size() - 1 == 0 ? 1 : values.size() - 1));
    }

}