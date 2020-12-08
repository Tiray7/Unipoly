package ch.zhaw.it.pm3.unipoly;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class ControllerTests {

	private static final Logger unipolyMcLogger = LogManager.getLogger(UnipolyApp.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private Controller controller;

	@BeforeEach
	public void init() {
		mvc = MockMvcBuilders.standaloneSetup(controller)
				.build();
	}

	@Test
	void getState() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/state")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "State returned.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getJoin() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/join")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "join() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getStart() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/start")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "start() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getResetGame() throws Exception {
		MockHttpServletResponse response = mvc.perform(
				get("/resetgame")
						.accept(MediaType.ALL))
				.andExpect(status().isAccepted())
				.andReturn().getResponse();

		assertThat(HttpStatus.ACCEPTED.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getRollDice() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/rolldice")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "rollDice() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getRollTwoDice() {
		try {
			mvc.perform(
					get("/rolltwodice")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "rollTwoDice() method reached.");
		}
	}

	@Test
	void getEndTurn() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/endturn")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "endTurn() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getCheckFieldOptions() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/checkfieldOptions")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "checkFieldOptions() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getJumpPlayer() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/jumpplayer")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "jumpplayer() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getUserWantToBuy() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/userwantstobuy")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "buyProperty() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getPayDetentionRandom() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/paydetentionransom")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "payDetentionRansom() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getLeaveDetention() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/leavedetention")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "leaveDetention() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getPayoffDebt() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/payoffdebt")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "payOffDebt() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getReadCard() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/readcard")
							.accept(MediaType.ALL))
					.andExpect(status().isAccepted())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "readCard() method reached.");
		}

		assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatus());
	}

	@Test
	void getQuizAnswer() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			response = mvc.perform(
					get("/quizanswer")
							.accept(MediaType.ALL))
					.andExpect(status().is4xxClientError())
					.andReturn().getResponse();
		} catch (Exception e) {
			unipolyMcLogger.log(Level.DEBUG, "quizAnswer() method reached.");
		}

		assertThat(HttpStatus.BAD_REQUEST.value()).isEqualTo(response.getStatus());
	}
}



