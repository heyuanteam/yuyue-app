//Common event
function formatDate(fmt, date) { // author: meizz
    var o = {
        "M+": date.getMonth() + 1, // 月份
        "d+": date.getDate(), // 日
        "h+": date.getHours(), // 小时
        "m+": date.getMinutes(), // 分
        "s+": date.getSeconds(), // 秒
        "q+": Math.floor((date.getMonth() + 3) / 3), // 季度
        "S": date.getMilliseconds()
        // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

window._WOO = {
    vender: {}
};
(function (woo) {
    /**
     * 配置文件
     */
    woo.config = (function () {
        // var host = "http://www.woodare.com/";
        var host = "http://localhost:8020/";
        var webcontext = window.CMS_URL;// host + "APP2/";
        var url = webcontext + "";
        var page = webcontext + "";
        var uploads = webcontext + "";
        var imgupload = webcontext + "";
        return {
            host: host,
            webcontext: webcontext,
            url: url,
            page: page,
            uploads: uploads,
            imgupload: imgupload
        };
    })();
    /**
     * 本地缓存
     */
    var support = false;
    var sStorage = window.sessionStorage;
    if (sStorage && sStorage.setItem) {
        var word = "testtesttesttest";
        sStorage.setItem(word, word);
        support = sStorage.getItem(word) == word;
        sStorage.removeItem(word);
    }
    woo.sess = {
        save: function (key, value) {
            if (support) {
                if (typeof value === 'object') {
                    sStorage.setItem(key, JSON.stringify(value));
                } else {
                    sStorage.setItem(key, value);
                }
            }
        },
        get: function (key) {
            if (support) {
                var value = sStorage.getItem(key);
                try {
                    value = JSON.parse(value);
                } catch (e) {
                }
                return value;
            } else {
                return null;
            }
        },
        remove: function (key) {
            if (support && key) {
                sStorage.removeItem(key);
            }
        },
        empty: function () {
            if (support) {
                sStorage.clear();
            }
        }
    };
    /**
     * 路由
     */
    woo.route = {
        _noAuthPages: [
            'login',
            'phone-number',
            'service',
            'service-detail',
            'service-comment',
            'shop-mall',
            'shop-mall-detail',
            'goods-comment'
        ],
        go: function (page, params) {
            var d = woo.sess.get("CACHED_PARAMES");
            d = d || {};
            d[page] = params;
            woo.sess.save("CACHED_PARAMES", d);
            if (!woo.sess.get("token") && this._noAuthPages.indexOf(page) < 0) {
                woo.sess.save("RETURN_URL", page);
                window.location.href = woo.config.page + "login.html";
            } else {
                window.location.href = woo.config.page + page + ".html";
            }
        },
        reload: function () {
            window.location.reload();
        },
        getParams: function (page) {
            var d = woo.sess.get("CACHED_PARAMES") || {};
            return d[page];
        },
        cleanParams: function (page) {
            var d = woo.sess.get("CACHED_PARAMES") || {};
            d[page] = null;
            woo.sess.save("CACHED_PARAMES", d);
        },
        goBackUrl: function () {
            var backUrl = woo.sess.get("RETURN_URL") || "service";
            woo.sess.remove("RETURN_URL");
            window.location.href = woo.config.page + backUrl + ".html";
        },
        checkAuth: function () {
            var page = (window.location.origin + window.location.pathname).replace(woo.config.page, "").replace(".html", "");
            if (!woo.sess.get("token") && this._noAuthPages.indexOf(page) < 0) {
                woo.sess.save("RETURN_URL", page);
                window.location.href = woo.config.page + "login.html";
            }
        }
    };
    /**
     * 工具类
     */
    woo.utils = {
        getImagePath: function (path) {
            return woo.config.uploads + path;
        },
        getDate: function (elName) {
            var d = $(elName).val();
            return d ? new Date(d).getTime() : null;
        },
        parseDate: function (d) {
            return d ? new Date(d).getTime() : null;
        },
        formatDate: function (longTime) {
            if (longTime == null) {
                longTime = new Date().getTime();
            }
            return formatDate('yyyy-MM-dd', new Date(longTime));
        },
        formatTime: function (longTime) {
            if (longTime == null) {
                longTime = new Date().getTime();
            }
            return formatDate('yyyy-MM-dd hh:mm:ss', new Date(longTime));
        },
        toFixed: function (number, precision) {
            number = number || 0;
            if (precision !== 0) {
                precision = precision || 2;
            }
            number = Number(number);
            return number.toFixed(precision);
        },
        getDueInfo: function (date) {
            date = new Date(date).getTime();
            var left = parseInt((date - new Date().getTime()) / 8.64e7);
            var dueDay = 280 - left;
            var week = parseInt(dueDay / 7);
            var day = dueDay % 7 + 1;
            return {
                left: left,
                dueDay: dueDay,
                week: week,
                day: day
            };
        },
        getBirthInfo: function (date) {
            date = new Date(date).getTime();
            return parseInt((new Date().getTime() - date) / 8.64e7);
        },
        getUrlParam: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg); // 匹配目标参数
            if (r != null) {
                return decodeURI(r[2]);
            }
            return null; // 返回参数值
        }
    };
    /**
     * 网络请求
     */
    woo.com = {
        send: function (options) {
            options = options || {};
            var self = this;
            var deferred = new $.Deferred();
            var d = options.data || {};
            if (window.PLATFORM_CODE) {
                d.pcode = window.PLATFORM_CODE;
            }
            var req = $.ajax({
                url: window.CMS_URL + options.url,
                type: options.type || 'POST',
                dataType: options.dataType || 'json',
                data: d,
                timeout: options.timeout || 30000
            });
            deferred.fail(options.error).done(options.success);
            req.fail(function (jqXHR, textStatus, errorThrown) {
                deferred.rejectWith(deferred, [
                    options.errorMsg,
                    textStatus,
                    jqXHR,
                    errorThrown
                ]);
            }).done(function (data, textStatus, jqXHR) {
                if (options.dataType === 'html') {
                    if (data) {
                        deferred.resolveWith(deferred, [
                            data,
                            textStatus,
                            jqXHR
                        ]);
                    } else {
                        deferred.rejectWith(deferred, [
                            options.errorMsg,
                            textStatus,
                            jqXHR,
                            data
                        ]);
                    }
                } else {
                    if (data.RETURNCODE === '00') {
                        deferred.resolveWith(deferred, [
                            data,
                            textStatus,
                            jqXHR
                        ]);
                    } else {
                        if (data.RETURNCODE === "MCA10007") {// 用户已失效 TOKEN
                            // 失效
                            woo.modal.alert("用户授权已失效，请重新登录").done(function () {
                                window.location.href = window.CMS_URL + "login.app";
                            });
                        } else {
                            deferred.rejectWith(deferred, [
                                data.RETURNCON,
                                textStatus,
                                jqXHR,
                                data
                            ]);
                        }
                    }
                }
            });
            return deferred.promise();
        }
    };


    /**
     * 网络请求2
     */
    woo.com2 = {
        send: function (options) {
            options = options || {};
            var self = this;
            var deferred = new $.Deferred();
            var d = options.data || {};
            if (window.PLATFORM_CODE) {
                d.pcode = window.PLATFORM_CODE;
            }
            var req = $.ajax({
                url: window.CMS_URL + options.url,
                type: options.type || 'POST',
                dataType: options.dataType || 'json',
                data: d,
                timeout: options.timeout || 30000
            });
            deferred.fail(options.error).done(options.success);
            req.fail(function (jqXHR, textStatus, errorThrown) {
                deferred.rejectWith(deferred, [
                    options.errorMsg,
                    textStatus,
                    jqXHR,
                    errorThrown
                ]);
            }).done(function (data, textStatus, jqXHR) {
                if (options.dataType === 'html') {
                    if (data) {
                        deferred.resolveWith(deferred, [
                            data,
                            textStatus,
                            jqXHR
                        ]);
                    } else {
                        deferred.rejectWith(deferred, [
                            options.errorMsg,
                            textStatus,
                            jqXHR,
                            data
                        ]);
                    }
                } else {
                    deferred.resolveWith(deferred, [
                        data,
                        textStatus,
                        jqXHR
                    ]);
                }
            });
            return deferred.promise();
        }
    };

    /**
     * 绑定全局事件
     */
    $(function () {
        // 点击高亮。 //粉色
        // 给需要高亮的地方加上“zhy-btn” class
        $("body").delegate(".zhy-btn", "mousedown", function (ev) {
            $(this).addClass('act');
        });
        $("body").delegate(".zhy-btn", "mouseup", function (ev) {
            $(this).removeClass('act');
        });
        $("body").delegate(".zhy-btn", "mouseout", function (ev) {
            $(this).removeClass('act');
        });
        $("body").delegate(".zhy-btn", "touchstart", function (ev) {
            $(this).addClass('act');
        });
        $("body").delegate(".zhy-btn", "touchmove", function (ev) {
            $(this).removeClass('act');
        });
        $("body").delegate(".zhy-btn", "touchend", function (ev) {
            $(this).removeClass('act');
        });

        // common event nav
        $(".a-bottom").click(function () {
            $(".a-bottom.active").removeClass("active");
            $(this).addClass("active");
        });
    });

    $(function () {
        // 点击高亮。//灰色
        // 给需要高亮的地方加上“zhy-btt” class
        $("body").delegate(".zhy-btt", "mousedown", function (ev) {
            $(this).addClass('act');
        });
        $("body").delegate(".zhy-btt", "mouseup", function (ev) {
            $(this).removeClass('act');
        });
        $("body").delegate(".zhy-btt", "mouseout", function (ev) {
            $(this).removeClass('act');
        });
        $("body").delegate(".zhy-btt", "touchstart", function (ev) {
            $(this).addClass('act');
        });
        $("body").delegate(".zhy-btt", "touchmove", function (ev) {
            $(this).removeClass('act');
        });
        $("body").delegate(".zhy-btt", "touchend", function (ev) {
            $(this).removeClass('act');
        });

        // common event nav
        $(".a-bottom").click(function () {
            $(".a-bottom.active").removeClass("active");
            $(this).addClass("active");
        });
    });
    /**
     * 登录检查
     */
    // woo.route.checkAuth();
})(window._WOO || {});

