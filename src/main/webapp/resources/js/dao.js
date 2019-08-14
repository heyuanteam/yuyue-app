(function (woo) {
    woo.dao = (function () {
        return {
            /**
             * 获取省份
             */
            getProvinces: function () {
                return woo.com.send({
                    url: "M145",
                    data: {
                        CONTTYPE: "text/html",
                        DAT_TYP: 0,
                        PROV_CD: null
                    }
                });
            },
            /**
             * 获取城市
             */
            getCities: function (provinceCode) {
                return woo.com.send({
                    url: "M145",
                    data: {
                        CONTTYPE: "text/html",
                        DAT_TYP: 0,
                        PROV_CD: provinceCode
                    }
                });
            },
            /**
             * 查询联行行号
             */
            getBanks: function (bankCode, cityCode, keyword) {
                return woo.com.send({
                    url: "M143",
                    data: {
                        CONTTYPE: "text/html",
                        DAT_TYP: 0,
                        BNK_COD: bankCode,
                        CITY_CD: cityCode,
                        KEY_WORD: keyword
                    }
                });
            },
            /**
             * 发送注册验证码
             */
            sendSmsCode: function (phone, product) {
                return woo.com.send({
                    url: "sendCode.app",
                    data: {
                        phone: phone,
                        product: product
                    }
                });
            },

            /**
             * 发送忘记密码验证码
             */
            sendForSmsCode: function (phone) {
                return woo.com.send({
                    url: "sendCode.app",
                    data: {
                        phone: phone,
                        product: product
                    }
                });
            },
            /**
             * 注册
             */
            register: function (data) {
                return woo.com.send({
                    url: "register.app",
                    data: {
                        phone: data.phone,
                        code: data.smsCode,
                        pwd: data.pwd,
                        realName: data.realName
                    }
                });
            },

            /**
             * 注册
             */
            appregister: function (data) {
                return woo.com.send({
                    url: "appregister.app",
                    data: {
                        phone: data.phone,
                        code: data.smsCode,
                        pwd: data.pwd,
                        referPhone: data.referPhone,
                        product: data.product
                    }
                });
            },
            /**
             * 申请收银员
             */
            beCashier: function (data) {
                return woo.com.send({
                    url: "ApplyCashier.app",
                    data: {
                        phone: data.phone,
                        psd: data.pwd,
                        sn: data.sn,
                        realName: data.realName
                    }
                });
            },
            editCashier: function (id, status) {
                return woo.com.send({
                    url: "editCashier.app",
                    data: {
                        id: id,
                        status: status
                    }
                });
            },
            deleteCashier: function (id) {
                return woo.com.send({
                    url: "deleteCashier.app",
                    data: {
                        id: id
                    }
                });
            },
            /**
             * 重新申请收银员
             */
            reapply: function (data) {
                return woo.com.send({
                    url: "fix/reapplyCasher",
                    data: {
                        openId: data.openId,
                    }
                });
            },
            /**
             * 获取收银员STATUS
             */
            getCashierStatus: function (openId) {
                return woo.com.send({
                    url: "fix/getCashierStatus",
                    data: {
                        openId: openId,
                    }
                });
            },
            /**
             * 忘记密码修改密码
             */
            forgetPass: function (data) {
                return woo.com.send({
                    url: "forgotPassword.app",
                    data: {
                        psd: data.pwd,
                        phone: data.phone,
                        code: data.smsCode
                    }
                });
            },
            /**
             * 商户登录
             */
            login: function (openId, code, phone, pwd) {
                return woo.com.send({
                    url: "login.app",
                    data: {
                        phone: phone,
                        psd: pwd,
                        openid: openId,
                        code: code
                    }
                });
            },
            /**
             * 收银员登录
             */
            loginCashers: function (openId, code, phone, pwd) {
                return woo.com.send({
                    url: "loginCashers.app",
                    data: {
                        phone: phone,
                        psd: pwd,
                        openid: openId,
                        code: code
                    }
                });
            },
            /**
             * 退出登录
             */
            exitLogin: function (openId) {
                return woo.com.send({
                    url: "fix/exitLogin",
                    data: {
                        openId: openId,
                    }
                });
            },
            /**
             * 修改密码
             */
            modifyPass: function (openId, oldPass, newPass, reNewPass) {
                return woo.com.send({
                    url: "modifyPassword.app",
                    data: {
                        openId: openId,
                        oldpsd: oldPass,
                        newpsd: newPass,
                        newpsd2: reNewPass
                    }
                });
            },

            /**
             * 开户
             */
            openMerchant: function (data) {
                data = data || {};
                return woo.com.send({
                    url: "Auth.app",
                    data: {
                        accountName: data.realName,// 姓名
                        ICNO: data.crpId,// 身份证号
                        merchantName: data.mchName,// 商户名称
                        bankcode: data.bankcode,// 开户行名称
                        account: data.bankCard,// 结算账号
                        icAserverId: icAserverId,
                        icBserverId: icBserverId,
                        bankAserverId: bankAserverId,
                        bankBserverId: bankBserverId,
                        handerICserverId: handerICserverId,
                    }
                });
            },
            /**
             * 额度申请
             */
            applyQuota: function (data) {
                data = data || {};
                return woo.com.send({
                    url: "M1421",
                    data: {
                        openId: data.openId,
                        CONTTYPE: "text/html",
                        CRP_ID_NO1: data.crpId,// 身份证号
                        CRP_ABOVE_IMG1: data.crpAbovePath,// 身份证正面
                        CRP_BELOW_IMG1: data.crpBelowPath,// 身份证反面
                        CRP_CARD_IMG1: data.bankCardPath,// 银行卡照片
                        CRP_TRIPLE_IMG1: data.allInPath
                        // 三合一照片
                    }
                });
            },

            /**
             * 同步用户状态
             */
            syncUser: function (openId) {
                return woo.com.send({
                    url: "M108",
                    data: {
                        DAT_TYP: 0,
                        openId: openId
                    }
                });
            },
            /**
             * 支付
             */
            pay: function (sn, price, openId, type) {
                return woo.com.send({
                    url: "fix/pay",
                    data: {
                        sn: sn,
                        price: price,
                        openId: openId,
                        type: type
                    }
                });
            },
            /**
             * 循环
             */
            loop: function (sn, srefNo) {
                return woo.com.send({
                    url: "fix/loop",
                    data: {
                        sn: sn,
                        srefNo: srefNo
                    }
                });
            },
            /**
             * 绑设备/二维码
             */
            bindCode: function (openId, sn) {
                return woo.com.send({
                    url: "M146",
                    data: {
                        CONTTYPE: "text/html",
                        TRM_SN: sn,
                        openId: openId
                    }
                });
            },
            /**
             * 获取名称
             */
            getInfoBySn: function (sn) {
                return woo.com.send({
                    url: "QRYTRMINF",
                    data: {
                        MSG_TYP: "",
                        TRM_SN: sn
                    }
                });
            },
            /**
             * 查询交易
             */
            queryTrans: function (openId, startDate, endDate) {
                return woo.com.send({
                    url: "transactionQuery.app",
                    data: {
                        STARTDATE: startDate,
                        ENDDATE: endDate,
                        PAG_SIZ: 99999,
                        openId: openId
                    }
                });
            },
            /**
             * 收银员查询交易
             */
            cashierQueryTrans: function (openId, startDate, endDate) {
                return woo.com.send({
                    url: "fix/cashierQueryTrans",
                    data: {
                        STARTDATE: startDate,
                        ENDDATE: endDate,
                        openId: openId
                    }
                });
            },
            /**
             * 我的商店
             */
            center: function (merchantid) {
                return woo.com.send({
                    url: "getCashierPoint.app",
                    data: {
                        merchantid: merchantid
                    }
                });
            },

            /**
             *
             */
            getCashier: function (merchantid) {
                return woo.com.send({
                    url: "getCashier.app",
                    data: {
                        merchantid: merchantid
                    }
                });
            },
            /**
             * 提现
             */
            getMoney: function (openId, money) {
                return woo.com.send({
                    url: "M15201",
                    data: {
                        openId: openId,
                        AC_BAL: money
                    }
                });
            },
            /**
             * 编辑SN
             */
            editSn: function (sn, name) {
                return woo.com.send({
                    url: "fix/editSn",
                    data: {
                        sn: sn,
                        name: name
                    }
                });
            },
            /**
             * 获取SN列表
             */
            getSns: function (sns) {
                return woo.com.send({
                    url: "fix/getSns",
                    data: {
                        sn: sns.join(",")
                    }
                });
            },
            /**
             * 无卡支付
             */
            nocardpay: function (data) {
                return woo.com.send({
                    url: "fix/nocardpay",
                    data: data
                });
            },
            /**
             * 无卡交易查询
             */
            nocardTrans: function (data) {
                return woo.com2.send({
                    url: "mf/nocardpay/fix/transList",
                    data: data
                });
            }

        };
    })();
})(window._WOO || {});
