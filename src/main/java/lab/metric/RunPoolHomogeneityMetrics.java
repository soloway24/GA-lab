package lab.metric;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record RunPoolHomogeneityMetrics(
        int minNi75h,
        int maxNi75h,
        double avgNi75h,
        double sigmaNi75h,

        double minAvg75h,
        double maxAvg75h,
        double avgAvg75h,
        double sigmaAvg75h,

        int minNumOpt75h,
        int maxNumOpt75h,
        double avgNumOpt75h,
        double sigmaNumOpt75h,


        int minNi90h,
        int maxNi90h,
        double avgNi90h,
        double sigmaNi90h,

        double minAvg90h,
        double maxAvg90h,
        double avgAvg90h,
        double sigmaAvg90h,

        int minNumOpt90h,
        int maxNumOpt90h,
        double avgNumOpt90h,
        double sigmaNumOpt90h,


        int minNi95h,
        int maxNi95h,
        double avgNi95h,
        double sigmaNi95h,

        double minAvg95h,
        double maxAvg95h,
        double avgAvg95h,
        double sigmaAvg95h,

        int minNumOpt95h,
        int maxNumOpt95h,
        double avgNumOpt95h,
        double sigmaNumOpt95h
) {
}