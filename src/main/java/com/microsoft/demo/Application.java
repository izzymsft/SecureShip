package com.microsoft.demo;

import com.azure.identity.ChainedTokenCredential;
import com.azure.identity.ChainedTokenCredentialBuilder;
import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.microsoft.demo.models.Product;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    private static final String MSI_CLIENT_ID = "MSI_CLIENT_ID";
    private static final String KEY_VAULT_URL = "KEY_VAULT_URL";

    private static final String AUTH_STRATEGY = "DB_AUTHENTICATION_STRATEGY";
    private static final String DB_SERVER = "DATABASE_SERVER";
    private static final String DB_NAME = "DATABASE_NAME";

    public Application() {

    }

    @RequestMapping(value="/products", method = RequestMethod.GET, produces = { "application/json" })
    public List<Product> getProducts() {

        List<Product> products = new ArrayList<Product>();

        products.add(new Product(1, "", "", 1));
        products.add(new Product(2, "", "", 1));

        return products;
    }

    /**
     * Returns all the Available Environment Variables
     *
     * There are many subtle differences between the way environment variables are implemented on different systems.
     *
     * For example, Windows ignores case in environment variable names, while UNIX does not.
     *
     * The way environment variables are used also varies.
     *
     * For example, Windows provides the user name in an environment variable called USERNAME,
     * while UNIX implementations might provide the user name in USER, LOGNAME, or both.
     *
     * To maximize portability, never refer to an environment variable when the same value is available in a system property.
     *
     * For example, if the operating system provides a user name, it will always be available in the system property user.name.
     *
     * See documentation for more information - https://docs.oracle.com/javase/tutorial/essential/environment/env.html
     * @return
     */
    private Map<String, String> getEnvironmentVariables() {

        // Retrieve the Hash Map (Key-Value pair or Dictionary) of Environment Variables
        Map<String, String> environmentVars = System.getenv();

        return environmentVars;
    }

    @RequestMapping(value="/products2", method = RequestMethod.GET, produces = {"application/json"})
    public List<Product> getProducts3() {

        // Retrieves the Environment Variables
        Map<String, String> environmentVars = this.getEnvironmentVariables();

        final String msiClientId = environmentVars.get(MSI_CLIENT_ID);
        final String keyVaultURL = environmentVars.get(KEY_VAULT_URL);

        ManagedIdentityCredential managedIdentityCredential = new ManagedIdentityCredentialBuilder()
                .clientId(msiClientId)
                .build();

        ChainedTokenCredential credentialChain = new ChainedTokenCredentialBuilder()
                .addLast(managedIdentityCredential)
                .build();

        SecretClient client = new SecretClientBuilder()
                .vaultUrl(keyVaultURL)
                .credential(credentialChain)
                .buildClient();


        KeyVaultSecret secret = client.getSecret("usa");

        List<Product> products = new ArrayList<Product>();

        products.add(new Product(1, secret.getName(), secret.getValue(), 1));

        return products;
    }

    @RequestMapping(value="/product3", method = RequestMethod.GET, produces = { "application/json" })
    public List<Product> getProducts2() {

        List<Product> products = new ArrayList<Product>();

        Map<String, String> environmentVars = this.getEnvironmentVariables();

        final String msiClientId = environmentVars.get(MSI_CLIENT_ID);
        final String serverName = environmentVars.get(DB_SERVER);
        final String databaseName = environmentVars.get(DB_NAME);
        final String authenticationStrategy = environmentVars.get(AUTH_STRATEGY); // Default is ActiveDirectoryMSI

        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setAuthentication(authenticationStrategy); // Authentication Strategy
        ds.setMSIClientId(msiClientId); // Client ID for the User Manager Identity
        ds.setServerName(serverName); // Database Server Hostname
        ds.setDatabaseName(databaseName); // Database Name

        // Replace with Client ID of User-Assigned MSI to be used
        try (Connection connection = ds.getConnection();

             Statement stmt = connection.createStatement();

             ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.product_info")) {

            if (rs.next()) {

                int productId = rs.getInt(1);

                products.add(new Product(productId, "", "", 1));
            }

        } catch (Exception e) { // SQLException

        }

        return products;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
