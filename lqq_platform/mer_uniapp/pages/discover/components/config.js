import store from "../../../store";

/**
 * 视频操作按钮
 * @param list 列表对象
 */
export function operationButton(list, isShowCent) {
    const config = [];
    if(isShowCent){
        config.push({
            text: '点赞', //2.点赞
            num: list.likeNum,
            icon: 'icon-ic_love_2',
            type: "like",
            top: '5px'
        });
        if(list.platReplySwitch){  // 3.评论
            config.push({
                text: '评论',
                num: list.replyNum,
                icon: 'icon-icon_comment',
                type: "comment",
                top: '18px'
            });
        }
        if(list.auditStatus == 1){   // 4.分享
            config.push({
                text: '分享',
                type: "share",
                icon: 'icon-icon_transmit',
                top: '17px'
            });
        }
        if(list.authorId == store.state.app.uid){   //5.自己的视频
            config.push({
                type: "more",
                icon: 'icon-ic_more',
                top: '17px'
            });
        }
        if(list.productList && list.productList.length>0){  // 商品购物车
            config.push({
                type: "collection",
                top: '18px'
            });
        }
        config.push({ // 展开
            type: "enlarge",
            icon: 'icon-ic_enlarge2',
            style: 'onHide',
            top: list.productList && list.productList.length>0 ? '0' : '35rpx'
        });
    }else{
        config.push({
            type: "enlarge",
            style: 'onHide',
            icon: 'icon-ic_reduce',
        });
    }
    return config;
}

export function myOperationButton(list){
    return [
        {
            text: '编辑',
            icon: 'icon-ic_edit',
            type: 'edit'
        },
        {
            text: '删除',
            icon: 'icon-ic_delete',
            type: 'delete'
        },
        {
            text: list.replyStatus ==1 ? '禁止评论' : '开启评论',
            icon: list.replyStatus ==1 ? 'icon-ic_message3' : 'icon-ic_message',
            type: 'message'
        },
    ]
}