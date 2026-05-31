package ua.lpnu.coffevan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility to parse JaCoCo CSV report and generate a markdown summary.
 */
public class CoverageReporter {

    public static void main(String[] args) {
        String csvPath = "target/site/jacoco/jacoco.csv";
        String outputPath = "doc/coverage_report.md";

        File csvFile = new File(csvPath);
        if (!csvFile.exists()) {
            System.err.println("Error: JaCoCo report not found at " + csvPath + ". Please run 'mvn clean test' first.");
            System.exit(1);
        }

        long totalInstructionsMissed = 0;
        long totalInstructionsCovered = 0;
        long totalBranchesMissed = 0;
        long totalBranchesCovered = 0;

        List<ClassCoverage> classCoverages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine(); // Read header
            if (headerLine == null) {
                System.err.println("Error: CSV file is empty");
                System.exit(1);
            }

            // Split headers to map column indices
            String[] headers = headerLine.split(",");
            int idxClass = -1;
            int idxPackage = -1;
            int idxInstMissed = -1;
            int idxInstCovered = -1;
            int idxBranchMissed = -1;
            int idxBranchCovered = -1;

            for (int i = 0; i < headers.length; i++) {
                switch (headers[i]) {
                    case "CLASS": idxClass = i; break;
                    case "PACKAGE": idxPackage = i; break;
                    case "INSTRUCTION_MISSED": idxInstMissed = i; break;
                    case "INSTRUCTION_COVERED": idxInstCovered = i; break;
                    case "BRANCH_MISSED": idxBranchMissed = i; break;
                    case "BRANCH_COVERED": idxBranchCovered = i; break;
                }
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < headers.length) continue;

                String className = values[idxClass];
                String packageName = values[idxPackage];
                long instMissed = Long.parseLong(values[idxInstMissed]);
                long instCovered = Long.parseLong(values[idxInstCovered]);
                long branchMissed = Long.parseLong(values[idxBranchMissed]);
                long branchCovered = Long.parseLong(values[idxBranchCovered]);

                totalInstructionsMissed += instMissed;
                totalInstructionsCovered += instCovered;
                totalBranchesMissed += branchMissed;
                totalBranchesCovered += branchCovered;

                long totalInst = instMissed + instCovered;
                double instCovPct = totalInst > 0 ? ((double) instCovered / totalInst) * 100.0 : 100.0;

                long totalBranch = branchMissed + branchCovered;
                double branchCovPct = totalBranch > 0 ? ((double) branchCovered / totalBranch) * 100.0 : 100.0;

                classCoverages.add(new ClassCoverage(
                        className,
                        packageName,
                        instCovPct,
                        instCovered,
                        totalInst,
                        totalBranch > 0,
                        branchCovPct,
                        branchCovered,
                        totalBranch
                ));
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            System.exit(1);
        }

        long totalInstAll = totalInstructionsMissed + totalInstructionsCovered;
        double totalInstCovPct = totalInstAll > 0 ? ((double) totalInstructionsCovered / totalInstAll) * 100.0 : 100.0;

        long totalBranchAll = totalBranchesMissed + totalBranchesCovered;
        double totalBranchCovPct = totalBranchAll > 0 ? ((double) totalBranchesCovered / totalBranchAll) * 100.0 : 100.0;

        // Sort by class name
        classCoverages.sort(Comparator.comparing(c -> c.className));

        StringBuilder sb = new StringBuilder();
        sb.append("# Звіт про покриття коду тестами (JaCoCo)\n\n");
        sb.append("## Загальна статистика\n");
        sb.append(String.format("- **Покриття інструкцій (Instructions):** %.2f%% (%d/%d)\n", totalInstCovPct, totalInstructionsCovered, totalInstAll));
        sb.append(String.format("- **Покриття розгалужень (Branches):** %.2f%% (%d/%d)\n\n", totalBranchCovPct, totalBranchesCovered, totalBranchAll));

        sb.append("## Детальний звіт по класах\n\n");
        sb.append("| Клас | Пакет | Покриття інструкцій | Покриття розгалужень |\n");
        sb.append("| :--- | :--- | :--- | :--- |\n");

        for (ClassCoverage c : classCoverages) {
            String instStr = String.format("%.2f%% (%d/%d)", c.instCovPct, c.instCovered, c.totalInst);
            String branchStr = c.hasBranches ? String.format("%.2f%% (%d/%d)", c.branchCovPct, c.branchCovered, c.totalBranch) : "N/A";
            sb.append(String.format("| `%s` | `%s` | %s | %s |\n", c.className, c.packageName, instStr, branchStr));
        }

        File outputFile = new File(outputPath);
        if (outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(sb.toString());
            System.out.println("Coverage report successfully written to " + outputPath);
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
            System.exit(1);
        }
    }

    private static class ClassCoverage {
        String className;
        String packageName;
        double instCovPct;
        long instCovered;
        long totalInst;
        boolean hasBranches;
        double branchCovPct;
        long branchCovered;
        long totalBranch;

        public ClassCoverage(String className, String packageName, double instCovPct, long instCovered, long totalInst,
                             boolean hasBranches, double branchCovPct, long branchCovered, long totalBranch) {
            this.className = className;
            this.packageName = packageName;
            this.instCovPct = instCovPct;
            this.instCovered = instCovered;
            this.totalInst = totalInst;
            this.hasBranches = hasBranches;
            this.branchCovPct = branchCovPct;
            this.branchCovered = branchCovered;
            this.totalBranch = totalBranch;
        }
    }
}
