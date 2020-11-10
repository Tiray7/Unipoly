package ch.zhaw.it.pm3.unipoly;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerTests {

	@Autowired
	private Controller controller;

	@Test
	public void contextLoads() {
		assertThat(controller).isNotNull();
	}

	@Test
	public void getIndexHtml() {
		assertEquals("index.html", controller.get().getFilename());
	}

	@Test
	public void getState() {
		UnipolyApp unipolyApp = new UnipolyApp();
		//assertEquals(unipolyApp, controller.getState());
	}
}




