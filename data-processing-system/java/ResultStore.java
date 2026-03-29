import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResultStore {
    /*
     * The synchronized list keeps result access safe while multiple workers
     * add results in parallel. This is the shared resource we must protect.
     */
    private final List<ProcessedResult> results = Collections.synchronizedList(new ArrayList<>());

    public void addResult(ProcessedResult result) {
        results.add(result);
    }

    public List<ProcessedResult> getAllResults() {
        synchronized (results) {
            return new ArrayList<>(results);
        }
    }

    public void writeResultsToFile(String fileName) {
        List<ProcessedResult> snapshot = getAllResults();
        snapshot.sort((left, right) -> Integer.compare(left.getTaskId(), right.getTaskId()));

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            writer.println("Data Processing System Results");
            writer.println("========================================");
            for (ProcessedResult result : snapshot) {
                writer.println(result.toString());
            }
            writer.println("========================================");
            writer.println("Total results: " + snapshot.size());
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to write results file: " + e.getMessage());
        }
    }
}
