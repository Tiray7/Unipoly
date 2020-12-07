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

/**
 * This class resembles at first the main entry point into the Unipoly application
 * and second contains the controller for the spring web application context.
 * The controller receives a GET-request from then frontend and answers
 * accordingly either with a resource in the beginning or with a {@link ResponseEntity}
 * containing the {@link UnipolyApp} in JSON-format in the responses body.
 * Also the responses body gets logged via a {@link Logger} instance into "C:/temp/unipoly.log".
 * The processing to a {@link String} instance gets done with an {@link ObjectMapper}.
 */
@SpringBootApplication
@RestController
public class Controller {

	protected UnipolyApp unipoly;
	private static final Logger unipolyLogger = LogManager.getLogger(Controller.class);

	/**
	 * Starting point of the {@link SpringApplication}
	 * @param args feasible arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Controller.class, args);
	}

	/**
	 * Constructor for the Controller
	 * @param unipoly instance of the UnipolyApp to have a game initialized
	 * @Autowired  so spring can do all the work :)
	 */
	@Autowired
	public Controller(UnipolyApp unipoly) {
		this.unipoly = unipoly;
	}

	/**
	 * Starting point for the frontend to retrieve the frontpage
	 * @return starting page of the web application
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public FileSystemResource get() {
		return getHttp("index.html");
	}

	/**
	 * Gets a filename as parameter and creates a FileSystemRessource which
	 * on requesting gets returned
	 * @param fileName filename received from get()
	 * @return instance of the required file
	 */
	@RequestMapping(value = "/{file_name:.+}", method = RequestMethod.GET)
	@ResponseBody
	public FileSystemResource getHttp(@PathVariable("file_name") String fileName) {
		return new FileSystemResource("http/" + fileName);
	}


	/**
	 * Logs the requested state of the {@link UnipolyApp} and answers accordingly with
	 * a {@link ResponseEntity} containing the unipoly as json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/state", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> getState() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @param name of the player
	 * @param token chosen by the player
	 * @return response containing the unipoly as json in the body
	 * @throws FieldIndexException if something's wrong with the index
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/join", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> join(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "token", required = false) Token.TokenType token)
			throws FieldIndexException, IOException {
		unipoly.join(name, token);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @param gamemode chosen by the player
	 * @param npcnum amount of npcs chosen by the player
	 * @return response containing the unipoly as json in the body
	 * @throws FieldIndexException if something's wrong with the index
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> start(
			@RequestParam(value = "gamemode", required = false) UnipolyApp.Gamemode gamemode,
			@RequestParam(value = "npcnum", required = false) Integer npcnum) throws FieldIndexException, IOException {
		unipoly.start(gamemode, npcnum);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Creates a new {@link UnipolyApp} instance and overwrites the previous one, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/resetgame", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> resetGame() throws IOException {
		unipoly = new UnipolyApp();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @param firstDice chosen by the player
	 * @return response containing the unipoly as json in the body
	 * @throws FieldIndexException if something's wrong with the index
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/rolldice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> rollDice(@RequestParam(value = "firstDice", required = false) Integer firstDice)
			throws FieldIndexException, IOException {
		unipoly.rollDice(firstDice);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/rolltwodice", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> rollTwoDice() throws IOException {
		unipoly.rollTwoDice();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with the server or client
	 * @throws FieldIndexException if something's wrong with the index
	 */
	@RequestMapping(value = "/endturn", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> endTurn() throws IOException, FieldIndexException {
		unipoly.switchPlayer();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with the server or client
	 * @throws FieldIndexException if something's wrong with the index
	 */
	@RequestMapping(value = "/checkfieldoptions", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> checkFieldOptions() throws FieldIndexException, IOException {
		unipoly.checkFieldOptions();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @param FieldIndex of the chosen field by the player
	 * @return response containing the unipoly as json in the body
	 * @throws FieldIndexException if something's wrong with the index
	 * @throws IOException if something goes horribly wrong with the server or client
	 */
	@RequestMapping(value = "/jumpplayer", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> jumpPlayer(
			@RequestParam(value = "FieldIndex", required = false) Integer FieldIndex)
			throws FieldIndexException, IOException {
		unipoly.jumpPlayer(FieldIndex);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with server or client
	 */
	@RequestMapping(value = "/userwantstobuy", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> userWantsToBuy() throws IOException {
		unipoly.buyProperty();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with server or client
	 */
	@RequestMapping(value = "/paydetentionransom", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> payDetentionRansom() throws IOException, FieldIndexException {
		unipoly.payDetentionRansom();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws IOException if something goes horribly wrong with server or client
	 */
	@RequestMapping(value = "/leavedetention", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> leaveDetention() throws IOException, FieldIndexException {
		unipoly.leaveDetention();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @param fieldIndices is the index of the field to pay debt for
	 * @return response containing the unipoly as json in the body
	 * @throws JsonProcessingException if something goes wrong while processing the unipoly to json
	 */
	@RequestMapping(value = "/payoffdebt", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> payOffDebt(
			@RequestParam(value = "fieldIndices", required = false) Integer[] fieldIndices)
			throws JsonProcessingException, FieldIndexException {
		unipoly.payOffDebt(fieldIndices);
		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @return response containing the unipoly as json in the body
	 * @throws JsonProcessingException if something goes wrong while processing the unipoly to json
	 */
	@RequestMapping(value = "/readcard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> readCard() throws JsonProcessingException, FieldIndexException {
		unipoly.readCard();

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}

	/**
	 * Processes the requested method in the {@link UnipolyApp} application, then logs
	 * the current state after the processing via the {@link ObjectMapper} and returns
	 * a {@link ResponseEntity} containing the json in the body.
	 * @param x true if answer is correct, false if not
	 * @return response containing the unipoly as json in the body
	 * @throws JsonProcessingException if something goes wrong while processing the unipoly to json
	 */
	@RequestMapping(value = "/quizanswer", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<UnipolyApp> quizAnswer(@RequestParam boolean x) throws JsonProcessingException {
		unipoly.quizAnswer(x);

		ObjectMapper objectMapper = new ObjectMapper();
		unipolyLogger.log(Level.DEBUG, objectMapper.writeValueAsString(unipoly) + "\n");

		return new ResponseEntity<>(unipoly, HttpStatus.ACCEPTED);
	}
}