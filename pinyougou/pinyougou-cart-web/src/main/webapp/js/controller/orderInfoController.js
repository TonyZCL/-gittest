app.controller("orderInfoController",function ($scope,cartService,addressService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //计算总价和总数量
            $scope.totalValue = cartService.sumTotalValue(response);
        });
    };

    //获取当前登录人的收货地址列表
    $scope.findAddressList = function () {
        addressService.findAddressList().success(function (response) {
            $scope.addressList = response;
            //默认地址
            for (var i = 0 ; i < response.length;i++){
                var address = response[i];
                if (address.isDefaulT == "1"){
                    $scope.address = address;
                    break;
                }
            }
        });
    };

    //判断地址是否选中的地址
    $scope.isAddressSelected = function (address) {
        if ($scope.address == address){
            return true;
        }
        return false;
    };

    $scope.selectAddress = function (address) {
      $scope.address = address;
    };

    //订单
    $scope.order = {"paymentType" : "1"};

    //选择支付类型
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    //提交订单
    $scope.submitOrder = function () {
        //1、设置待提交的数据
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;

        //2、发送请求，并处理返回结果
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success){
                if ("1" == $scope.order.paymentType){
                    //携带支付业务 id，跳转到支付页面
                    location.href = "pay.html#?outTradeNo=" + response.message;
                }else {
                    location.href = "paysuccess.html";
                }
            }else {
                alert(response.message);
            }
        });
    };
});