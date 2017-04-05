package rest;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import google.RedirectUrlListener;
import google.SheetsAuthorizer;

@Path("gauth")
public class GoogleAuth implements RedirectUrlListener {
	CountDownLatch latch = new CountDownLatch(1);
	RedirectUrlListener self = this;
	private String url ="/";
//	private String callbackUrl = "localhost";
	private String callbackUrl = "ec2-34-250-23-172.eu-west-1.compute.amazonaws.com";
	  
	@GET
	public Response getAuthLink(){
		startAuthFlow(callbackUrl);
		try {
			latch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URI uri = UriBuilder.fromUri(url).build();
		return Response.seeOther(uri).build();
		
	}

	private void startAuthFlow(String callbackUrl) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					System.out.println("invoking sheetsAuthorizer");
					SheetsAuthorizer.getSheetsService(self, callbackUrl);
					
				} catch (IOException e) {
					
				}
			}
		}).start();
		
	}

	@Override
	public void notifyUrl(String url) {
		this.url =url;
		latch.countDown();
		
	}
}
