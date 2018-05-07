package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.search.controller
 * @date 2018/5/6/0006
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference( timeout = 3000)
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map search(@RequestBody Map searchMap){
        System.out.println(searchMap.get("keywords"));
        Map map = itemSearchService.search(searchMap);
        return map;
    }
}
