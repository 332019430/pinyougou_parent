//2.定义一个controller
app.controller('baseController', function ($scope) {
    //发送请求去查询数据库的数据

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        /*totalItems: 10,*/
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {//入口函数
            console.log("进入分页函数")
            $scope.reloadList();//重新加载
        }
    };
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }
    /**
     *多选checkbox
     */
    $scope.selectIds = [];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);
        }
    }

    $scope.jsonToString=function (jsonString,key) {
        var jsonArray=angular.fromJson(jsonString);
        var str ="";
        for (var i = 0; i < jsonArray.length; i++) {
             str += jsonArray[i][key]+",";
        }
        if (str.length>0){
           str= str.substring(0,str.length-1);
        }
        return str;
    }
})