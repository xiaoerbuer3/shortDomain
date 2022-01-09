package ks.sequoia;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ks.sequoia.bobj.DomainBObj;
import ks.sequoia.eobj.DomainEObj;
import ks.sequoia.eobj.LRU;
import ks.sequoia.utils.IdFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.sql.Timestamp;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Slf4j
@SpringBootTest(classes = Application.class)
@Data
@Transactional(transactionManager = "transactionManager")
public class IdFactoryTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;
    @Resource
    private DomainBObj domainBObj;


    @Before
    public void before() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();   //构造MockMvc
    }
    @Test
    public void testIdFactory(){
        IdFactory idFactory = new IdFactory();
        idFactory.nextId();
        idFactory.getDatacenterId();
        idFactory.getWorkerId();
        idFactory.getTimestamp();
        idFactory.tilNextMillis(System.currentTimeMillis());
        idFactory.timeGen();
    }

    @Test
    public void testLRU(){
        LRU<Long, DomainEObj> lru = new LRU<>(100);
        lru.size();
        DomainEObj domainEObj = new DomainEObj();
         domainEObj.setLongDomain("11");
        domainEObj.setShortDomain("22");
        domainEObj.setCreateTime(new Timestamp(System.currentTimeMillis()));
        domainEObj.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        this.getDomainBObj().addEObj(domainEObj);
        lru.put(123L,domainEObj);
        lru.get(123L);
        lru.getHead();
        lru.remove(123L);
        lru.size();
        lru.contain(123L);
        lru.toString();
    }
}
