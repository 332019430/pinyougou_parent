app.service('specificationService', function ($http) {

    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../specification/search.do?page=' + pageNum + '&rows=' + pageSize, searchEntity);
    }

    this.findOne = function (id) {
        return $http.get('../specification/findOne.do?id=' + id);
    }

    this.dele = function (selectIds) {
        return $http.get('../specification/delete.do?ids=' + selectIds);
    }

    this.add = function (entity) {
        return $http.post('../specification/add.do', entity);
    }

    this.update = function (entity) {
        return $http.post('../specification/update.do',entity);
    }

    this.selectOptionList=function () {
        return $http.get("../specification/selectSpecificationList.do");
    }
})