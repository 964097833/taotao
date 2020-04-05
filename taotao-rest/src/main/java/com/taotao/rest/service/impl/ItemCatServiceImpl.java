package com.taotao.rest.service.impl;

import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public CatResult getItemCatList() {

        CatResult catResult = new CatResult();
        //查询分类列表
        catResult.setData(getCatList((long) 0));
        return catResult;
    }

    private List<?> getCatList(Long parentId) {

        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询语句
        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        //创建一个list存放结果
        List resultList = new ArrayList<>();
        //向list中存放节点
        int count = 0;//计数器
        for (TbItemCat tbItemCat:list) {
            //判断是否为父节点
            if (tbItemCat.getIsParent()) {
                CatNode catNode = new CatNode();
                //当前是第一个节点
                if (parentId == 0) {
                    count++;
                    if (count > 14) {
                        break;
                    }
                    catNode.setName("<a href='/products/" + tbItemCat.getId() + ".html'>" + tbItemCat.getName() + "</a>");
                } else {
                    //当前不是第一个节点
                    catNode.setName(tbItemCat.getName());
                }
                catNode.setUrl("/products/" + tbItemCat.getId() + ".html");
                //递归
                catNode.setItem(getCatList(tbItemCat.getId()));
                resultList.add(catNode);
            } else {
                //如果是叶子节点
                resultList.add("/products/"+tbItemCat.getId()+".html|"+tbItemCat.getName());
            }
        }
        return resultList;
    }
}
