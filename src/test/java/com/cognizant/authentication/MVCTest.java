package com.cognizant.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.cognizant.authentication.dto.ConfirmPasswordDTO;
import com.cognizant.authentication.dto.PasswordChangeDTO;
import com.cognizant.authentication.model.AuthenticationRequest;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class MVCTest {

	@Autowired
	private MockMvc mvc;
	
	//Valid Login
	@Test
	public void testLogin() throws Exception {
		Gson gson = new Gson();
		AuthenticationRequest request = new AuthenticationRequest("triquetrx", "zaidkhan");
		String json = gson.toJson(request);
		mvc.perform(post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(200))
		.andExpect(jsonPath("$.name", is("Zaid Khan")));
	}
	
	//Invalid Login
	@Test
	public void testInvalidLogin() throws Exception {
		Gson gson = new Gson();
		AuthenticationRequest request = new AuthenticationRequest("triquetrx", "zaid");
		String json = gson.toJson(request);
		mvc.perform(post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(401))
		.andExpect(jsonPath("$", is("INVALID_CREDENTIALS")));
	}
	
	//Token validity test
	@Test
	public String testTokenLogin() throws Exception {
		Gson gson = new Gson();
		AuthenticationRequest request = new AuthenticationRequest("triquetrx", "zaidkhan");
		String json = gson.toJson(request);
		MvcResult result = mvc.perform(post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(200))
		.andExpect(jsonPath("$.name", is("Zaid Khan")))
		.andReturn();
		
		return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
	}
	
	//Valid token test
	@Test
	public void testLoginValidToken() throws Exception{
		mvc.perform(get("/validate")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer "+testTokenLogin())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.validStatus",is(true)));
	}
	
	//InValid token test
	@Test
	public void testLoginInValidToken() throws Exception{
		mvc.perform(get("/validate")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer "+testTokenLogin()+"xyz")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(401));
	}
	
	//change with correct password test
	@Test
	public void testChangePassword() throws Exception{
		PasswordChangeDTO changeDTO = new PasswordChangeDTO("zaidkhan", "zaidkhan");
		Gson gson = new Gson();
		String json = gson.toJson(changeDTO);
		mvc.perform(post("/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.header("Authorization", "Bearer "+testTokenLogin())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$",is("CHANGED_PASSWORD_SUCCESSFULLY")));
	}
	
	//change with incorrect password test
	@Test
	public void testChangePasswordIncorrect() throws Exception{
		PasswordChangeDTO changeDTO = new PasswordChangeDTO("zaidkh", "zaidkhan");
		Gson gson = new Gson();
		String json = gson.toJson(changeDTO);
		mvc.perform(post("/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.header("Authorization", "Bearer "+testTokenLogin())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$",is("PASSWORD_NOT_A_MATCH")));
	}
	
	//check correct password test
	@Test
	public void testCheckCorrectPassword() throws Exception{
		ConfirmPasswordDTO changeDTO = new ConfirmPasswordDTO("zaidkhan");
		Gson gson = new Gson();
		String json = gson.toJson(changeDTO);
		mvc.perform(post("/check-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.header("Authorization", "Bearer "+testTokenLogin())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$",is(true)));
	}
	
	//check incorrect password test
	@Test
	public void testCheckInCorrectPassword() throws Exception{
		ConfirmPasswordDTO changeDTO = new ConfirmPasswordDTO("zaidkh");
		Gson gson = new Gson();
		String json = gson.toJson(changeDTO);
		mvc.perform(post("/check-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.header("Authorization", "Bearer "+testTokenLogin())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(403))
		.andExpect(jsonPath("$",is("PASSWORD_NOT_A_MATCH")));
	}
	
	
	
}