// Modal
(function (woo) {
    function ConfirmModal(title, message, yesBtn, noBtn) {
        var deferred = new $.Deferred();
        $("#modal-bg, #modal-confirm").remove();
        this.$bg = $('<div class="body-bg" id="modal-bg"></div>');
        var elArr = [
            '<div class="confirm" id="modal-confirm">',
            '<div class="title">',
            title,
            '</div>',
            '<div class="mask">',
            message,
            '</div>',
            '<div class="return">',
            '<a class="return-left zhy-btn">',
            (noBtn || '取消'),
            '</a>',
            '<a class="return-right zhy-btn">',
            (yesBtn || '确定'),
            '</a>',
            '</div>',
            '</div>'
        ];
        this.$modal = $(elArr.join(""));
        this.$ok = this.$modal.find(".return-right");
        this.$no = this.$modal.find(".return-left");
        var self = this;
        this.$no.click(function () {
            self.$bg.remove();
            self.$modal.remove();
            deferred.reject();
        });
        this.$ok.click(function () {
            self.$bg.remove();
            self.$modal.remove();
            deferred.resolve();
        });
        this.show = function () {
            $('body').append(this.$bg).append(this.$modal);
        };
        this.deferred = deferred.promise();
    }

    function AlertModal(title, message, yesBtn) {
        var deferred = new $.Deferred();
        $("#modal-bg, #modal-confirm").remove();
        this.$bg = $('<div class="body-bg" id="modal-bg"></div>');
        var elArr = [
            '<div class="confirm" id="modal-confirm">',
            '<div class="title">',
            title,
            '</div>',
            '<div class="mask">',
            message,
            '</div>',
            '<div class="return">',
            '<a class="return-full zhy-btn">',
            (yesBtn || '确定'),
            '</a>',
            '</div>',
            '</div>'
        ];
        this.$modal = $(elArr.join(""));
        this.$ok = this.$modal.find(".return-full");
        var self = this;
        this.$ok.click(function () {
            self.$bg.remove();
            self.$modal.remove();
            deferred.resolve();
        });
        this.show = function () {
            $('body').append(this.$bg).append(this.$modal);
        };
        this.deferred = deferred.promise();
    }

    function LoginModal(title, message, yesBtn, noBtn) {
        $("#modal-bg, #modal-confirm").remove();
        this.$bg = $('<div class="body-bg" id="modal-bg"></div>');
        var elArr = [
            '<div class="confirm" id="modal-confirm">',
            '<div style="text-align: center; font-size: 20px; color: #555; padding-top: 12px;">快速登录</div>',
            '<div class="login-list ">',
            '<div class="login-block-title" >用户名</div>',
            '<div class="login-data">',
            '<input value="" name="username" type="text" id="username" placeholder="手机号">',
            '</div>',
            '</div>',
            '<div class=" login-pass">',
            '<div class="login-block-title" >密码</div>',
            '<div class="login-data">',
            '<input value="" name="password" type="password" id="password" placeholder="密码">',
            '</div>',
            '</div>',
            '<div style="text-align: right; padding-right: 10%;"><a href="phone-number.html" style="color: #33B5E5; font-size: 14px;">注册</a></div>',
            '<div class="return b-log">',
            '<a class="return-full zhy-btn login-btn">登录</a>',
            '</div>',
            '</div>'
        ];
        this.$modal = $(elArr.join(""));
        this.$ok = this.$modal.find(".login-btn");
        var self = this;
        this.$ok.click(function () {
            var username = $("#username").val();
            var pwd = $("#password").val();
            _WOO.AuthModel.login(username, pwd).done(function (d) {
                self.$bg.remove();
                self.$modal.remove();
            }).fail(function (d) {
                _WOO.modal.alert(d).done(function () {
                    woo.modal.login();
                });
            });
        });
        this.show = function () {
            $('body').append(this.$bg).append(this.$modal);
        };
    }

    function EditModal(title, message, defaultValue) {
        var deferred = new $.Deferred();
        $("#modal-bg, #modal-confirm").remove();
        this.$bg = $('<div class="body-bg" id="modal-bg"></div>');
        var elArr = [
            '<div class="confirm" id="modal-confirm">',
            '<div style="text-align: center; font-size: 20px; color: #555; padding-top: 12px;">' + title + '</div>',
            '<div style="padding: 8px;">',
            '<div style="font-size:16px;color: #999;">' + message + '</div>',
            '<div>',
            '<input value="' + defaultValue + '" type="text" id="model-value" style="font-size:16px;width: 100%; border: 1px solid #EFEFEF; height: 32px; line-height: 32px; margin-top: 8px; padding-left: 4px;">',
            '</div>',
            '</div>',
            '<div class="return">',
            '<a class="return-left zhy-btn">取消</a>',
            '<a class="return-right zhy-btn">确定</a>',
            '</div>',
            '</div>'
        ];
        this.$modal = $(elArr.join(""));
        this.$ok = this.$modal.find(".return-right");
        this.$no = this.$modal.find(".return-left");
        var self = this;
        this.$no.click(function () {
            self.$bg.remove();
            self.$modal.remove();
            deferred.reject();
        });
        this.$ok.click(function () {
            var username = self.$modal.find("#model-value").val();
            self.$bg.remove();
            self.$modal.remove();
            deferred.resolve(username);
        });
        this.show = function () {
            $('body').append(this.$bg).append(this.$modal);
        };
        this.deferred = deferred.promise();
    }

    /**
     * 弹出框类
     */
    woo.modal = {
        confirm: function (msg, title, yesBtn, noBtn) {
            var modal = new ConfirmModal(title || '提示', msg, yesBtn, noBtn);
            modal.show();
            return modal.deferred;
        },
        alert: function (msg, title) {
            var modal = new AlertModal(title || '提示', msg);
            modal.show();
            return modal.deferred;
        },
        login: function () {
            var modal = new LoginModal();
            modal.show();
            return modal.deferred;
        },
        edit: function (title, message, defaultValue) {
            var modal = new EditModal(title || '编辑', message || '', defaultValue || '');
            modal.show();
            return modal.deferred;
        }
    };
})(window._WOO || {});