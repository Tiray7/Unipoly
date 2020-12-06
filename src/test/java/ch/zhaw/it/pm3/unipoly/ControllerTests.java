

/*
package ch.zhaw.it.pm3.unipoly;

import ch.zhaw.it.pm3.unipoly.Controller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

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

	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private Controller controller;

	@Before
	public void init() {
		mvc = MockMvcBuilders.standaloneSetup(controller)
				.build();
	}

	@Test
	public void shouldReturnUnipolyState() throws Exception {
		MockHttpServletResponse response = mvc.perform(
				get("/state")
						.accept(MediaType.ALL))
				.andExpect(status().isAccepted())
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
	}

	@Test
	public void shouldJoinPlayerAndReturnUnipolyState() throws Exception {
		MockHttpServletResponse response = mvc.perform(
				get("/join")
						.accept(MediaType.ALL))
				.andExpect(status().isAccepted())
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
	}

	@Test
	public void shouldResetGameAndReturnNewUnipolyInstance() throws Exception {
		MockHttpServletResponse response = mvc.perform(
				get("/resetgame")
						.accept(MediaType.ALL))
				.andExpect(status().isAccepted())
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
	}
}
*/




