import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class TestEnv {
    public static void main(String[] args) throws Exception {
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim();
                        String val = line.substring(eqIdx + 1).trim();
                        System.out.println("Key: [" + key + "], Val: [" + val + "]");
                    }
                }
            }
        }
    }
}
