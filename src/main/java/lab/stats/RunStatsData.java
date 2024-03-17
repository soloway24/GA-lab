package lab.stats;

import lombok.Getter;

import java.util.List;

@Getter
public class RunStatsData {

    public long NI;
    public double F_found, F_avg, Intensity, Diversity, Sigma, BestPercent, GR, RR, TETA;

    public List<Integer> ones;
    public List<Double> phenotypes;
    public List<Double> fitness;

    public RunStatsData(int generation, double average_health, double best_health, double intensity, double diversity, double sigma, double best_percent, double growth_rate, double reproduction_rate, double loss_of_diversity) {
        NI = generation;
        F_found = best_health;
        F_avg = average_health;
        Intensity = intensity;
        Diversity = diversity;
        Sigma = sigma;
        BestPercent = best_percent;
        GR = growth_rate;
        RR = reproduction_rate;
        TETA = loss_of_diversity;
    }
}
