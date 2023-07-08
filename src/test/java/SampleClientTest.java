import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SampleClientTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testReadLastNamesFromFile() throws IOException {
        File testFile = tempFolder.newFile("test.txt");
        List<String> expectedLastNames = Arrays.asList("Smith", "Johnson", "Williams");
        Files.write(testFile.toPath(), expectedLastNames);

        List<String> lastNames = SampleClient.readLastNamesFromFile(testFile.toString());

        assertEquals(expectedLastNames, lastNames);
    }

    @Test
    public void testExtractPatientListFromBundle() {
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        Observation observation = new Observation();

        Bundle bundle = new Bundle();
        bundle.addEntry().setResource(patient1);
        bundle.addEntry().setResource(observation);
        bundle.addEntry().setResource(patient2);

        List<Patient> patients = SampleClient.extractPatientListFromBundle(bundle);

        assertEquals(2, patients.size());
        assertTrue(patients.contains(patient1));
        assertTrue(patients.contains(patient2));
    }
}