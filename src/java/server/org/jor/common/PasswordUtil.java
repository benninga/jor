package org.jor.common;

public class PasswordUtil
{
    public static final String HASH_PREFIX = "HASH:";
    
    /**
     * Provide hash for a given password.
     * Null value passwords will be converted to empty string
     * 
     * @param password
     * @return one-way hash of the password
     */
    public static String hashPassword(String password)
    {
        if(password == null) {
            password = "";
        }
        
        // gensalt's log_rounds parameter determines the complexity
        // the work factor is 2**log_rounds, and the default is 10
        final int saltComplexity = 10;
        
        // Hash a password for the first time
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(saltComplexity));
        hashed = addPasswordPrefix(hashed);
        return hashed;
    }
    
    public static boolean matchPassword(String password, String hashedPassword)
    {
        if(password == null) {
            password = "";
        }
        if(hashedPassword == null) {
            return false;
        }
        hashedPassword = removePasswordPrefix(hashedPassword);
        
        if (BCrypt.checkpw(password, hashedPassword))
        {
            return true;
        }
        return false;
    }
    
    public static String addPasswordPrefix(String password)
    {
        return HASH_PREFIX + password;
    }
    
    public static String removePasswordPrefix(String password)
    {
        if(password.startsWith(HASH_PREFIX) == false) {
            throw new IllegalArgumentException("Password does not start with hash prefix");
        }
        return password.substring(HASH_PREFIX.length());
    }
}
