package lab.parameters;

import lombok.Getter;

@Getter
public enum ContestType {
    UNIQUE_ENTRY("without_replacement"),
    MULTIPLE_ENTRY("with_replacement");

    private final String outPath;

    ContestType(String out) {
        outPath = out;
    }
}
