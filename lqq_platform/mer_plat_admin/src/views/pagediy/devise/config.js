// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import * as selfUtil from '@/utils/ZBKJIutil.js';
import router from "@/router";

export default function useDeviseDiy() {
    const frontDomain = `${selfUtil.getFrontDomainUrl()}`;
    // 预览
    const previewProtol = (id) => {
        if (!id) return;
        let perViewDia = {};
        perViewDia.perViewUrl = frontDomain + '?id=' + id;
        perViewDia.visible = true;
        return perViewDia;
    };
    // 添加、编辑、赋值
    const onEditDiyPage = (id, type, fromTo) => {
        const { href } = router.resolve({
            path: `/page/design/creatDevise/${id}/${type}`,
        });
        window.open(href);
    };

    return {
        previewProtol,
        onEditDiyPage
    };
}