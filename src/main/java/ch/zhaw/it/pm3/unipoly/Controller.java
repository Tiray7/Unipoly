package ch.zhaw.it.pm3.unipoly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@SpringBootApplication
@RestController
public class Controller {

	private UnipolyApp unipoly;
	private static final Logger unipolyLogger = LogManager.getLogger(Controller.class);


	public static void main(String[] args) {
		SpringApplication.run(Controller.class, args);
	}

	@Autowired
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
	public ResponseEntity<UnipolyApp> getState() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/join", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> join(@RequestParam(value="name",required = false) String name,
										   @RequestParam(value="token",required = false) Token.TokenType token) throws FieldIndexException, IOException {
		unipoly.join(name, token);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> start(@RequestParam(value="gamemode", required = false) UnipolyApp.Gamemode gamemode,
											@RequestParam(value="npcnum", required = false) Integer npcnum) throws FieldIndexException, IOException {
		unipoly.start(gamemode, npcnum);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/resetgame", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> resetGame() throws IOException {
		unipoly = new UnipolyApp();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/rolldice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> rollDice(@RequestParam(value="firstDice",required = false) Integer firstDice) throws FieldIndexException, IOException {
		unipoly.rollDice(firstDice);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/rolltwodice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> rollTwoDice() throws IOException {
		unipoly.rollTwoDice();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/endturn", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> endTurn() throws IOException, FieldIndexException {
		unipoly.switchPlayer();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/checkfieldoptions", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> checkFieldOptions() throws FieldIndexException, IOException {
		unipoly.checkFieldOptions();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/jumpplayer", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> jumpPlayer(@RequestParam(value="FieldIndex",required = false) Integer FieldIndex) throws FieldIndexException, IOException {
		unipoly.jumpPlayer(FieldIndex);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/userwantstobuy", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> userWantsToBuy() throws IOException {
		unipoly.buyProperty();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/paydetentionransom", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> payDetentionRansom() throws IOException {
		unipoly.payDetentionRansom();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/leavedetention", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> leaveDetention() throws IOException {
		unipoly.leaveDetention();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/payoffdebt", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> payOffDebt(@RequestParam(value="FieldIndexes",required = false) int[] FieldIndexes) throws FieldIndexException, JsonProcessingException {
		unipoly.payOffDebt(FieldIndexes);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}
}