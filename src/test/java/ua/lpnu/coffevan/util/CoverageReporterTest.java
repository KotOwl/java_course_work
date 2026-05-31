package ua.lpnu.coffevan.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Unit tests for {@link CoverageReporter}.
 *
 * <p>Tests call {@code CoverageReporter.generate(File, File)} directly so that
 * we avoid any {@code System.exit()} calls made by {@code main()} for error paths.
 */
class CoverageReporterTest {

    @TempDir
    Path tempDir;

    /** Minimal valid JaCoCo CSV with two classes (one with branches, one without). */
    private static final String VALID_CSV =
            "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED," +
            "BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED," +
            "COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n" +
            "coffee-van,ua/lpnu/coffevan/model,BeanCoffee,0,52,0,0,0,12,0,5,0,5\n" +
            "coffee-van,ua/lpnu/coffevan/model,Coffee,10,200,4,8,2,30,3,10,1,8\n";

    // ── helpers ──────────────────────────────────────────────────────────

    private File writeCsv(String content) throws IOException {
        Path csv = tempDir.resolve("jacoco.csv");
        Files.writeString(csv, content);
        return csv.toFile();
    }

    private File outputFile() {
        return tempDir.resolve("coverage_report.md").toFile();
    }

    private String generate(String csvContent) throws IOException {
        File csv = writeCsv(csvContent);
        File out = outputFile();
        CoverageReporter.generate(csv, out);
        return Files.readString(out.toPath());
    }

    // ── structural tests ─────────────────────────────────────────────────

