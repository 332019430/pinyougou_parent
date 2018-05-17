package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import com.pinyougou.utils.PinyougouContants;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 首页轮播图
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		try {
			redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		contentMapper.insert(content);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		try {
			//获取原本的文本的类型ID
			Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
			if (content.getCategoryId()!=categoryId.longValue()){
				redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(categoryId);
			}
			redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			contentMapper.deleteByPrimaryKey(id);
			try {
				TbContent tbContent = contentMapper.selectByPrimaryKey(id);
				redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(tbContent.getCategoryId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
				
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public List<TbContent> findContentListByCategoryId(Long categoryId) {

		try {
			List<TbContent> contents = (List)redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).get(categoryId);
			if(contents!=null && contents.size()>0){
				System.out.println("从缓存中获取");
				return contents;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TbContentExample exmaple = new TbContentExample();
		exmaple.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
		exmaple.setOrderByClause("sort_order");//order by sor_order asc 升序排列
		List<TbContent> contents = contentMapper.selectByExample(exmaple);
		System.out.println("从数据库中获取");
		try {

			redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).put(categoryId,contents);
			System.out.println("把数据放进缓存");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}

}
