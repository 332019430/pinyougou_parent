package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.service.impl
 * @date 2018/5/6/0006
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService{
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, String> search(Map searchMap) {
        Map map = new HashMap<>();
        String keywords = (String) searchMap.get("keywords");
        System.out.println(keywords);
        Query query = new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_keywords");
        criteria.is(keywords);
        query.addCriteria(criteria);

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> tbItems = page.getContent();
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
        map.put("rows",tbItems);

        return map;
    }
}
