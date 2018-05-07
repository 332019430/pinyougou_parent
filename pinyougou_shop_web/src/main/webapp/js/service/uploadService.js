//文件上传服务层
app.service("uploadService",function($http){
    //定义上传function
    this.uploadFile=function(){
        //创建一个Form表单
        var formData=new FormData();
        //form表单添加一个文件上传
        formData.append("file",file.files[0]);

        return $http({
            //post请求
            method:'POST',
            //请求地址
            url:"../upload.do",
            //传送的数据
            data: formData,
            //不确定data
            headers: {'Content-Type':undefined},
            //表示使用angulr的定义 定义request传输的数据格式。
            transformRequest: angular.identity
        });
    }
});