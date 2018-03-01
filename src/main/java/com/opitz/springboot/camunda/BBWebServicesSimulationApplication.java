package com.opitz.springboot.camunda;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Class contains logic for simulating web services
 * @author JUR
 *
 */
@SpringBootApplication
@EnableWebFlux
public class BBWebServicesSimulationApplication {

	// assign values from application.properties
	
	@Value("${strings.get_vm_response}")
	private String getVmResponse;

	@Value("${strings.get_vm_id_response}")
	private String getVmWithIdResponse;

	@Value("${strings.post_vm_response}")
	private String postVmResponse;

	@Value("${strings.post_infoblox_response}")
	private String postInfobloxResponse;

	@Value("${strings.get_serverList_response_xml}")
	private String getServerListResponse;

	@Value("${strings.post_session_response}")
	private String postSessionResponse;

	/**
	 * Main 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BBWebServicesSimulationApplication.class, args);
	}
	
	/**
	 * Returns routing data for web server - 
	 * @return
	 */
	@Bean
	RouterFunction<?> routes() {
		
		return RouterFunctions.route(
							RequestPredicates.GET("/rest/vcenter/vm"), 
							request -> { 
								System.out.println(" ** GET /rest/vcenter/vm"); 
								
								// log received body and return content of getVmResponse
								request.bodyToMono(String.class).subscribe(body -> System.out.println(" ** received body: " + body)); 
								return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.<String>just(getVmResponse), String.class); 
							} )
					.andRoute(
							RequestPredicates.GET("/rest/vcenter/vm/{id}"), 
							request -> { 
								String id = request.pathVariable("id"); 
								System.out.println(" ** GET /rest/vcenter/vm/" + id); 
								
								// log received body and return content of getVmWithIdResponse with replacement of vmid and last 2 digits of mac address with id input parameter
								request.bodyToMono(String.class).subscribe(body -> System.out.println(" ** received body: " + body)); 
								return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.<String>just(getVmWithIdResponse.replaceAll("__", id).replaceAll("--", id.substring(id.length()-2, id.length()))), String.class); 
							})
					.andRoute(
							RequestPredicates.POST("/rest/com/vmware/cis/session"), 
							request -> { 
								System.out.println(" ** POST /rest/com/vmware/cis/session"); 
								
								// log received body and return content of postSessionResponse with replacement of __ (cis) with random generated UUID
								request.bodyToMono(String.class).subscribe(body -> System.out.println(" ** received body: " + body)); 
								return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.<String>just(postSessionResponse.replaceAll("__", UUID.randomUUID().toString())), String.class); 
							})
					.andRoute(
							RequestPredicates.POST("/rest/vcenter/vm"), 
							request -> { 
								System.out.println(" ** POST /rest/vcenter/vm");
								
								// log received body and return content of postVmResponse with replacement of __ (vmid) with random generated integer
								request.bodyToMono(String.class).subscribe(body -> System.out.println(" ** received body: " + body)); 
								return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.<String>just(postVmResponse.replaceAll("__", "" + (new Random().nextInt(1000) + 1) )), String.class); 
							})
					.andRoute(
							RequestPredicates.POST("/wapi/v1.2/record:host"), 
							request -> { 
								System.out.println(" ** POST /wapi/v1.2/record:host"); 

								// log received body and return content of postInfobloxResponse with replacement of __ (IP) with random generated integer
								request.bodyToMono(String.class).subscribe(body -> System.out.println(" ** received body: " + body)); 
								return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.<String>just(postInfobloxResponse.replaceAll("__", "" + (new Random().nextInt(255) + 1) )), String.class); 
							})
					.andRoute(
							RequestPredicates.GET("/serverlist"), 
							request -> { 
								System.out.println(" ** GET /serverlist"); request.bodyToMono(String.class).subscribe(body -> System.out.println(" ** received body: " + body)); 
								return ServerResponse.ok().contentType(MediaType.APPLICATION_XML).body(Mono.<String>just(getServerListResponse), String.class); 
							});
	}
	
}