    @Test
    void coverageReporter_hasPrivateConstructor() throws NoSuchMethodException {
        Constructor<CoverageReporter> ctor = CoverageReporter.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()),
                "CoverageReporter should have a private constructor");
    }

    // ── generate() happy-path ─────────────────────────────────────────────

    @Test
    void generate_createsOutputFile() throws IOException {
        writeCsv(VALID_CSV);
        File out = outputFile();
        CoverageReporter.generate(writeCsv(VALID_CSV), out);
        assertTrue(out.exists(), "Output file should be created");
    }

    @Test
    void generate_containsUkrainianHeading() throws IOException {
        String content = generate(VALID_CSV);
        assertTrue(content.contains("# Звіт про покриття коду тестами"),
                "Should contain Ukrainian heading");
    }

    @Test
    void generate_listsAllClasses() throws IOException {
        String content = generate(VALID_CSV);
        assertTrue(content.contains("BeanCoffee"), "Should list BeanCoffee");
        assertTrue(content.contains("Coffee"),     "Should list Coffee");
    }

    @Test
    void generate_beanCoffeeShows100PercentInstructionCoverage() throws IOException {
        String content = generate(VALID_CSV);
        // BeanCoffee: 52/52 → 100.00%
        assertTrue(content.contains("100.00%"),
                "BeanCoffee with 0 missed instructions should show 100.00%");
    }

    @Test
    void generate_coffeeShowsPartialInstructionCoverage() throws IOException {
        String content = generate(VALID_CSV);
        // Coffee: 200/210 → 95.24%
        assertTrue(content.contains("95.24%"),
                "Coffee class should show ~95.24% instruction coverage");
    }

    @Test
    void generate_beanCoffeeWithNoBranchesShowsNA() throws IOException {
        String content = generate(VALID_CSV);
        assertTrue(content.contains("N/A"),
                "Classes with no branches should show N/A");
    }

    @Test
    void generate_classesSortedAlphabetically() throws IOException {
        String content = generate(VALID_CSV);
        int beanPos   = content.indexOf("BeanCoffee");
        int coffeePos = content.indexOf("| `Coffee`");
        assertTrue(beanPos < coffeePos,
                "Classes should be sorted alphabetically (BeanCoffee before Coffee)");
    }

    @Test
    void generate_overallStatsSectionIsPresent() throws IOException {
        String content = generate(VALID_CSV);
        assertTrue(content.contains("Покриття інструкцій"),
                "Should contain instruction coverage line");
        assertTrue(content.contains("Покриття розгалужень"),
                "Should contain branch coverage line");
    }

    @Test
    void generate_overallBranchCoverage_isCalculatedCorrectly() throws IOException {
        String content = generate(VALID_CSV);
        // Only Coffee has branches: 8 covered / 12 total → 66.67%
        assertTrue(content.contains("66.67%"),
                "Overall branch coverage should be 66.67%");
    }

    // ── edge cases ────────────────────────────────────────────────────────

    @Test
    void generate_withEmptyCsvBody_stillCreatesFile() throws IOException {
        String headerOnly =
                "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED," +
                "BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED," +
                "COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n";
        String content = generate(headerOnly);
        assertTrue(content.contains("# Звіт"),
                "Report header should be written even for empty CSV body");
    }

    @Test
    void generate_withEmptyCsvBody_shows100PercentCoverage() throws IOException {
        String headerOnly =
                "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED," +
                "BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED," +
                "COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n";
        String content = generate(headerOnly);
        // 0/0 → 100.00%
        assertTrue(content.contains("100.00%"),
                "Empty data should result in 100% (0 covered / 0 total)");
    }

    @Test
    void generate_withAllBranchesMissed_showsZeroPercent() throws IOException {
        String csv =
                "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED," +
                "BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED," +
                "COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n" +
                "coffee-van,ua/lpnu/coffevan/model,SomeClass,5,95,4,0,1,10,2,4,1,3\n";
        String content = generate(csv);
        assertTrue(content.contains("0.00%"),
                "All-missed branches should show 0.00%");
    }

    @Test
    void generate_withMultiplePackages_listsAllClasses() throws IOException {
        String csv =
                "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED," +
                "BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED," +
                "COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n" +
                "cvan,ua/lpnu/coffevan/model,BeanCoffee,0,52,0,0,0,12,0,5,0,5\n" +
                "cvan,ua/lpnu/coffevan/service,VanService,3,270,1,21,1,60,1,15,0,10\n" +
                "cvan,ua/lpnu/coffevan/dao,CoffeeDaoImpl,5,477,2,25,1,110,2,20,0,18\n";
        String content = generate(csv);
        assertTrue(content.contains("BeanCoffee"),    "Should list BeanCoffee");
        assertTrue(content.contains("VanService"),    "Should list VanService");
        assertTrue(content.contains("CoffeeDaoImpl"), "Should list CoffeeDaoImpl");
    }

    @Test
    void generate_outputFileParentDirsAreCreatedAutomatically() throws IOException {
        File csv = writeCsv(VALID_CSV);
        // Output in a nested directory that doesn't exist yet
        File out = tempDir.resolve("nested/deep/coverage.md").toFile();
        CoverageReporter.generate(csv, out);
        assertTrue(out.exists(), "Output file should be created in nested directories");
    }

    @Test
    void generate_withRowsShorterThanHeader_skipsRow() throws IOException {
        // One invalid row (too few columns) + one valid row
        String csv =
                "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED," +
                "BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED," +
                "COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n" +
                "invalid,short,row\n" +                               // skipped
                "cvan,ua/lpnu/coffevan/model,BeanCoffee,0,52,0,0,0,12,0,5,0,5\n"; // valid
        String content = generate(csv);
        assertTrue(content.contains("BeanCoffee"), "Valid row should still be listed");
    }

    @Test
    void generate_withEmptyCsvFile_throwsIOException() throws IOException {
        File empty = tempDir.resolve("empty.csv").toFile();
        empty.createNewFile(); // exists but is truly empty (no header)
        assertThrows(IOException.class,
                () -> CoverageReporter.generate(empty, outputFile()),
                "Empty CSV file (no header) should throw IOException");
    }

    // ── main() coverage ───────────────────────────────────────────────────

    /**
     * Tests the {@code main()} happy path using the real JaCoCo CSV that
     * Maven generates during the {@code test} phase.
     *
     * <p>The test is skipped on first clean build (CSV not yet present) and
     * passes on subsequent runs once the CSV exists.
     */
    @Test
    void main_withRealJacocoCsv_writesReportSuccessfully() {
        File csv = new File("target/site/jacoco/jacoco.csv");
        assumeTrue(csv.exists(),
                "Skipping main() test: JaCoCo CSV not yet generated (run mvn test again)");

        // main() writes to doc/coverage_report.md relative to cwd
        assertDoesNotThrow(() -> CoverageReporter.main(new String[]{}),
                "main() should complete without throwing when CSV is present");

        File report = new File("doc/coverage_report.md");
        assertTrue(report.exists(), "doc/coverage_report.md should be created by main()");
    }
}
