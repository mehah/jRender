package greencode.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SecurityUtils {
	
	public enum TYPE {		 
		MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"),
		SHA384("SHA-384"), SHA512("SHA-512");
		 
		public final String type;
		 
		private TYPE(final String type) {
			this.type = type;
		}
	}
	
	public final static byte[] generateHash(String text, TYPE type) throws NoSuchAlgorithmException {
	    MessageDigest md = MessageDigest.getInstance(type.type);
	    md.update(text.getBytes());
	    return md.digest();
	}
	
	public final static String generateString(String text, TYPE type) {
	    try {
			return new BigInteger(1, SecurityUtils.generateHash(text, type)).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    
	    return null;
	}	
}