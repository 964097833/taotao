package com.taotao.service.impl;

import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.EUTreeNode;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Override
    public List<EUTreeNode> getCategoryList(long parentId) {
        //根据parentId查询节点列表
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        List<EUTreeNode> result = new ArrayList<>();
        for (TbContentCategory contentCategory:list) {
            //创建一个节点
            EUTreeNode node = new EUTreeNode();
            node.setId(contentCategory.getId());
            node.setText(contentCategory.getName());
            node.setState(contentCategory.getIsParent()?"closed":"open");
            result.add(node);
        }
        return result;
    }

    @Override
    public TaotaoResult insertContentCategory(long parentId, String name) {
        //创建一个pojo对象
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setName(name);
        contentCategory.setIsParent(false);
        //'排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数',
        contentCategory.setSortOrder(1);
        contentCategory.setParentId(parentId);
        //'状态。可选值:1(正常),2(删除)',
        contentCategory.setStatus(1);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //插入数据
        contentCategoryMapper.insert(contentCategory);
        //创建一个父节点
        TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
        //判断父节点是否已经标注位true，未标注则标注上true
        if (!parentCat.getIsParent()) {
            parentCat.setIsParent(true);
            contentCategoryMapper.updateByPrimaryKey(parentCat);
        }
        return TaotaoResult.ok(contentCategory);
    }
}
