app.controller("cartController",function ($scope,cartService) {
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

    //增减、删除购物车数据
    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();
            }else {
                alert(response.message);
            }
        });
    };

});