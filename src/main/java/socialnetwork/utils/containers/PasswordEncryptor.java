package socialnetwork.utils.containers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
    private MessageDigest messageDigest;
    public PasswordEncryptor(){
        try {
            this.messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public boolean authenticate(String password, String hashedPassword){
        messageDigest.update(password.getBytes());
        byte[] hashedPasswordInBytes = messageDigest.digest();
        var v = hashedPassword.getBytes();
        String expectedHashedPassword = convertToStringByteArray(hashedPasswordInBytes);
        return expectedHashedPassword.equals(hashedPassword);
    }

    private String convertToStringByteArray(byte[] hashedPasswordInBytes) {
        StringBuilder s = new StringBuilder();
        for(int i=0; i< hashedPasswordInBytes.length ;i++)
        {
            s.append(Integer.toString((hashedPasswordInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return s.toString();
    }

    public String hash(String password){
        messageDigest.update(password.getBytes());
        return convertToStringByteArray(messageDigest.digest());
    }
}
