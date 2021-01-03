package com.forschung.projektdij;

import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProjektdijApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjektdijApplication.class, args);
	}


@GetMapping("/map")
public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
return String.format("Hello %s!", name);
}

@Bean
CommandLineRunner init(NodeRepository nodeRepository) {
	return args -> {
		Stream.of(22.1, 33.9, 32.1, 31.34).forEach(x -> {
			Nodes node = new Nodes(x, 23.24);
			nodeRepository.save(node);
		});
		nodeRepository.findAll().forEach(System.out::println);
	};
}

}
