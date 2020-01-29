package io.multiversum.core.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.github.novacrypto.bip39.wordlists.English;
import io.multiversum.core.conf.WalletFile;

@Service
public class WalletService {

	private static final Logger log = LoggerFactory.getLogger(WalletService.class);

	@Autowired
	ObjectMapper objectMapper;

	private String address;

	private String keywords;

	private String passphrase;

	private PublicKey publicKey;

	private PrivateKey privKey;
	
	@Autowired
	private SettingsService settingsService;
	
	public WalletService() {
		// TODO Auto-generated constructor stub
	}

	public void loadWallet() throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchProviderException {
		
		File walletFile = new File(this.settingsService.getHome() + File.separator + "wallet.json");

		if (walletFile.canRead()) {
			ObjectMapper objectMapper = new ObjectMapper();

			WalletFile wallet = objectMapper.readValue(walletFile, WalletFile.class);
		
			this.restoreFromKeyWords(wallet.getKeyWords(), wallet.getPassphrase());

		} else {
			log.warn("Missing wallet file. Please create one with create command or restore one with restore- command");
		}
	}
	
	public String getAddress() {
		SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
		byte[] digest = digestSHA3.digest(this.publicKey.getEncoded());

		return Hex.toHexString(digest);
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public PrivateKey getPrivateKey() {
		return this.privKey;
	}

	public PrivateKey getPrivateKeyByte() {
		return this.privKey;
	}

	public void saveWallet() throws JsonGenerationException, JsonMappingException, IOException {
		WalletFile wf = new WalletFile();
		
		wf.setAddress(this.getAddress());
		wf.setPrivateKey(Hex.toHexString(this.privKey.getEncoded()));
		wf.setPubKey(Hex.toHexString(this.publicKey.getEncoded()));
		wf.setKeyWords(this.keywords);

		wf.setPassphrase(passphrase);

		File tempFile = new File(System.getProperty("user.home") + "/.multiversum/wallet.json");
		if (tempFile.exists()) {
			
			log.warn("Overwriting config file?");
		}

		objectMapper.writeValue(tempFile, wf);

		log.info(String.format("Wallet saved. File: %s", tempFile));
	}

	public WalletFile generateWallet(String passphrase)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		String mnemonic = generateMnemonic();
		return restoreFromKeyWords(mnemonic, passphrase);
	}

	public String generateMnemonic() {
		StringBuilder sb = new StringBuilder();
		byte[] entropy = new byte[Words.TWELVE.byteLength()];
		new SecureRandom().nextBytes(entropy);
		new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
		return sb.toString();
	}
	
	public WalletFile restoreFromKeyWords(String keywords, String passphrase)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		Security.addProvider(new BouncyCastleProvider());

		this.keywords = keywords;
		this.passphrase = passphrase;
		
		WalletFile wf = new WalletFile();

		try {

			MnemonicValidator.ofWordList(English.INSTANCE).validate(keywords.trim());

			byte[] seed = new SeedCalculator().calculateSeed(keywords, passphrase);

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(seed);

			BigInteger bi = new BigInteger(hash);

			KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");

			ECPoint Q = ecSpec.getG().multiply(bi);

			ECPublicKeySpec pubSpec = new ECPublicKeySpec(Q, ecSpec);
			ECPrivateKeySpec privSpec = new ECPrivateKeySpec(bi, ecSpec);

			this.publicKey = keyFactory.generatePublic(pubSpec);
			this.privKey = keyFactory.generatePrivate(privSpec);

			wf.setAddress(this.getAddress());
			wf.setPrivateKey(Hex.toHexString(this.privKey.getEncoded()));
			wf.setPubKey(Hex.toHexString(this.publicKey.getEncoded()));
			wf.setKeyWords(this.keywords);
			wf.setPassphrase(passphrase);

		} catch (InvalidChecksumException | InvalidWordCountException | WordNotFoundException
				| UnexpectedWhiteSpaceException e) {
			e.printStackTrace();
		}

		return wf;

	}

	public File getWalletFile() {
		return new File(settingsService.getHome() + File.separator + "wallet.json");
	}
}
