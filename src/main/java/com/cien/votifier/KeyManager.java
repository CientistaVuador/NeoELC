package com.cien.votifier;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class KeyManager {

	private static KeyPairGenerator theGenerator;
	private static final File folder = new File("votifier_keys");
	private static final File publickeyfile = new File(folder, "public_key.txt");
	private static final File privatekeyfile = new File(folder, "private_key.txt");
	
	static {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
			generator.initialize(spec);
			theGenerator = generator;
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
			//never happens
			theGenerator = null;
		}
	}
	
	public static File getFolder() {
		return folder;
	}
	
	public static File getPrivateKeyFile() {
		return privatekeyfile;
	}
	
	public static File getPublicKeyFile() {
		return publickeyfile;
	}
	
	public static void writeKeyPair(KeyPair p) throws IOException {
		folder.mkdirs();
		
		//public key
		PublicKey pub = p.getPublic();
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pub.getEncoded());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(publickeyfile));
		byte[] pubBase64 = DatatypeConverter.printBase64Binary(pubSpec.getEncoded()).getBytes();
		out.write(pubBase64);
		out.close();
		
		//private key
		PrivateKey priv = p.getPrivate();
		PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(priv.getEncoded());
		out = new BufferedOutputStream(new FileOutputStream(privatekeyfile));
		byte[] privBase64 = DatatypeConverter.printBase64Binary(privSpec.getEncoded()).getBytes();
		out.write(privBase64);
		out.close();
	}
	
	public static KeyPair readKeyPair() throws IOException {
		if (!folder.exists()) {
			return null;
		}
		if (!privatekeyfile.exists()) {
			return null;
		}
		if (!publickeyfile.exists()) {
			return null;
		}
		BufferedInputStream in;
		byte[] bytes;
		
		//public key
		in = new BufferedInputStream(new FileInputStream(publickeyfile));
		bytes = new byte[(int) publickeyfile.length()];
		in.read(bytes);
		byte[] publicKey = DatatypeConverter.parseBase64Binary(new String(bytes));
		in.close();
		
		//private key
		in = new BufferedInputStream(new FileInputStream(privatekeyfile));
		bytes = new byte[(int) privatekeyfile.length()];
		in.read(bytes);
		byte[] privateKey = DatatypeConverter.parseBase64Binary(new String(bytes));
		in.close();
		
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey);
			PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey);
			
			PublicKey publ = factory.generatePublic(publicSpec);
			PrivateKey priv = factory.generatePrivate(privateSpec);
			
			return new KeyPair(publ, priv);
		} catch (NoSuchAlgorithmException ex) {
			//never happens
			return null;
		} catch (InvalidKeySpecException e) {
			throw new IOException(e);
		}
	}
	
	public static byte[] encrypt(byte[] data, KeyPair pair) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			return null;
		}
	}
	
	public static byte[] decrypt(byte[] data, KeyPair pair) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			return null;
		}
	}
	
	public static KeyPair newPair() {
		return theGenerator.generateKeyPair();
	}
	
	private KeyManager() {
		// TODO Auto-generated constructor stub
	}

}
