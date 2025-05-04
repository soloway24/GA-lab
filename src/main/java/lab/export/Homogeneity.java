package lab.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Homogeneity {

    SEVENTY_FIVE(0.75),
    NINETY(0.9),
    NINETY_FIVE(0.95),
    NINETY_NINE(0.99),
    ;

    private final double percentage;
}
