package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojigroup.Goods;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsDescService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	@Override
	public void add(Goods goods) {
		//插入商品数据
		TbGoods goods1 = goods.getGoods();
		goods1.setAuditStatus("0");
		goodsMapper.insert(goods1);

		//插入商品描述
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(goods1.getId());
		tbGoodsDescMapper.insert(goodsDesc);

		if ("0".equals(goods1.getIsEnableSpec())){
			TbItem item = new TbItem();
			item.setTitle(goods1.getGoodsName());
			item.setPrice(goods1.getPrice());
			item.setNum(9999);
			item.setStatus("1");
			item.setIsDefault("1");
			item.setSpec("{}");

			String itemImages = goodsDesc.getItemImages();
			if (itemImages!=null){
				List<Map> imagesList = JSON.parseArray(itemImages, Map.class);
				item.setImage((String) imagesList.get(0).get("url"));
			}

			//设置三级类目ID
			item.setCategoryid(goods1.getCategory3Id());
			//设置三级类目的名称
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
			item.setCategory(tbItemCat.getName());

			item.setCreateTime(new Date());
			item.setUpdateTime(item.getCreateTime());
			//设置商品id
			item.setGoodsId(goods1.getId());
			//设置商家ID
			item.setSellerId(goods1.getSellerId());
			//设置商家店铺名称
			item.setSellerId(goods1.getGoodsName());
			//设置品牌名称
			item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());

			itemMapper.insert(item);
		}else {
			//插入商品sku列表
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				//设置标题
				String title = goods1.getGoodsName();
				String spec = item.getSpec();
				Map map = JSON.parseObject(spec, Map.class);
				for (Object o : map.keySet()) {
					title+=map.get(o)+" ";
				}
				item.setTitle(title);

				//设置图片路径
				String itemImages = goodsDesc.getItemImages();
				if(itemImages!=null){
					List<Map> imagesList = JSON.parseArray(itemImages, Map.class);
					item.setImage((String) imagesList.get(0).get("url"));
				}

				//设置三级类目ID
				item.setCategoryid(goods1.getCategory3Id());

				//设置三级类目名称
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
				item.setCategory(tbItemCat.getName());

				item.setCreateTime(new Date());
				item.setUpdateTime(item.getCreateTime());

				//设置此单件商品所属的商品ID
				item.setGoodsId(goods1.getId());

				//设置商家的ID
				item.setSellerId(goods1.getSellerId());

				//设置商家名
				item.setSeller(goods1.getGoodsName());

				item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());

				itemMapper.insert(item);
			}


		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		TbGoods goods1 = goods.getGoods();
		goods1.setAuditStatus("0");
		goodsMapper.updateByPrimaryKeySelective(goods1);

		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		tbGoodsDescMapper.updateByPrimaryKeySelective(goodsDesc);

		TbItemExample example =new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(goods1.getId());
		itemMapper.deleteByExample(example);

        if ("0".equals(goods1.getIsEnableSpec())){
            TbItem item = new TbItem();
            item.setTitle(goods1.getGoodsName());
            item.setPrice(goods1.getPrice());
            item.setNum(9999);
            item.setStatus("1");
            item.setIsDefault("1");
            item.setSpec("{}");

            String itemImages = goodsDesc.getItemImages();
            if (itemImages!=null){
                List<Map> imagesList = JSON.parseArray(itemImages, Map.class);
                item.setImage((String) imagesList.get(0).get("url"));
            }

            //设置三级类目ID
            item.setCategoryid(goods1.getCategory3Id());
            //设置三级类目的名称
            TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
            item.setCategory(tbItemCat.getName());

            item.setCreateTime(new Date());
            item.setUpdateTime(item.getCreateTime());
            //设置商品id
            item.setGoodsId(goods1.getId());
            //设置商家ID
            item.setSellerId(goods1.getSellerId());
            //设置商家店铺名称
            item.setSellerId(goods1.getGoodsName());
            //设置品牌名称
            item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());

            itemMapper.insert(item);
        }else {
            //插入商品sku列表
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                //设置标题
                String title = goods1.getGoodsName();
                String spec = item.getSpec();
                Map map = JSON.parseObject(spec, Map.class);
                for (Object o : map.keySet()) {
                    title+=map.get(o)+" ";
                }
                item.setTitle(title);

                //设置图片路径
                String itemImages = goodsDesc.getItemImages();
                if(itemImages!=null){
                    List<Map> imagesList = JSON.parseArray(itemImages, Map.class);
                    item.setImage((String) imagesList.get(0).get("url"));
                }

                //设置三级类目ID
                item.setCategoryid(goods1.getCategory3Id());

                //设置三级类目名称
                TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
                item.setCategory(tbItemCat.getName());

                item.setCreateTime(new Date());
                item.setUpdateTime(item.getCreateTime());

                //设置此单件商品所属的商品ID
                item.setGoodsId(goods1.getId());

                //设置商家的ID
                item.setSellerId(goods1.getSellerId());

                //设置商家名
                item.setSeller(goods1.getGoodsName());

                item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());

                itemMapper.insert(item);
            }


        }
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);

		TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItemList(tbItems);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}
	}


		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if(goods!=null){
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
		}

		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

}
