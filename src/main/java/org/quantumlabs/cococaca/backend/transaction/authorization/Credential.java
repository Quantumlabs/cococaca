package org.quantumlabs.cococaca.backend.transaction.authorization;

public class Credential {
	private String userName;
	private String password;

	public Credential(String userEmail, String userPassword) {
		this.userName = userEmail;
		this.password = userPassword;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
}
