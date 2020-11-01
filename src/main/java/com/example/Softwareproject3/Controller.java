package com.example.Softwareproject3;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@Configuration
public class Controller {

	private static UnipolyApp unipoly;

	public static void main(String[] args) {
		SpringApplication.run(Controller.class, args);
		unipoly = new UnipolyApp();
	}

	// @GetMapping("/softwareproject3")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public FileSystemResource get() {
		return getHttp("index.html");
	}

	@RequestMapping(value = "/{file_name:.+}", method = RequestMethod.GET)
	@ResponseBody
	public FileSystemResource getHttp(@PathVariable("file_name") String fileName) {
		return new FileSystemResource("http/" + fileName);
	}

	// Operations
	@RequestMapping(value = "/state", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp getState() {
		return unipoly;
	}

	@RequestMapping(value = "/join", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp join(@RequestParam String name, @RequestParam TokenType token) {
		unipoly.join(name, token);
		return unipoly;
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp start(@RequestParam Gamemode gamemode) {
		unipoly.start(gamemode);
		return unipoly;
	}

	@RequestMapping(value = "/resetgame", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp resetGame() {
		unipoly.resetGame();
		unipoly = new UnipolyApp();
		return unipoly;
	}

	@RequestMapping(value = "/rolldice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp rollDice(@RequestParam int firstDice) throws InterruptedException {
		unipoly.rollDice(firstDice);
		return unipoly;
	}

	@RequestMapping(value = "/greeting.html", method = RequestMethod.GET)
	@ResponseBody
	public String getGreeting() {
		return "Hey David";
	}

	@RequestMapping(value = "/endturn", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp endTurn() {
		unipoly.switchPlayer();
		return unipoly;
	}

}