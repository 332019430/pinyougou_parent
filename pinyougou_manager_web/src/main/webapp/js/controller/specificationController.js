//2.定义一个controller
app.controller('specificationController', function ($scope,$controller,specificationService) {
    $controller('baseController',{$scope:$scope})


    //新增品牌
    $scope.save = function () {
        var serviceObject = specificationService.add($scope.entity);
        //如果entity中没有ID的值说明是新增
        if ($scope.entity.specification.id != null) {
            serviceObject = specificationService.update($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    aletr("添加失败")
                }
            }
        )
    }

    $scope.findOne = function (id) {
        specificationService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }



    $scope.dele = function () {
        specificationService.dele($scope.selectIds).success(
            function (response) {
                $scope.selectIds.splice(0, $scope.selectIds.length);
                if (response.success) {
                    $scope.reloadList();
                } else {
                    aletr("删除失败")
                }
            }
        )
    }
    //定义一个对象
    $scope.searchEntity = {};
    $scope.search = function (pageNum, pageSize) {
                        specificationService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }
    //定义一个集合
    $scope.entity={specificationOptionList:[]}
    $scope.addTableRow=function () {
        $scope.entity.specificationOptionList.push({})
    }

    $scope.deleTableRow=function (index) {
        $scope.entity.specificationOptionList.splice(index,1)
    }
})