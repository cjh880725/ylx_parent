//服务层

app.service("seckillGoodsService",function ($http) {
    //读取列表数据保存到表单中
    this.findList=function () {
        return $http.get("seckillGoods/findList.do");
    }

    this.findOneFromRedis=function(id){
        return $http.get('seckillGoods/findOneFromRedis.do?id='+id);
    }

});