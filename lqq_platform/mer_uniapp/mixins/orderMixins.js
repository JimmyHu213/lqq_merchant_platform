// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { ClipboardJS } from "../plugin/clipboard/clipboard";
export default {
    data() {
        return {
        }
    },
    onReady: function() {
        // #ifdef H5
        this.$nextTick(function() {
            debugger
            const clipboard = new ClipboardJS(".copy-data");
            clipboard.on("success", () => {
                this.$util.Tips({
                    title: '复制成功'
                });
            });
        });
        // #endif
    },
    methods:{
        /**
         * 剪切订单号
         */
        // #ifndef H5
        handleCopy: function(orderNo) {
            let that = this;
            uni.setClipboardData({
                data: orderNo
            });
        },
        // #endif
    }
}