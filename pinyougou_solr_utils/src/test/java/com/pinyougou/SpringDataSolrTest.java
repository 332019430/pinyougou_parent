package com.pinyougou;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou
 * @date 2018/5/6/0006
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SpringDataSolrTest {
    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void add() {
        TbItem tbItem = null;

        tbItem = new TbItem();
        tbItem.setId(new Long(10001));
        tbItem.setTitle("钛灰色");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();

    }

    @Test
    public void delete() {
        solrTemplate.deleteById("test001");
        solrTemplate.commit();
    }

    @Test
    public void query() {
        Query query = new SimpleQuery("*:*");
        query.setOffset(1);
        query.setRows(2);
        /*Criteria criteria = new Criteria("item_title");
        criteria.is("钛灰色");
        query.addCriteria(criteria);*/
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println(tbItems.getTotalElements());
        List<TbItem> content = tbItems.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getId() + ":" + tbItem.getTitle());
        }
    }
}
