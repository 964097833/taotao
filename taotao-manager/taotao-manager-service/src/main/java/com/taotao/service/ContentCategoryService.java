package com.taotao.service;

import com.taotao.pojo.EUTreeNode;
import com.taotao.pojo.TaotaoResult;

import java.util.List;

public interface ContentCategoryService {
    List<EUTreeNode> getCategoryList(long parentId);
    TaotaoResult insertContentCategory(long parentId, String name);
}
