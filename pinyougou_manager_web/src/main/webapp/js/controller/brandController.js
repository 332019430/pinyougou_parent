//2.定义一个controller
app.controller('brandController', function ($scope,$controller,brandService) {
    $controller('baseController',{$scope:$scope})


    //新增品牌
    $scope.save = function () {
        var serviceObject = brandService.add($scope.entity);
        //如果entity中没有ID的值说明是新增
        if ($scope.entity.id != null) {
            serviceObject = brandService.update($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert("添加失败")
                }
            }
        )
    }

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }



    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                $scope.selectIds.pop();
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert("删除失败")
                }
            }
        )
    }

    $scope.searchEntity = {};
    $scope.search = function (pageNum, pageSize) {
        brandService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }
})
