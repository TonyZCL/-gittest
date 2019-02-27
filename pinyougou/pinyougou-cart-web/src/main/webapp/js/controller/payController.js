app.controller("payController",function ($scope,$location, cartService,payService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //生成支付二维码
    $scope.createNative = function () {
        //支付业务id,获取浏览器地址栏中的交易编号
        $scope.outTradeNo = $location.search()["outTradeNo"];
        //发送请求到后台获取信息：result_code微信统一下单的操作结果，total_fee支付总金额，outTradeNo交易编号，code_url二维码链接
        payService.createNative($scope.outTradeNo).success(function (response) {
            if ("SUCCESS" == response.result_code){
                //计算总金额
                $scope.totalFee = (response.totalFee/100).toFixed(2);

                //生成支付地址的二维码
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:"H",
                    value:response.code_url
                });

                //查询支付状态
                queryPayStatus($scope.outTradeNo);
            }else {
                alert("生成二维码失败!");
            }
        });
    };

    //查询支付状态
    queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if (response.success){
                location.href = "paysuccess.html#?money=" + $scope.totalFee;
            }else {
                if ("二维码超时" == response.message) {
                    //重新生成新的二维码
                    $scope.createNative();
                } else {
                    //支付失败页面
                    location.href = "payfail.html";
                }
            }
        });
    };

    //获取总金额
    $scope.getMoney = function () {
        $scope.money = $location.search()["money"]
    }

});