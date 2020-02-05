package com.microsoft.demo.utils;

import com.azure.identity.*;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

import java.util.Map;

public class BadKeyVaultAuthenticator {

    /**
     * Authenticates with Resource Credentials
     *
     * This strategy is not recommended
     *
     * @return Returns the Client Object
     */
    public static SecretClient authenticateWithCredentials() {

        // Retrieving Service Principal Credentials from Environment Variables
        final String msiClientId = "deedbeadc-d554-488e-8402-23ac5f7a2867";
        final String msiPassword = "Xtremely@Secure@P855word@That@Cannot@Be@Hacked!!!";
        final String msiTenant = "deedbeef-86f1-41af-91ab-2d7cd011db47";
        final String keyVaultURL = "https://mswinteready.vault.azure.net/";

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

}
