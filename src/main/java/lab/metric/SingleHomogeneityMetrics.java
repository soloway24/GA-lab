package lab.metric;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record SingleHomogeneityMetrics(
        int ni75h,
        double avg75h,
        int numOpt75h,

        int ni90h,
        double avg90h,
        int numOpt90h,

        int ni95h,
        double avg95h,
        int numOpt95h
) {
}