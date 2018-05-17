package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.service.impl
 * @date 2018/5/6/0006
 */
@Service(timeout = 6000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, String> search(Map searchMap) {
        Map resultMap = new HashMap<>();
        //高亮主查询
        Map map = searchHightList(searchMap);
        resultMap.putAll(map);

        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        //查找所有的商品分类
        if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
            Map map1 = searchBrandListAndSpecListByCategory((String) searchMap.get("category"));
            resultMap.putAll(map1);
        } else {
            if (categoryList != null && categoryList.size() > 0) {
                Map map1 = searchBrandListAndSpecListByCategory(categoryList.get(0));
                resultMap.putAll(map1);
            }
        }
        return resultMap;
    }

    private Map searchHightList(Map searchMap) {
        Map map = new HashMap<>();
        String keywords = (String) searchMap.get("keywords");
        keywords=keywords.replaceAll(" ","");
        HighlightQuery query = new SimpleHighlightQuery();

        if (keywords != null && keywords.length() > 0) {
            Criteria criteria = new Criteria("item_title");
            criteria.is(keywords);
            query.addCriteria(criteria);
        }
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);

        //设置过滤条件
        //设置商品分类过滤条件
        if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category");
            filterCriteria.is(searchMap.get("category"));
            FilterQuery addFilterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(addFilterQuery);
        }

        //设置品牌分类过滤条件
        if (searchMap.get("brand") != null && !"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand");
            filterCriteria.is(searchMap.get("brand"));
            FilterQuery addFilterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(addFilterQuery);
        }

        //设置品牌分类过滤条件
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map<String, String>) searchMap.get("spec");
            System.out.println("spec" + spec);
            for (String key : spec.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key);
                filterCriteria.is(spec.get(key));
                FilterQuery addFilterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(addFilterQuery);
            }
        }

        //设置分页查询
        Integer pageNum = (Integer) searchMap.get("pageNum");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=40;
        }
        query.setOffset(pageNum);
        query.setRows(pageSize);

        //设置价格分类查询
        String price = (String) searchMap.get("price");
        if (StringUtils.isNotBlank(price)){
            String[] split = price.split("-");
            Criteria priceCriteria = new Criteria("item_price");
            if (split[1].equals("*")){
                priceCriteria.greaterThanEqual(split[0]);
            }else{
                priceCriteria.between(split[0], split[1], true, true);
            }
            FilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
            query.addFilterQuery(filterQuery);
        }

        //设置排序
        String sortName = (String) searchMap.get("sortName");
        String sortType = (String) searchMap.get("sortType");
        if (StringUtils.isNotBlank(sortName)&&StringUtils.isNotBlank(sortType)){
            if (sortType.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortName);
                query.addSort(sort);
            }else {
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortName);
                query.addSort(sort);
            }
        }


        try {
            //高亮查询
            HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

            List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
            for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
                //获取正常的对象
                TbItem entity = tbItemHighlightEntry.getEntity();
                List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
                if (highlights != null && highlights.size() > 0 && highlights.get(0) != null && highlights.get(0).getSnipplets() != null && highlights.get(0).getSnipplets().size() > 0) {
                    entity.setTitle(highlights.get(0).getSnipplets().get(0));
                }
            }

            List<TbItem> tbItems = highlightPage.getContent();

            map.put("rows", tbItems);


            int totalPages = highlightPage.getTotalPages();


            map.put("totalPages",totalPages);
            long totalElements = highlightPage.getTotalElements();
            map.put("totalElements",totalElements);
            return map;
        } catch (Exception e) {

            e.printStackTrace();
            SimpleQuery simpleQuery = new SimpleQuery("*:*");
            ScoredPage<TbItem> page = solrTemplate.queryForPage(simpleQuery, TbItem.class);
            List<TbItem> tbItems = page.getContent();
            map.put("rows", tbItems);


            return  map;
        }


    }



    //查询分类
    public List<String> searchCategoryList(Map searchMap) {
        ArrayList<String> categoryList = new ArrayList<>();

        //简单查询
        SimpleQuery simpleQuery = new SimpleQuery("*:*");


        String keywords = (String) searchMap.get("keywords");
        if (keywords != null && keywords.length() != 0) {
            Criteria criteria = new Criteria("item_keywords");
            criteria.is(keywords);
            simpleQuery.addCriteria(criteria);
        }
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        simpleQuery.setGroupOptions(groupOptions);

        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(simpleQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            categoryList.add(tbItemGroupEntry.getGroupValue());
        }
        return categoryList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    public Map searchBrandListAndSpecListByCategory(String categoryName) {
        Map map = new HashMap<>();
        Long typeTempldateId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeTempldateId);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeTempldateId);
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }
}
