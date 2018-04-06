package com.touzhijia;

import com.touzhijia.remote.ApiService;
import com.touzhijia.remote.RetrofitClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Response;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApiAutoApplicationTests {

	@Test
	public void testGet(){
		ApiService apiService = RetrofitClient.getClient("http://a.io.tzj.net/", ApiService.class);
		try {
			Response<String> response = apiService.get("new.partner.svc/api/partner_applys/SLDyeDAYQ/verify").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPost(){
		String json = "{\"telephone\": \"13803623948\",\"password\": \"cxl111111\",\"device\": \"pc\",\"platform\": \"touzhijia\",\"clientIP\": \"10.255.1.112\"}" ;
		ApiService apiService = RetrofitClient.getClient("http://a.io.tzj.net/", ApiService.class);

		try {
			Response<String> response = apiService.post("user_account.svc/api/accounts",json).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDouBan(){
		ApiService apiService = RetrofitClient.getClient("https://api.douban.com/", ApiService.class);
		try {
			Response<String> response = apiService.get("v2/book/1220562").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
