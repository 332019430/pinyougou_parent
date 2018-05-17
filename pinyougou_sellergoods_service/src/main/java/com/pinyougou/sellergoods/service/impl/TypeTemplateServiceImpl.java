package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }


        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        //查找所有的模板
        List<TbTypeTemplate> all = findAll();
        for (TbTypeTemplate tbTypeTemplate : all) {
            //这个模板关联的品牌[{"id":1,"text":"联想"},{"id":3,"text":"三星"},{"id":2,"text":"华为"},{"id":5,"text":"OPPO"},{"id":4,"text":"小米"},{"id":9,"text":"苹果"},{"id":8,"text":"魅族"},{"id":6,"text":"360"},{"id":10,"text":"VIVO"},{"id":11,"text":"诺基亚"},{"id":12,"text":"锤子"}]
            String brandIds = tbTypeTemplate.getBrandIds();
            List<Map> brandList = JSON.parseArray(brandIds, Map.class);

            redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);
            //通过模板的ID查询出模板的规格数组[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            List<Map> specList = selectTypeTemplateSpecList(tbTypeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);

        }
        System.out.println("缓存模板对应的品牌和规格");
        return new PageResult(page.getTotal(), page.getResult());
    }



    @Override
    public List<Map> selectTypeTemplate() {
        List<TbTypeTemplate> tbTypeTemplates = typeTemplateMapper.selectByExample(null);
        ArrayList<Map> tbTypeTemplateList = new ArrayList<>();
        for (TbTypeTemplate tbTypeTemplate : tbTypeTemplates) {
            Map map = new HashMap<>();
            map.put("id", tbTypeTemplate.getId());
            map.put("text", tbTypeTemplate.getName());
            tbTypeTemplateList.add(map);
        }
        return tbTypeTemplateList;
    }

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<Map> selectTypeTemplateSpecList(Long id) {
        //通过模板的ID查找模板
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        //获得模板的规格ID
        String specIds = tbTypeTemplate.getSpecIds();
        //把JSON字符串转换成List<map>
        //[
        //{"id":27,"text":"网络制式"},{"id":32,"text":"机身内存"}
        //]

        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //遍历MAP，通过规格名称查找规格选项
        for (Map map : maps) {
            //通过规格id值查找规格选项结合
            Integer specId = (Integer) map.get("id");
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(Long.valueOf(specId));
            List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
            map.put("options", tbSpecificationOptions);
            //{"id":27,"text":"网络制式","options":["16G","32G"]}
        }

        return maps;
    }

}
