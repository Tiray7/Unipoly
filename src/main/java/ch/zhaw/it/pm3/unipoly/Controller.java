package ch.zhaw.it.pm3.unipoly;

import org.apache.logging.log4j.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
@RestController
@Configuration
public class Controller {

	private UnipolyApp unipoly;
	private static final Logger controllerMcLogger = LogManager.getLogger(Controller.class);


	public static void main(String[] args) {
		SpringApplication.run(Controller.class, args);
	}

	public Controller(UnipolyApp unipoly) {
		this.unipoly = unipoly;
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
	public UnipolyApp join(@RequestParam String name, @RequestParam Token.TokenType token) throws FieldIndexException {
		unipoly.join(name, token);
		return unipoly;
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp start(@RequestParam UnipolyApp.Gamemode gamemode, @RequestParam int npcnum) throws FieldIndexException {
		unipoly.start(gamemode, npcnum);
		return unipoly;
	}

	@RequestMapping(value = "/resetgame", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp resetGame() {
		unipoly = new UnipolyApp();
		return unipoly;
	}

	@RequestMapping(value = "/rolldice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp rollDice(@RequestParam int firstDice) throws FieldIndexException {
		unipoly.rollDice(firstDice);
		return unipoly;
	}

	@RequestMapping(value = "/rolltwodice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp rollTwoDice() throws FieldIndexException {
		unipoly.rollTwoDice();
		return unipoly;
	}

	@RequestMapping(value = "/endturn", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp endTurn() {
		unipoly.switchPlayer();
		return unipoly;
	}

	@RequestMapping(value = "/checkfieldoptions", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp checkFieldOptions() throws FieldIndexException {
		unipoly.checkFieldOptions();
		return unipoly;
	}

	@RequestMapping(value = "/jumpplayer", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp jumpPlayer(@RequestParam int FieldIndex) throws FieldIndexException {
		unipoly.jumpPlayer(FieldIndex);
		return unipoly;
	}

	@RequestMapping(value = "/userwantstobuy", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp userWantsToBuy() {
		unipoly.buyProperty();
		return unipoly;
	}

	@RequestMapping(value = "/paydetentionransom", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp payDetentionRansom() throws FieldIndexException {
		unipoly.payDetentionRansom();
		return unipoly;
	}

	@RequestMapping(value = "/leavedetention", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp leaveDetention() throws FieldIndexException {
		unipoly.leaveDetention();
		return unipoly;
	}

	@RequestMapping(value = "/payoffdebt", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UnipolyApp payOffDebt(@RequestParam int[] FieldIndexes) throws FieldIndexException {
		unipoly.payOffDebt(FieldIndexes);
		return unipoly;
	}
}