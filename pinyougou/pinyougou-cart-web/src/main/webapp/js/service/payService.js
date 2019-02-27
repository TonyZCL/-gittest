app.service("payService",function ($http) {
    this.getUsername = function () {
        return $http.get("cart/getUsername.do?t="+Math.random());
    };

    this.createNative = function (outTradeNo) {
        return $http.get("pay/createNative.do?outTradeNo=" + outTradeNo + "&r" + Math.random());
    };

    this.queryPayStatus = function (outTradeNo) {
        return $http.get("pay/queryPayStatus.do?outTradeNo=" + outTradeNo +
            "&r=" + Math.random());
    };
});