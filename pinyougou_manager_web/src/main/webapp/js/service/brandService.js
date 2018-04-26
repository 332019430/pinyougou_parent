app.service('brandService', function ($http) {
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../brand/search.do?page=' + pageNum + '&rows=' + pageSize, searchEntity);
    }

    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    }

    this.dele = function (selectIds) {
        return $http.get('../brand/delete.do?ids=' + selectIds);
    }

    this.add = function (entity) {
        return $http.post('../brand/add.do', entity);
    }

    this.update = function (entity) {
        return $http.post('../brand/update.do',entity);
    }

    this.selectOptionList=function () {
        return $http.get("../brand/selectBrandList.do")
    }
})