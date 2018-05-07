//控制层
app.controller('goodsController', function ($scope, $controller, $location,uploadService,itemCatService,goodsService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承
    $scope.image_entity={color:'',url:''};
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出url
                $scope.image_entity.url = response.msg;//设置文件地址
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传发生错误");
        });
    };
    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if(id==null ||id==undefined){
            return;
        }
        goodsService.findOne(id).success(

            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.goodsDesc.introduction);

                $scope.entity.goodsDesc.itemImages=angular.fromJson($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.entity.goodsDesc.customAttributeItems);

                $scope.entity.goodsDesc.specificationItems=angular.fromJson($scope.entity.goodsDesc.specificationItems);

                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec=angular.fromJson($scope.entity.itemList[i].spec);

                }
            }
        );
    }
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}, itemList: []};

    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象

        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            $scope.entity.goodsDesc.introduction = editor.html();
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    $scope.entity = {};//清空
                    editor.html('');//清空富文本编辑器中的内容。
                    //重新查询
                    window.location.href="goods.html";
                } else {
                    alert(response.msg);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }



    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    }

    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                }
            )
        }
    })

    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                }
            )
        }
    })

    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
                }
            )
        }
    })

    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        if (newValue != undefined) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response; //更新模板ID
                    $scope.typeTemplate.brandIds = angular.fromJson(response.brandIds);
                    if($location.search()['id']==null||$location.search()[id]==undefined){
                        $scope.entity.goodsDesc.customAttributeItems = angular.fromJson(response.customAttributeItems);
                    }

                }
            )

            typeTemplateService.specList1(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            )

        }
    })

    $scope.updateSpecAttribute = function ($event, name, optionValue) {
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (obj == null) {
                        $scope.entity.goodsDesc.specificationItems.push({'attributeName': name, 'attributeValue': [optionValue]});
        } else {
            if ($event.target.checked) {
                obj.attributeValue.push(optionValue);
            } else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(optionValue), 1);
            }
            if (obj.attributeValue.length == 0) {
                $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj), 1)
            }
        }
    }

    $scope.createList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 0, status: '0', idDefault: '0'}];
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            //obj是一个map
            var obj = items[i];
            $scope.entity.itemList = addColumn($scope.entity.itemList, obj.attributeName, obj.attributeValue);
        }
    }

    addColumn = function (list, column, columnValue) {
        var newList=[];
        //页面要显示的集合，此时这个集合已被初始化，spec为空
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            //遍历map中的"attributeValue"的集合
            for (var j = 0; j < columnValue.length; j++) {
                //深克隆一个要显示的行对象
                var newRow = angular.fromJson(angular.toJson(oldRow));
                //把
                newRow.spec[column] = columnValue[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    $scope.cleanSpec=function (num) {
        if (num==0){
            $scope.entity.itemList = [{spec: {}, price: 0, num: 0, status: '0', idDefault: '0'}];
        }
    }

    $scope.searchEntity = {};//定义搜索对象
    $scope.status=['未审核','已审核','审核未通过','关闭'];

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.itemCatList=[];
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    var obj = response[i];
                    $scope.itemCatList[obj.id]=obj.name;
                }
            }
        )
    }

    $scope.checkAttributeValue=function (name,value) {
        var specThisItems = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(specThisItems, 'attributeName', name);
        if(obj!=null){
            if(obj.attributeValue.indexOf(value)!=-1){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    $scope.goListPage=function () {
        history.back();
    }
});
