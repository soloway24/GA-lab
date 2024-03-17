package lab.parameters;

import lombok.Getter;

@Getter
public enum Encoding {
    STANDARD("Standard"),
    GRAY("Gray");

    private final String outPath;

    Encoding(String out) {
        outPath = out;
    }

}
