var app = angular.module('pinyougou', []);//定义模块

app.filter('trustHtml',function ($sce) {
    return function (data) {//data就是原来的数据
        return $sce.trustAsHtml(data);
    }
})