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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class KeyVaultDemoApplication {

    private static final String MSI_CLIENT_ID = "MSI_CLIENT_ID";
    private static final String KEY_VAULT_URL = "KEY_VAULT_URL";

    public KeyVaultDemoApplication() {

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

    @RequestMapping(value="/products", method = RequestMethod.GET, produces = { "application/json" })
    public List<Product> getProducts() {

        List<Product> products = new ArrayList<Product>();

        products.add(new Product(1, "Product 1", "Product #1 Description", 5));
        products.add(new Product(2, "Product 2", "Product #2 Description", 3));

        return products;
    }

    @RequestMapping(value="/products2", method = RequestMethod.GET, produces = {"application/json"})
    public List<Product> getProducts3() {

        final String msiClientId = getEnvironmentVariables().get(MSI_CLIENT_ID);
        final String keyVaultURL = getEnvironmentVariables().get(KEY_VAULT_URL);

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

    public static void main(String[] args) {
        SpringApplication.run(KeyVaultDemoApplication.class, args);
    }
}
