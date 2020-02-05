package com.microsoft.demo;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.microsoft.demo.models.State;
import com.microsoft.demo.models.StateConfidentialData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.microsoft.demo.utils.KeyVaultAuthenticator.authenticateWithAzureActiveDirectory;
import static com.microsoft.demo.utils.KeyVaultAuthenticator.authenticateWithCredentials;

/**
 * Sample Application to Demonstrate Best Practices for the following
 *
 * - Accessing Azure Resources outside and AKS Cluster Securely
 * - Accessing Web Applications inside the AKS cluster Securely
 * - Automating the Deployment Process with Available Tools and Strategies
 *
 * API Docs is Available here http://localhost:8080/v3/api-docs/
 * Swagger UI HTML is Available here http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
@RestController()
@Tag(name = "United States Secret Locations", description = "Sample Application to Demonstrate Best Practices")
public class Application {

    private final List<String> lookupKeys = new ArrayList<>(16);

    private final Map<String, String> stateNames = new HashMap<>(16);

    /**
     * Default Constructor
     */
    public Application() {

        // Initializing the Lookup Keys
        this.initializeLookupKeys();
    }

    /**
     * Private Method Used for Setting up State Data
     */
    private void initializeLookupKeys() {

        // Loading up the two-letter abbreviations for the state names
        lookupKeys.add("CA");
        lookupKeys.add("FL");
        lookupKeys.add("NJ");
        lookupKeys.add("NY");
        lookupKeys.add("TX");
        lookupKeys.add("WA");

        // Loading up the the full names
        stateNames.put("CA", "California");
        stateNames.put("FL", "Florida");
        stateNames.put("NJ", "New Jersey");
        stateNames.put("NY", "New York");
        stateNames.put("TX", "Texas");
        stateNames.put("WA", "Washington");
    }

    /**
     * Returns ALL the Available States and Non-Confidential Data
     *
     * @return The Available States
     */
    @GetMapping(value="/states", produces = { "application/json" })
    @Operation(description = "Returns ALL the Available States and Non-Confidential Data",
            responses = {@ApiResponse(responseCode = "200", description = "All states names and information") })
    public List<State> listStates() {

        // This will store the states location
        List<State> statesCollection = new ArrayList<>();

        // Prepare the data to return to the calling client
        for(String stateAbbreviation : this.lookupKeys) {

            String fullStateName = this.stateNames.get(stateAbbreviation);

            State currentStateData = new State(stateAbbreviation, fullStateName);

            // Adding to the collection result set
            statesCollection.add(currentStateData);
        }

        return statesCollection;
    }


    /**
     * Returns ALL the Available States Plus the Confidential Locations of the State Capital
     *
     * @return The Confidential Collection
     */
    @GetMapping(value="/states/secure-data", produces = {"application/json"})
    @Operation(description = "Returns ALL the Available States and Non-Confidential Data",
            responses = {@ApiResponse(responseCode = "200", description = "Returns ALL the Available States Plus the Confidential Locations of the State Capital") })
    public List<StateConfidentialData> getConfidentialData() {

        //SecretClient client = authenticateWithCredentials();
        SecretClient client = authenticateWithAzureActiveDirectory();

        // This will store all the confidential data we are about to return to the calling client
        List<StateConfidentialData> confidentialCollection = new ArrayList<>();

        // Go through the list of state abbreviations and prepare the confidential collection to return
        for(final String abbr : this.lookupKeys) {

            // Lookup the full name for the state
            String fullName = this.stateNames.get(abbr);

            // Retrieves a Secret from Key Vault containing the confidential information
            KeyVaultSecret secret = client.getSecret(abbr);

            // Confidential data we are retrieving from key-vault (location of best recipes and restaurants)
            String stateCapital = secret.getValue();

            // Construct the confidential object
            StateConfidentialData currentStateData = new StateConfidentialData(abbr, fullName, stateCapital);

            // Add it to the collection
            confidentialCollection.add(currentStateData);
        }

        return confidentialCollection;
    }

    /**
     * Simulates a Post Request - Returns ALL the Available States and Non-Confidential Data
     *
     * @return The Available States
     */
    @PostMapping(value="/states", produces = { "application/json" })
    @Operation(description = "Simulates the creation of the new state record",
            responses = {@ApiResponse(responseCode = "200", description = "Returns ALL the Available States Plus the Confidential Locations of the State Capital") })
    public List<State> createState(@RequestBody State body) {

        return this.listStates();
    }

    /**
     * Simulates a Put Request - Returns ALL the Available States and Non-Confidential Data
     *
     * @return The Available States
     */
    @PutMapping(value="/states/{id}", produces = { "application/json" })
    @Operation(description = "Simulates the modification of an existing state record given the id",
            responses = {@ApiResponse(responseCode = "200", description = "Returns ALL the Available States Plus the Confidential Locations of the State Capital") })
    public List<State> modifyState(@PathVariable("id") String id, @RequestBody State body) {

        return this.listStates();
    }

    /**
     * Simulates a Delete Request - Returns ALL the Available States and Non-Confidential Data
     *
     * @return The Available States
     */
    @DeleteMapping(value="/states/{id}", produces = { "application/json" })
    @Operation(description = "Simulates the removal of an existing state record given the id",
            responses = {@ApiResponse(responseCode = "200", description = "Returns ALL the Available States Plus the Confidential Locations of the State Capital") })
    public List<State> removeState(@PathVariable("id") String id) {

        return this.listStates();
    }

    private void logRequestContents(final String contents) {

        contents.toString();
    }

    /**
     * Entry Point for the Application
     *
     * @param args Arguments for the Application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
