package lab.export;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lab.Individual;
import lab.population.PopulationTimingType;
import lab.selection.Selector;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SnapshotProbabilityPlotter {

    public void plotSelectorProbabilities(
            Map<Selector, Map<PopulationTimingType, Map<Individual, Double>>> selectorToProbabilities,
            Path baseParserPath
    ) throws IOException, PythonExecutionException {

        // Replace 'stats_v2' with 'probabilities' in the path
        Path outputDir = replaceFirstDir(baseParserPath, "stats_v2", "probabilities");

        // Ensure the directory exists
        Files.createDirectories(outputDir);

        for (PopulationTimingType timing : PopulationTimingType.values()) {
            Plot plt = Plot.create();
            plt.title(capitalize(timing.name()) + " Probability Distribution");
            plt.xlabel("Individual Index (sorted)");
            plt.ylabel("Probability");

            for (Map.Entry<Selector, Map<PopulationTimingType, Map<Individual, Double>>> selectorEntry : selectorToProbabilities.entrySet()) {
                Selector selector = selectorEntry.getKey();
                Map<PopulationTimingType, Map<Individual, Double>> timingMap = selectorEntry.getValue();

                Map<Individual, Double> probabilities = timingMap.get(timing);
                if (probabilities == null) continue;

                List<Double> sortedProbs = probabilities.values().stream()
                        .sorted()
                        .collect(Collectors.toList());

                List<Integer> x = new ArrayList<>();
                for (int i = 0; i < sortedProbs.size(); i++) {
                    x.add(i);
                }

                plt.plot()
                        .add(x, sortedProbs)
                        .label(selector.getFullName())
                        .linestyle("-")
                        .linewidth(2.0);
            }

            plt.legend().loc("best");
            plt.savefig(outputDir.resolve("selector_probs_" + timing + ".png").toString()).dpi(150);
            plt.executeSilently(); // No GUI popup
        }
    }

    private static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static Path replaceFirstDir(Path originalPath, String from, String to) {
        List<String> parts = new ArrayList<>();
        boolean replaced = false;
        for (Path part : originalPath) {
            String name = part.toString();
            if (!replaced && name.equals(from)) {
                parts.add(to);
                replaced = true;
            } else {
                parts.add(name);
            }
        }
        return Paths.get("", parts.toArray(new String[0])).getParent(); // exclude 'population_snapshots'
    }
}
