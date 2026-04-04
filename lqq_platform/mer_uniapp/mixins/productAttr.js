/**
 * 商品规格相关的公共方法
 */
import {getProductDetail} from "../api/product";
import {ProductMarketingTypeEnum} from "../enums/productEnums";

export default {
    data(){
        return{
            cartId: 0,
            product_id: 0,
            // attr: {
            //     cartAttr: false,
            //     productAttr: [],
            //     productSelect: {}
            // },
            productValue: [], //系统属性
            productInfo: {},
            attrValue: '', //已选属性
            attrTxt: '请选择', //属性页面提示
           // marketingType: 0,
            ProductMarketingTypeEnum: ProductMarketingTypeEnum
        }
    },
    methods: {
        /**
         * 获取产品详情
         *
         */
        getGoodsDetails: function(item) {
            uni.showLoading({
                title: '加载中',
                mask: true
            });
            this.cartId = item.id;
            this.product_id = item.productId;
            getProductDetail(item.productId, 0,0,'').then(res => {
                uni.hideLoading();
                this.attr.cartAttr = true;
                let productInfo = res.data.productInfo;
                this.$set(this, 'productInfo', productInfo);
                this.$set(this, 'productValue', res.data.productValue);
                let productAttr = res.data.productAttr.map(item => {
                    return {
                        attrName: item.attributeName,
                        attrValues: item.optionList.map(items => items.optionName),
                        id: item.id,
                        isDel: item.isDel,
                        productId: item.productId,
                        optionList: item.optionList,
                        isShowImage: item.isShowImage
                    }
                });
                this.$set(this.attr, 'productAttr', productAttr);
                this.defaultSelect();
            }).catch(err => {
                uni.hideLoading();
            })
        },
        /**
         * 获取符合条件的规格值
         * @param {Array} values - 规格值数组
         * @returns {Array} - 有效的规格值数组
         */
        getValidSku(values) {
            const isGroupbuying = this.marketingType === this.ProductMarketingTypeEnum.Groupbuying;

            // 检查库存条件
            const hasValidStock = (item) => {
                if (isGroupbuying) {
                    return item.stock !== 0 && item.groupStock !== 0;
                }
                return item.stock !== 0;
            };

            // 先找默认规格中库存不为0的
            const defaultSku = values.find(item => item.isDefault && hasValidStock(item));
            if (defaultSku) {
                return defaultSku.sku.split(',');
            }

            // 再找非默认规格中库存不为0的
            const nonDefaultSku = values.find(item => hasValidStock(item));
            return nonDefaultSku ? nonDefaultSku.sku.split(',') : [];
        },

        /**
         * 设置已选中的规格
         * @param {Object} productSelect - 选中的商品规格信息
         */
        setCheckedAttr(productSelect) {
            const attrs = {
                name: this.productInfo.name,
                image: productSelect.image,
                price: productSelect.price,
                stock: productSelect.stock,
                unique: productSelect.id,
                cart_num: 1,
                vipPrice: productSelect.vipPrice,
                otPrice: productSelect.otPrice,
                groupPrice: productSelect.groupPrice,
                groupStock: productSelect.groupStock,
                isPaidMember: this.productInfo.isPaidMember,
                redeemIntegral: productSelect.redeemIntegral,
                cost: productSelect.cost
            };

            Object.entries(attrs).forEach(([key, value]) => {
                this.$set(this.attr.productSelect, key, value);
            });

            this.$set(this, "attrTxt", "已选择");
        },

        /**
         * 设置未选中规格的默认值
         */
        noCheckedAttr() {
            const attrs = {
                name: this.productInfo.name,
                image: this.productInfo.image,
                price: this.productInfo.price,
                stock: 0,
                unique: this.productInfo.id,
                cart_num: 1,
                vipPrice: this.productInfo.vipPrice,
                otPrice: this.productInfo.otPrice,
                groupPrice: this.productInfo.groupPrice,
                isPaidMember: this.productInfo.isPaidMember,
                redeemIntegral: this.productInfo.redeemIntegral,
                cost: this.productInfo.cost
            };

            Object.entries(attrs).forEach(([key, value]) => {
                this.$set(this.attr.productSelect, key, value);
            });

            this.$set(this, "attrValue", "");
            this.$set(this, "attrTxt", "请选择");
        },

        /**
         * 设置默认选中的规格
         */
        defaultSelect() {
            const productAttr = this.attr.productAttr;
            const productValues = Object.values(this.productValue);

            // 获取有效的规格值
            const value = this.getValidSku(productValues);

            // 设置规格属性
            if (value.length) {
                productAttr.forEach((attr, index) => {
                    this.$set(attr, "index", value[index]);
                });
            }

            // 处理规格选择结果
            const productSelect = this.productValue[value.join(",")];
            if (productSelect && productAttr.length) {
                this.setCheckedAttr(productSelect);
                this.$set(this, "attrValue", value.join(","));
                this.$set(this, "attrTxt", "已选择");
            } else {
                this.noCheckedAttr();
            }
            // 更新商品价格
            this.getProductPrice();
        },
        //获取被选中属性；
        getCheckedValue: function() {
            let productAttr = this.attr.productAttr;
            let value = [];
            for (let i = 0; i < productAttr.length; i++) {
                for (let j = 0; j < productAttr[i].attrValues.length; j++) {
                    if (productAttr[i].index === productAttr[i].attrValues[j]) {
                        value.push(productAttr[i].attrValues[j]);
                    }
                }
            }
            return value;
        },
    }
};