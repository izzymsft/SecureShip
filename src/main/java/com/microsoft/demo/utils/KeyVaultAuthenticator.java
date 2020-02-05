package com.microsoft.demo.utils;

import com.azure.identity.*;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

import java.util.Map;

public class KeyVaultAuthenticator {

    // Static Variables Used as Lookup Keys in the Application
    private static final String MSI_CLIENT_ID = "MSI_CLIENT_ID"; // The lookup key for the client id
    private static final String MSI_CLIENT_SECRET = "MSI_CLIENT_SECRET"; // The lookup key for the client id
    private static final String MSI_CLIENT_TENANT = "MSI_CLIENT_TENANT"; // The lookup key for the client id
    private static final String KEY_VAULT_URL = "KEY_VAULT_URL"; // The lookup key for the key vault resource

    /**
     * Authenticates with Azure Active Directory
     *
     * This strategy is strongly recommended
     *
     * @return Returns the Client Object
     */
    public static SecretClient authenticateWithAzureActiveDirectory() {

        // Retrieving Client Id and Vault URL from Environment Variables
        final String msiClientId = getEnvironmentVariables().get(MSI_CLIENT_ID);
        final String keyVaultURL = getEnvironmentVariables().get(KEY_VAULT_URL);

        ManagedIdentityCredential managedIdentityCredential;

        // Setting up the Managed Identity Credentials
        managedIdentityCredential = new ManagedIdentityCredentialBuilder()
                .clientId(msiClientId)
                .build();

        // Setting up the Credential Chain
        ChainedTokenCredential credentialChain = new ChainedTokenCredentialBuilder()
                .addLast(managedIdentityCredential)
                .build();

        // Setting up the Key Vault Client
        SecretClient client = new SecretClientBuilder()
                .vaultUrl(keyVaultURL)
                .credential(credentialChain)
                .buildClient();

        return client;
    }

    /**
     * Authenticates with Resource Credentials
     *
     * This strategy is not recommended
     *
     * @return Returns the Client Object
     */
    public static SecretClient authenticateWithCredentials() {

        // Retrieving Service Principal Credentials from Environment Variables
        final String msiClientId = getEnvironmentVariables().get(MSI_CLIENT_ID);
        final String msiPassword = getEnvironmentVariables().get(MSI_CLIENT_SECRET);
        final String msiTenant = getEnvironmentVariables().get(MSI_CLIENT_TENANT);
        final String keyVaultURL = getEnvironmentVariables().get(KEY_VAULT_URL);

        ClientSecretCredential unSecureCredentials = new ClientSecretCredentialBuilder()
                .clientId(msiClientId)
                .clientSecret(msiPassword)
                .tenantId(msiTenant)
                .build();

        // Setting up the Credential Chain
        ChainedTokenCredential credentialChain = new ChainedTokenCredentialBuilder()
                .addLast(unSecureCredentials)
                .build();

        // Setting up the Key Vault Client
        SecretClient client = new SecretClientBuilder()
                .vaultUrl(keyVaultURL)
                .credential(credentialChain)
                .buildClient();

        return client;
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
     * @return The Environment Variables
     */
    private static Map<String, String> getEnvironmentVariables() {

        // Retrieve the Hash Map (Key-Value pair or Dictionary) of Environment Variables
        Map<String, String> environmentVars = System.getenv();

        return environmentVars;
    }
}
