import store from '@/store';
import { useProduct } from '@/hooks/use-product';
const { productTypeList } = useProduct();
const propsPlant = {
  children: 'childList',
  label: 'name',
  value: 'id',
  multiple: false,
  emitPath: false,
};
const propsMer = {
  children: 'childList',
  label: 'name',
  value: 'id',
  multiple: false,
  emitPath: false,
  checkStrictly: true,
};

export async function proSearchFormItems() {
  let merPlatProductClassify = []; //平台分类
  let merProductClassify = []; // 商户分类
  await store.dispatch('product/getAdminProductClassify').then((res) => {
    merPlatProductClassify = res;
  });
  await store.dispatch('product/getMerProductClassify').then((res) => {
    merProductClassify = res;
  });
  return [
    {
      type: 'input',
      prop: 'keywords',
      label: '商品搜索：',
      class: 'form_content_width',
      placeholder: '请输入商品名称关键字',
      clearable: true,
    },
    {
      type: 'cascader',
      prop: 'categoryId',
      label: '平台分类：',
      class: 'form_content_width',
      options: merPlatProductClassify,
      props: propsPlant,
      placeholder: '请输入平台商品分类',
      clearable: true,
    },
    {
      type: 'cascader',
      prop: 'cateId',
      label: '商户分类：',
      class: 'form_content_width',
      options: merProductClassify,
      props: propsMer,
      placeholder: '请输入商户商品分类',
      clearable: true,
    },
    {
      type: 'select',
      prop: 'vipPriceType',
      label: '会员类型：',
      class: 'selWidth',
      options: [
        { label: 'SVIP', value: '1' },
        { label: '商户会员', value: '2' },
        { label: '无', value: '0' },
      ],
      placeholder: '请选择',
      clearable: true,
    },
    {
      type: 'select',
      prop: 'productType',
      label: '商品类型：',
      class: 'selWidth',
      options: productTypeList,
      placeholder: '请选择',
      clearable: true,
    },
  ];
}
