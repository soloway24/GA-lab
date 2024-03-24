package lab.v2.run;

import java.util.List;

public record RunPool(RunConfiguration runConfiguration,
                      List<Run> runs) {

    public int getSize() {
        return runs.size();
    }
}
