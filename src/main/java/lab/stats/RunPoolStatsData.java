package lab.stats;

import lab.model.RunPool;
import lab.parameters.ContestType;
import lab.parameters.Encoding;
import lab.parameters.FitnessFunction;
import lab.parameters.GeneticOperatorsApplication;
import lab.utils.*;

public class RunPoolStatsData {
    public int Min_NI, Max_NI ;

    public int nSuccessfulRuns, NI_I_min, NI_I_max = 0;
    public double Avg_NI = 0,Sigma_NI, Suc = 0, Min_I_min = 0, Max_I_max = 0, Avg_I_min = 0, Avg_I_max = 0, Avg_I_avg = 0;
    public double Sigma_I_max = 0, Sigma_I_min = 0, Sigma_I_avg = 0;

    public double Min_GR_early, Max_GR_early, Avg_GR_early;
    public double Min_GR_late, Max_GR_late, Avg_GR_late;
    public double Min_GR_avg, Max_GR_avg, Avg_GR_avg;

    public double Min_RR_min, Max_RR_max, Avg_RR_min, Avg_RR_max, Avg_RR_avg;
    public long NI_RR_min, NI_RR_max;
    public double Sigma_RR_max, Sigma_RR_min, Sigma_RR_avg;

    public double Min_Teta_min, Max_Teta_max, Avg_Teta_min, Avg_Teta_max, Avg_Teta_avg;
    public long NI_Teta_min, NI_Teta_max;
    public double Sigma_Teta_max, Sigma_Teta_min, Sigma_Teta_avg;

    public double Min_s_min, Max_s_max, Avg_s_min, Avg_s_max, Avg_s_avg;
    public long NI_s_min, NI_s_max;


    public Encoding encoding;
    public ContestType contestType;
    public FitnessFunction fitnessFunction;
    public GeneticOperatorsApplication geneticOperatorsApplication;
    public int N;
    public float P_SWAP;

    public RunPoolStatsData(RunPool pool) {
        fitnessFunction = GeneticUtils.FITNESS_FUNCTION;
        N = GeneticUtils.N;
        contestType = GeneticUtils.CONTEST_TYPE;
        P_SWAP = GeneticUtils.P_SWAP;
        geneticOperatorsApplication = GeneticUtils.GENETIC_OPERATORS;
        encoding = GeneticUtils.ENCODING;

        Min_NI = pool.Min_NI;
        Max_NI = pool.Max_NI;
        Avg_NI = pool.Avg_NI;
        Sigma_NI = pool.Sigma_NI;

        Suc = pool.Suc;
        this.nSuccessfulRuns = pool.nSuccessfulRuns;
        this.NI_I_min = pool.NI_I_min;
        this.NI_I_max = pool.NI_I_max;

        Min_I_min = pool.Min_I_min;
        Max_I_max = pool.Max_I_max;

        Avg_I_min = pool.Avg_I_min;
        Avg_I_max = pool.Avg_I_max;
        Avg_I_avg = pool.Avg_I_avg;

        Sigma_I_max = pool.Sigma_I_max;
        Sigma_I_min = pool.Sigma_I_min;
        Sigma_I_avg = pool.Sigma_I_avg;

        Min_GR_early = pool.Min_GR_early;
        Max_GR_early = pool.Max_GR_early;
        Avg_GR_early = pool.Avg_GR_early;

        Min_GR_late = pool.Min_GR_late;
        Max_GR_late = pool.Max_GR_late;
        Avg_GR_late = pool.Avg_GR_late;

        Min_GR_avg = pool.Min_GR_avg;
        Max_GR_avg = pool.Max_GR_avg;
        Avg_GR_avg = pool.Avg_GR_avg;

        Min_RR_min = pool.Min_RR_min;
        Max_RR_max = pool.Max_RR_max;
        Avg_RR_min = pool.Avg_RR_min;
        Avg_RR_max = pool.Avg_RR_max;
        Avg_RR_avg = pool.Avg_RR_avg;

        this.NI_RR_min = pool.NI_RR_min;
        this.NI_RR_max = pool.NI_RR_max;

        Sigma_RR_max = pool.Sigma_RR_max;
        Sigma_RR_min = pool.Sigma_RR_min;
        Sigma_RR_avg = pool.Sigma_RR_avg;

        Min_Teta_min = pool.Min_Teta_min;
        Max_Teta_max = pool.Max_Teta_max;

        Avg_Teta_min = pool.Avg_Teta_min;
        Avg_Teta_max = pool.Avg_Teta_max;
        Avg_Teta_avg = pool.Avg_Teta_avg;

        this.NI_Teta_min = pool.NI_Teta_min;
        this.NI_Teta_max = pool.NI_Teta_max;

        Sigma_Teta_max = pool.Sigma_Teta_max;
        Sigma_Teta_min = pool.Sigma_Teta_min;
        Sigma_Teta_avg = pool.Sigma_Teta_avg;

        Min_s_min = pool.Min_s_min;
        Max_s_max = pool.Max_s_max;

        Avg_s_min = pool.Avg_s_min;
        Avg_s_max = pool.Avg_s_max;
        Avg_s_avg = pool.Avg_s_avg;

        this.NI_s_min = pool.NI_s_min;
        this.NI_s_max = pool.NI_s_max;
    }

}
