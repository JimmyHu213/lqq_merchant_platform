import Cookies from 'js-cookie';
import { getServiceInfo } from '@/utils/ZBKJIutil';

const JavaMerchantId = Cookies.get('JavaMerchantId');

export const linkData = {
  list: [
    {
      id: 0,
      type: 1,
      name: '店铺主页',
      url: `/pages/merchant/home/index`,
    },
    {
      id: 1,
      type: 1,
      name: '商品分类',
      url: `/pages/merchant/classify/index`,
    },
    {
      id: 2,
      type: 1,
      name: '商品列表',
      url: `/pages/goods/goods_list/index`,
    },
    {
      id: 3,
      type: 1,
      name: '优惠券列表',
      url: `/pages/merchant/coupon/index`,
    },
    {
      id: 4,
      type: 1,
      name: '商户详情',
      url: `/pages/merchant/detail/index`,
    },
    // {
    //   id: 5,
    //   type: 'customerService',
    //   name: '联系客服',
    //   url: getServiceInfo(),
    // },
  ],
  merchantList:[
    {
      id: 1,
      type: 1,
      name: '店铺主页',
      url: `/pages/merchant/home/index`,
    },
    {
      id: 2,
      type: 1,
      name: '商品列表',
      url: `/pages/goods/goods_list/index`,
    },
  ]
};
