package ks.sequoia.impl;

import ks.sequoia.aware.CacheServiceAware;
import ks.sequoia.bobj.DomainBObj;
import ks.sequoia.eobj.DomainEObj;
import ks.sequoia.eobj.LRU;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public abstract class AbstractCacheServiceImpl implements CacheServiceAware {
    protected static final int INITIAL_CAPACITY = 10240;

    protected static final int DEFAULT_FACTOR  = 6;

    protected static final int TOTAL = 8;

    protected static final int SPAN = 4;

    //2^6
    final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '-', '_'};

    @Resource
    private DomainBObj domainBObj;
    /**
     * 长域名的内存存储对象,初始化容量是10240W,避免内存移除,保存热点数据
     */
    protected Map<String, DomainEObj> longMappingMap = new ConcurrentHashMap<>(INITIAL_CAPACITY);
    /**
     *  短域名的内存存储对象,初始化容量是1W,避免内存移除,保存热点数据
     */
    protected Map<String, DomainEObj> shortMappingMap = new ConcurrentHashMap<>(INITIAL_CAPACITY);

    /**
     * LRU队列 ,当内存不足以写入新数据时候，移除最近少使用的key
     *
     */
    protected LRU<Long,DomainEObj> lruList = new LRU<>(INITIAL_CAPACITY);

    private void init(){
      List<DomainEObj> domainEObjList =  domainBObj.queryLatest10000Times();
      if(domainEObjList == null || domainEObjList.size() == 0){
          return;
      }
      for(DomainEObj domainEObj : domainEObjList){
          lruList.put(domainEObj.getDomainId(),domainEObj);
          addCache(domainEObj.getLongDomain(),domainEObj,longMappingMap);
          addCache(domainEObj.getShortDomain(),domainEObj,shortMappingMap);
      }
    }
    //短域名长度最大为 8 个字符
    protected DomainEObj transformShortDomain(String longDomain) {
       DomainEObj domainEObj =  longMappingMap.get(longDomain);
       //使用HashCode方法和equals生成短域名
       if(domainEObj == null){
           //得到longDomain的Hash值，^和>>>减少Hash碰撞
           StringBuffer sb = new StringBuffer();
           int pos = hash(longDomain) & DEFAULT_FACTOR;//元素的第一个位置
           sb.append(digits[pos]);
           if(longDomain.length() < 8){
               char[] chars = longDomain.toCharArray();
               for(char c : chars){
                  int p  =  hash(c) & DEFAULT_FACTOR;
                   sb.append(digits[p]);
               }
           }else {
                 int hashCode = longDomain.hashCode();
                 //将hashCode一分为8，最后一位参与^运算，减少hash碰撞
                 for(int ind = 1 ;ind <= TOTAL ;ind++){
                     int cal = 1>>>(ind * SPAN) ^(hashCode>>>(TOTAL * SPAN));
                     int p = cal & DEFAULT_FACTOR;
                     sb.append(digits[p]);
               }
           }
           return addDomainEObj(longDomain,sb.toString());
       }
       return domainEObj;
    }

    public static void main(String[] args) {
        System.out.println(8/7);
    }

    private DomainEObj addDomainEObj(String longDomain,String shortDomain){
        DomainEObj domainEObj = new DomainEObj();
        domainEObj.setLongDomain(longDomain);
        domainEObj.setShortDomain(shortDomain);
        domainEObj.setCreateTime(new Timestamp(System.currentTimeMillis()));
        domainEObj.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        this.getDomainBObj().addEObj(domainEObj);
        return domainEObj;
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    // 将字符串转换成二进制字符串，以空格相隔
    private String StrToBin(String str) {
        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]);
        }
        return result;
    }


    private void addCache(String flag,DomainEObj domainEObj,Map<String,DomainEObj> cacheMap){
         if(cacheMap.get(flag) == null){
             cacheMap.put(flag,domainEObj);
         }else {
             throw new RuntimeException("repeat domain name["+flag+"]");
         }
    }

    public DomainBObj getDomainBObj() {
        return domainBObj;
    }

    public void setDomainBObj(DomainBObj domainBObj) {
        this.domainBObj = domainBObj;
    }
}
