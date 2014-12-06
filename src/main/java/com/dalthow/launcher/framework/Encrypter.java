/**
 * Etaron
 *
 *
 * @Author Dalthow Game Studios
 * @Class Encrypter.java
 *
 **/

package com.dalthow.launcher.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypter 
{
	// Declaration
	
    private static final Logger logger = LoggerFactory.getLogger(Encrypter.class);

    
    // Takes in a string and exports the encrypted version
    
    public static String encryptString(String input) 
    {
        String encryptedOutput = null;
        MessageDigest digest;

        try 
        {
            digest = MessageDigest.getInstance("MD5");

            digest.reset();
            digest.update(input.getBytes());

            byte[] currentDigest = digest.digest();

            BigInteger encryptedNumber = new BigInteger(1, currentDigest);

            encryptedOutput = encryptedNumber.toString(16);

            while(encryptedOutput.length() < 32) 
            {
                encryptedOutput = "0" + encryptedOutput;
            }   
        } 
        
        catch(NoSuchAlgorithmException error) 
        {
            logger.error(error.getMessage(), error);
        }

        return encryptedOutput;
    }
}
