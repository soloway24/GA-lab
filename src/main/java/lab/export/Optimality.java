package lab.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Optimality {

    TWENTY_FIVE(0.25),
    FIFTY(0.5),
    SEVENTY_FIVE(0.75),
    NINETY(0.9),
    ;

    private final double percentage;
}