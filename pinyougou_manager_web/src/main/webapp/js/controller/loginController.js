//2.定义一个controller
app.controller('loginController', function ($scope,loginService) {


    $scope.getInfo=function () {
        loginService.getLoginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }





})