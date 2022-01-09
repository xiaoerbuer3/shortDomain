package ks.sequoia;


import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Slf4j
@SpringBootTest(classes = Application.class)
@Transactional(transactionManager = "transactionManager")
public class DomainTest {


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;


    @Before
    public void before() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();   //构造MockMvc
    }
    @Test
    public void testLongDomain() throws Exception {
        // Get发送请求
        String longDomain = "";
        String url = "/ks/queryEObjByLongDomain?longDomain" +longDomain;
        ResultActions resultActions = this.mockMvc
                .perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("客户端获的数据:" + result);
    }

    @Test
    public void testShortDomain() throws Exception {
        // Get发送请求
        String shortDomain = "";
        String url = "/ks/queryEObjByShortDomain?shortDomain" +shortDomain;
        ResultActions resultActions = this.mockMvc
                .perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("客户端获的数据:" + result);
    }
}
