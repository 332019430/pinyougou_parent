app.controller('searchController', function ($scope,$location,searchService) {

    $scope.searchMap = {
        "keywords": '',
        "category": '',
        "brand": '',
        spec: {},
        "price": '',
        "pageNum": 1,
        "pageSize": 40
    }
    //价格排序搜索
    $scope.sortSearch=function (sortName,sortType) {
        $scope.searchMap.sortName=sortName;
        $scope.searchMap.sortType=sortType;
        $scope.search();
    }
    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
                buildPageLable();
            }
        );
    }
    $scope.searchPage = function (page) {
        if (isNaN(page)) {
            alert("请输入数字");
            return;
        }
        $scope.searchMap.pageNum = parseInt(page);
        $scope.search();
    }

    buildPageLable = function () {
        //总页数
        var totalPages = $scope.resultMap.totalPages;
        var firstPage = 1;
        $scope.pageLabel = [];
        $scope.firstDot = true;
        $scope.lastDot = true;
        var lastPage = $scope.resultMap.totalPages;
        if (totalPages > 5) {
            if ($scope.searchMap.pageNum <= 3) {
                firstPage = 1;
                lastPage = 5;
                $scope.firstDot = false;
                $scope.lastDot = true;
            } else if ($scope.searchMap.pageNum > totalPages - 2) {
                firstPage = totalPages - 4;
                $scope.firstDot = true;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
                $scope.firstDot = false;
                $scope.lastDot = false;
            }
        } else {
            firstPage = 1;
            lastPage = totalPages;
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            console.log(i);
            $scope.pageLabel.push(i);
        }

    }
    /*ap.put("totalPages",totalPages);
            long totalElements = highlightPage.getTotalElements();
            map.put("totalElements",totalElements);*/

    $scope.addSearchItem = function (key, value) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = '';
        } else {
            delete  $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    $scope.loadKeywords=function () {
        var keywords = $location.search()['keywords'];
        if(keywords==null&&keywords==undefined){
            return;
        }
        $scope.searchMap.keywords=keywords;

        $scope.search();
    }
});
