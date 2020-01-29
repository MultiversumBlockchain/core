package io.multiversum.core.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WalletFile {

	@JsonProperty(value = "address")
	String address;
	
	@JsonProperty(value = "public_key")
	String pubKey;
	
	@JsonProperty(value = "private_key")
	String privateKey;
	
	@JsonProperty(value = "keywords")
	String keyWords;
	
	@JsonProperty(value = "passphrase")
	String passphrase;
	
	public WalletFile() {}
	
	public WalletFile(String address, String pubKey, String privateKey, String keyWords, String passphrase) {
		this.address = address;
		this.pubKey = pubKey;
		this.privateKey = privateKey;
		this.keyWords = keyWords;
		this.passphrase = passphrase;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	@Override
	public String toString() {
		return "WalletFile [address=" + address + ", pubKey=" + pubKey + ", privateKey=" + privateKey + ", keyWords="
				+ keyWords + ", passphrase=" + passphrase + "]";
	}

}
