package cn.cjh.core.service;

public interface ItemPageService {

    //生成Item商品详情页
    public boolean genItemHtml(Long id);
    //删除详情页面
    public boolean deleteItemHtml(Long[] goodsIds);
}
