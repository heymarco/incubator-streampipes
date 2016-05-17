package de.fzi.cep.sepa.rest.api.v2;

import javax.servlet.http.HttpServletRequest;

public interface Authentication {

	public String doLogin(String token);
	
	public String doLogout();
	
	public String doRegister(String registrationData);
	
	public String userAuthenticated(HttpServletRequest req);
	
}
