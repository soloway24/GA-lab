package lab.v2.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Homogeneity {

    SEVENTY(0.7),
    EIGHTY(0.8),
    NINETY(0.9),
    NINETY_FIVE(0.95),
    NINETY_NINE(0.99),
    ;

    private final double percentage;
}
