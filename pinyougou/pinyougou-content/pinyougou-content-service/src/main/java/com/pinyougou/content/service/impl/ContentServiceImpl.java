package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {
    //在redis中内容对应的key
    private static final String CATEGORY_CONTENT = "content";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList = null;
        try{
            //1、查询redis中是否存在数据，如果存在则直接返回；
            contentList = (List<TbContent>)redisTemplate.boundHashOps(CATEGORY_CONTENT).get(categoryId);
            if (contentList != null) {
                return contentList;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //-- 查询内容分类为轮播广告并且有效按照排序字段降序排序的广告内容。
        //select * from tb_content where category_id=1 and status='1' order by sort_order desc
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        //内容分类
        criteria.andEqualTo("categoryId",categoryId);
        //有效
        criteria.andEqualTo("status","1");
        //降序排序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);
        return contentList;
    }

    //新增广告后清除缓存
    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);

        //更新分类对应的缓存
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 更新分类对应的缓存
     * @param categoryId 分类id
     */
    private void updateContentListInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(CATEGORY_CONTENT).delete(categoryId);
    }

    @Override
    public void update(TbContent tbContent) {

        //查询原来的分类
        TbContent oldContent = findOne(tbContent.getId());

        if(!oldContent.getCategoryId().equals(tbContent.getCategoryId())){
            //修改了广告分类
            updateContentListInRedisByCategoryId(oldContent.getCategoryId());
        }

        //更新分类对应的缓存
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());

        super.update(tbContent);
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        //查询广告列表，然后再将每个广告对应的分类缓存数据删除
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        List<TbContent> contentList = contentMapper.selectByExample(example);
        for (TbContent tbContent : contentList) {
            updateContentListInRedisByCategoryId(tbContent.getCategoryId());
        }

        //删除内容
        super.deleteByIds(ids);
    }
}
