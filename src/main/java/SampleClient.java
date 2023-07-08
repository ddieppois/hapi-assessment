import Interceptors.NoCacheInterceptor;
import Interceptors.ResponseTimeInterceptor;
import Models.PatientResponse;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SampleClient {

    // File path to the names.txt file
    private static final String FILE_PATH = "src/main/resources/names.txt";
    private static final int SEARCH_LOOP = 3;

    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        //Start set of tasks
        basicTasks(client);
        intermediateTasks(client);

    }

    /**
     * This method process the basic tasks for the FHIR assessment
     *
     * @param client
     * @return
     */
    private static void basicTasks(IGenericClient client) {
        //Basic tasks output
        System.out.println("\n*********** Basic Tasks Start ***********");
        List<Patient> patients = extractPatientListFromBundle(bundleSearch("SMITH", client));

        // Collect the patient information in a list sorted by firstName using the custom PatientResponse object
        List<PatientResponse> patientInfoList = patients.stream()
                .map(patient -> new PatientResponse(
                        patient.getNameFirstRep().getGivenAsSingleString(),
                        patient.getNameFirstRep().getFamily(),
                        patient.getBirthDate() != null ? patient.getBirthDate().toString() : "N/A"
                ))
                .sorted(Comparator.comparing(patientResponse -> patientResponse.getFirstName()))
                .collect(Collectors.toList());

        // Print the sorted patient information
        for (PatientResponse patientInfo : patientInfoList) {
            System.out.println(patientInfo);
        }
    }

    /**
     * This method process the intermediate tasks for the FHIR assessment
     *
     * @param client
     * @return
     */
    private static void intermediateTasks(IGenericClient client) {
        //Intermediate tasks output
        try {
            System.out.println("\n*********** Intermediate Tasks Start ***********");
            ResponseTimeInterceptor responseTimeInterceptor = new ResponseTimeInterceptor();
            client.registerInterceptor(responseTimeInterceptor);
            List<String> nameList = readLastNamesFromFile(FILE_PATH);
            for (int i = 0; i < SEARCH_LOOP; i++) {
                if (i == 2) {
                    client.registerInterceptor(new NoCacheInterceptor());
                }
                //Retrieving names from file and performing a search for each
                nameList.forEach(lastName -> {
                    System.out.println("Searching for last name: " + lastName);
                    bundleSearch(lastName, client);
                });
                //Displaying average response time for the set of requests
                responseTimeInterceptor.displayAverageResponseTime();
                responseTimeInterceptor.resetResponseTimes();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts a list of patients from a bundle
     *
     * @param bundle The bundle to extract the patients from
     * @return A list of patients
     */
    public static List<Patient> extractPatientListFromBundle(Bundle bundle) {
        List<Patient> patientList = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource resource = entry.getResource();
            if (resource instanceof Patient) {
                patientList.add((Patient) resource);
            }
        }
        return patientList;
    }

    /**
     * Performs a search for patients with the given family name
     *
     * @param familyName The family name to search for
     * @param client     The client to perform the search with
     * @return A bundle containing the search results
     */
    private static Bundle bundleSearch(String familyName, IGenericClient client) {

        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value(familyName))
                .returnBundle(Bundle.class)
                .execute();
        return response;
    }

    /**
     * Reads the last names from a file
     *
     * @param filePath The path to the file to read from
     * @return A list of last names
     * @throws IOException
     */
    public static List<String> readLastNamesFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }

}
