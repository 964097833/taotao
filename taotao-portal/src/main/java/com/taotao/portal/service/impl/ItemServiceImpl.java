package com.taotao.portal.service.impl;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.portal.pojo.ItemInfo;
import com.taotao.portal.service.ItemService;
import com.taotao.utils.HttpClientUtil;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Value("${REST_BASE_URL}")
    private String REST_BASE_URL;
    @Value("${ITEM_INFO_URL}")
    private String ITEM_INFO_URL;
    @Value("${ITEM_DESC_URL}")
    private String ITEM_DESC_URL;
    @Value("${ITEM_PARAM_URL}")
    private String ITEM_PARAM_URL;

    @Override
    public ItemInfo getItemInfo(long itemId) {

        ItemInfo itemInfo = null;
        try {
            String json = HttpClientUtil.doGet(REST_BASE_URL + ITEM_INFO_URL + itemId);
            if (!StringUtils.isBlank(json)) {
                TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, ItemInfo.class);
                if (taotaoResult.getStatus() == 200) {
                    itemInfo = (ItemInfo) taotaoResult.getData();
                    return itemInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getItemDesc(long itemId) {

        TbItemDesc itemDesc = null;
        try {

            String json = HttpClientUtil.doGet(REST_BASE_URL + ITEM_DESC_URL + itemId);
            TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemDesc.class);
            if (taotaoResult.getStatus() == 200) {
               itemDesc = (TbItemDesc) taotaoResult.getData();
                String result = itemDesc.getItemDesc();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getItemParam(long itemId) {

        try {
            String json = HttpClientUtil.doGet(REST_BASE_URL + ITEM_PARAM_URL + itemId);
            TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemParamItem.class);
            if (taotaoResult.getStatus() == 200) {
                TbItemParamItem paramItem = (TbItemParamItem) taotaoResult.getData();
                String paramData = paramItem.getParamData();
                //生成html
                //把规格参数json数据转换成java对象
                List<Map> jsonList = JsonUtils.jsonToList(paramData, Map.class);
                StringBuffer sb = new StringBuffer();
                sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\">\n");
                sb.append("     <tbody>\n");
                for(Map m1:jsonList) {
                    sb.append("          <tr>\n");
                    sb.append("               <th class=\"tdTitle\" colspan=\"2\">"+m1.get("group")+"</th>\n");
                    sb.append("          </tr>\n");
                    //取规格项
                    List<Map> list2 = (List<Map>) m1.get("params");
                    for (Map m2 : list2) {
                        sb.append("          <tr>\n");
                        sb.append("               <td class=\"tdTitle\">"+m2.get("k")+"</td>\n");
                        sb.append("               <td>"+m2.get("v")+"</td>\n");
                        sb.append("          </tr>\n");
                    }
                }
                sb.append("     </tbody>\n");
                sb.append("</table>");
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
