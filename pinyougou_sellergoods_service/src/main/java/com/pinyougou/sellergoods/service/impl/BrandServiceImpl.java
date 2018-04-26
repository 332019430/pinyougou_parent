package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    /**
     * 增加
     */
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbBrand brand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbBrandExample example = new TbBrandExample();
        Criteria criteria = example.createCriteria();

        if (brand != null) {
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        List<TbBrand> tbBrands = brandMapper.selectByExample(example);
        Page<TbBrand> page = (Page<TbBrand>) tbBrands;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectBrandList() {
        List<TbBrand> tbBrands = brandMapper.selectByExample(null);
        List<Map> list=new ArrayList<>();
        for (TbBrand tbBrand : tbBrands) {
            Map map = new HashMap<>();
            map.put("id",tbBrand.getId());
            map.put("text",tbBrand.getName());
            list.add(map);
        }
        return list;
    }

}
