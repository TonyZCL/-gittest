package cn.itcast.solr;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/*.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 新增或更新solr中的数据
     */
    @Test
    public void addOrUpdate(){
        TbItem item = new TbItem();
        item.setId(123L);
        item.setTitle("vivo Z1 新一代全面屏AI双摄游戏手机 6GB+64GB 瓷釉黑 移动联通电信全网通4G手机");
        item.setPrice(new BigDecimal(1498));
        item.setCategory("手机");
        item.setSeller("vivo");
        item.setBrand("vivo");
        item.setImage("https://item.jd.com/7717260.html?jd_pop=99c505e8-9234-463c-b07f-a19fad244d7d&abt=0");

        solrTemplate.saveBean(item);
        solrTemplate.commit();

    }

    /**
     * 更加id删除solr中的数据
     */
    @Test
    public void deleteById(){
        solrTemplate.deleteById("123");

        solrTemplate.commit();
    }

    /**
     * 删除solr中的数据
     */
    @Test
    public void delete(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);

        solrTemplate.commit();
    }

    /**
     * 单条件查询数据
     */
    @Test
    public void queryForPage(){
        //contains 不再对内容进行分词；
        Criteria criteria = new Criteria("item_title").contains("新一代");

        SimpleQuery query = new SimpleQuery();
        query.addCriteria(criteria);

        //设置分页信息；设置起始索引号，默认为0
        query.setOffset(0);
        //页大小；默认为10
        query.setRows(20);

        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        show(scoredPage);
    }

    /**
     * 多条件查询数据；条件之间是并列 也就是AND
     */
    @Test
    public void multiQueryForPage(){
        SimpleQuery query = new SimpleQuery();

        //contains 不再对内容进行分词；
        Criteria criteria = new Criteria("item_title").contains("新一代");
        query.addCriteria(criteria);

        Criteria criteria2 = new Criteria("item_category").contains("手机");
        query.addCriteria(criteria2);

        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        show(scoredPage);
    }

    /**
     * 输出查询结果
     * @param scoredPage
     */
    private void show(ScoredPage<TbItem> scoredPage) {
        System.out.println("本次查询出来的总记录数为：" + scoredPage.getTotalElements());
        System.out.println("本次查询出来的总页数为：" + scoredPage.getTotalPages());
        for (TbItem tbItem : scoredPage.getContent()) {
            System.out.println(" id = " + tbItem.getId());
            System.out.println(" title = " + tbItem.getTitle());
            System.out.println(" price = " + tbItem.getPrice());
            System.out.println(" seller = " + tbItem.getSeller());
            System.out.println(" brand = " + tbItem.getBrand());
            System.out.println(" category = " + tbItem.getCategory());
            System.out.println(" image = " + tbItem.getImage());
        }
    }
}
