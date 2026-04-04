import { productCouponListApi } from '@/api/product';
import { defaultObj } from '@/views/product/creatProduct/default';
import { OrderSecondTypeEnum } from '@/enums/productEnums';
import {uploadImage} from "@/utils/ZBKJIutil";
let headTabList = [
  { tit: '商品信息', name: '1' },
  { tit: '规格库存', name: '2' },
  { tit: '预约设置', name: '5' },
  { tit: '商品详情', name: '3' },
  { tit: '其他设置', name: '4' },
];
export default {
  data() {
    return {
      headTab: [],
      formValidate: Object.assign({}, defaultObj),
      attrInfo: {},
      OrderSecondTypeEnum,
      timeCheckGroup: [], //自动划分当前选中的元素
      isCustomize: false,
    };
  },
  mounted() {
    if (this.formValidate.type === 7) {
      this.headTab = headTabList;
    } else {
      this.headTab = headTabList.slice(0, 2).concat(headTabList.slice(3));
    }
  },
  methods: {
    // 获取详情中的规格数据，预约 采集的时候 只有一条规格，这里做处理
    getAttrList(info, type) {
      if (this.formValidate.type === this.OrderSecondTypeEnum.Reservation) {
        return type === 'copy' && info.attrList.length > 1 ? info.attrList.slice(0, 1) : info.attrList;
      } else {
        return info.attrList;
      }
    },
    // 获取编辑时候的规格数据
    getAttrs(info) {
      if (info.specType) {
        if (info.attrValueList) {
          info.attrValueList.forEach((val) => {
            val.image = this.$selfUtil.setDomain(val.image);
            val.attrValueShow = JSON.parse(val.attrValue);
            val.attr_arr = val.sku.split(',');
            val.brokerage = val.brokerage || 0;
            val.brokerageTwo = val.brokerageTwo || 0;
            val.vipPrice = val.vipPrice || 0;
          });
          this.ManyAttrValue = [...this.oneFormBatch, ...info.attrValueList];
        } else {
          if (this.formValidate.attrList.length) {
            this.oneFormBatch[0].image = this.$selfUtil.setDomain(info.image);
          }
        }
        this.formValidate.attrs = this.formValidate.attrList.map((i) => {
          return {
            value: i.attributeName,
            detail: i.optionList.map((val) => ({ value: val.optionName, image: val.image })),
            add_pic: i.isShowImage ? 1 : 0,
          };
        });
      } else {
        this.OneattrValue = info.attrValueList;
      }
    },
    getData(res, type, editOrCopy) {
      let info = res;
      this.formValidate.content = '';
      const deliveryMethods = this.formValidate.type === this.OrderSecondTypeEnum.Reservation ? ['3']  : ['1']
      this.formValidate = {
        ...info,
        image: this.$selfUtil.setDomain(info.image),
        sliderImages: JSON.parse(info.sliderImage),
        cateIds: info.cateId ? info.cateId.split(',') : [], // 商品分类id
        sort: info.sort ? info.sort : 0,
        stock: info.stock ? info.stock : 0,
        attrList: info.attrList ? this.getAttrList(info, type) : [],
        attrValueList: info.attrValueList ? info.attrValueList : [],
        isSub: info.isSub ? info.isSub : false,
        content: info.content ? this.$selfUtil.replaceImgSrcHttps(info.content) : '',
        id: info.id ? info.id : 0,
        coupons: info.coupons ? info.coupons : [],
        couponIds: info.couponIds ? info.couponIds : [],
        type: info.type ? info.type : Number(this.$route.params.productType),
        guaranteeIdsList: info.guaranteeIds ? info.guaranteeIds.split(',').map(Number) : [], //保障服务
        vipPriceType: info.vipPriceType ? info.vipPriceType : 0,
        deliveryMethodList: info.deliveryMethod ? info.deliveryMethod.split(',') : deliveryMethods,
        systemFormId: info.systemFormId ? info.systemFormId : null,
        refundSwitch: type === 'add' ? info.refundSwitch : true,
        supplementReservation: info.supplementReservation
          ? {
              ...info.supplementReservation,
              reservationDateWeekRangeList: info.supplementReservation.reservationDateWeekRange
                ? info.supplementReservation.reservationDateWeekRange.split(',').map(Number)
                : [1, 2, 3, 4, 5, 6, 7],
            }
          : {},
        temporaryData: {
          customizeTimeClone: [['', '']], // 自定义划分库存
          reservationTime: ['', ''], // 自动划分起止时间
        }
      };
      // 复制商品 卡密类型，置空卡密选择
      if(editOrCopy === 'copy') {
        info.attrValueList.forEach((val) => {
          val.cdkeyLibraryName = ''
          val.cdkeyId = 0
        })
      }
      this.getAttrs(info); // 规格数据

      this.htmlKey++;
      this.labelarr = info.keyword ? info.keyword.split(',') : [];
      if (this.formValidate.categoryId) {
        this.getbrandList();
        this.brandList.push({ name: '其他', id: 0 });
      }

      if (type !== 'copy' && info.type === this.OrderSecondTypeEnum.Reservation) this.getReservationInfo(info);
      if (type === 'copy') this.getImageList() // 采集商品处理图片
      this.getProductCouponList(); // 优惠券

      let imgs = JSON.parse(info.sliderImage);
      let imgss = [];
      Object.keys(imgs).map((i) => {
        imgss.push(this.$selfUtil.setDomain(imgs[i]));
      });
      this.formValidate.sliderImages = [...imgss];
      if (this.getFileType(this.formValidate.sliderImages[0]) == 'video') {
        //如果返回数据轮播图的第一张是视频，就将其赋值给videoLink做渲染，同时将其在轮播图中删除
        this.$set(this, 'videoLink', this.formValidate.sliderImages[0]);
        this.formValidate.sliderImages.splice(0, 1);
      }
      // 次卡商品规格处理
      if (this.formValidate.type == this.OrderSecondTypeEnum.PunchCard) {
        this.formValidate.specType = false
        this.formValidate.attrs = []
        this.formValidate.attrList = []
        this.formValidate.attrValueList = []
      }
    },
    //加载优惠券数据
    getProductCouponList(){
      if (this.formValidate.couponIds && this.formValidate.couponIds.length) {
        productCouponListApi().then((res) => {
          let ids = this.formValidate.couponIds.toString();
          let arr = res;
          let obj = {};
          for (let i in arr) {
            obj[arr[i].id] = arr[i];
          }
          let strArr = ids.split(',');
          let newArr = [];
          for (let item of strArr) {
            if (obj[item]) {
              newArr.push(obj[item]);
            }
          }
          this.$set(this.formValidate, 'coupons', newArr); //在编辑回显时，让返回数据中的优惠券id，通过接口匹配显示,
        });
      }
    },
    // 采集商品处理图片，获取所有图片集合
    getImageList(){
      // 合并并去重图片
      const allImages = [];
      // 添加封面图
      if (this.formValidate.image) {
        allImages.push(this.formValidate.image);
      }
      // 添加轮播图
      if (this.formValidate.sliderImages && this.formValidate.sliderImages.length > 0) {
        allImages.push(...this.formValidate.sliderImages);
      }
      // 添加规格值图片
      if (this.formValidate.specType && this.formValidate.attrValueList) {
        this.formValidate.attrValueList.forEach((item) => {
          if (item.image) {
            allImages.push(item.image);
          }
        });
      }

      // 去重图片数组
      const uniqueImages = [...new Set(allImages)];

      // 过滤出需要下载并重新上传的图片（外部链接图片）
      const externalImages = uniqueImages.filter(img => img && typeof img === 'string' && img.startsWith('http'));

      // 处理外部图片下载并重新上传
      if (externalImages.length > 0) {
        this.handleExternalImages(externalImages);
      }
    },
    async handleExternalImages(externalImages){
      try {
        // 为每个外部图片创建上传Promise
        const uploadPromises = externalImages.map(async (imgUrl) => {
          try {
            // 使用getImgData下载并上传图片
            const newUrl = await this.getImgData(imgUrl);
            return { oldUrl: imgUrl, newUrl };
          } catch (error) {
            console.error('图片上传失败:', imgUrl, error);
            return { oldUrl: imgUrl, newUrl: imgUrl }; // 失败时保留原URL
          }
        });
        
        // 等待所有图片处理完成
        const uploadResults = await Promise.all(uploadPromises);

        this.getChangeImgaes(uploadResults)
        // 隐藏加载状态
        this.fullscreenLoading = false;
      } catch (error) {
        console.error('图片处理失败:', error);
        this.fullscreenLoading = false;
      }
    },
    // 获取上传替换过后的图片地址
    getChangeImgaes(uploadResults){
      // 创建图片映射对象
      const imgMap = {};
      // 构建图片映射
      uploadResults.forEach(result => {
        if (result.newUrl && result.newUrl !== result.oldUrl) {
          imgMap[result.oldUrl] = result.newUrl;
        }
      });

      // 更新封面图
      if (this.formValidate.image && imgMap[this.formValidate.image]) {
        this.formValidate.image = imgMap[this.formValidate.image];
      }

      // 更新轮播图
      if (this.formValidate.sliderImages && Array.isArray(this.formValidate.sliderImages)) {
        this.formValidate.sliderImages = this.formValidate.sliderImages.map(img =>
            imgMap[img] || img
        );
      }

      // 更新规格值图片
      if (this.formValidate.specType && this.formValidate.attrValueList) {
        this.formValidate.attrValueList.forEach((item) => {
          if (item.image && imgMap[item.image]) {
            item.image = imgMap[item.image];
          }
        });
      }
    },
    // 下载图片并上传到服务器
    async getImgData(imgUrl) {
      try {
        // 创建一个临时的img元素来加载图片
        const img = new Image();
        img.crossOrigin = 'anonymous'; // 允许跨域
        
        // 使用Promise包装图片加载
        const imgLoaded = new Promise((resolve, reject) => {
          img.onload = () => resolve(img);
          img.onerror = () => reject(new Error(`图片加载失败: ${imgUrl}`));
          img.src = imgUrl;
        });
        
        // 等待图片加载完成
        await imgLoaded;
        
        // 创建canvas来获取图片数据
        const canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0);
        
        // 将canvas转换为Blob
        const blob = await new Promise((resolve) => {
          canvas.toBlob(resolve, 'image/png');
        });
        
        // 创建FormData对象
        const formData = new FormData();
        formData.append('multipart', blob, 'image.png');
        const data = {
          model: 'product',
          pid: 0,
        };
        // 调用上传接口
        const res = await uploadImage(formData,data);
        return res.url || imgUrl; // 返回上传后的图片地址或原始地址
      } catch (error) {
        console.error('获取图片数据失败:', error);
        return imgUrl; // 失败时返回原始地址
      }
    },
    // 获取选择的时间段
    getTimeCheckGroup(){
      this.timeCheckGroup = []
      this.timeCheckGroup = this.formValidate.timeSlotAutomatic.filter((item) => item.isCheck === 1).map((item) => item.timeSlot);
      this.timeCheckAll = this.timeCheckGroup.length === this.formValidate.timeSlotAutomatic.length;
    },
    // 编辑获取预约商品详情数据
    getReservationInfo(info) {
      //时间段赋值
      if (info.supplementReservation.timeSlotStartEndTime)
        this.formValidate.temporaryData.reservationTime = info.supplementReservation.timeSlotStartEndTime.split(',');
      //预约商品时段划分类型：1-自动，2-自定义 , 编辑中赋值设置的时间段
      let timeSlotLists = info.timeSlotList.map((item) => item.timeSlot);
      if (info.supplementReservation.timeSlotClassifyType === 1) {
        this.formValidate.timeSlotAutomatic = info.timeSlotList;
        this.isCustomize = false;
        this.timeCheckGroup = timeSlotLists;
      } else {
        this.isCustomize = true;
        const formattedTimes = timeSlotLists.map((time) => time.split('-'));
        this.formValidate.temporaryData.customizeTimeClone = formattedTimes;
       // this.customizeTimeData = timeSlotLists;
      }
      this.getChecked()
    },
    // 点击商品图
    modalPicTap(tit, num, i, status) {
      const _this = this;
      if (_this.isDisabled) return;
      this.$modalUpload(
        function (img) {
          if (!img) return;
          if (!tit && !num) {
            _this.formValidate.image = img[0].sattDir;
            _this.OneattrValue[0].image = img[0].sattDir;
          }
          if (tit && !num) {
            if (img.length > 10) return this.$message.warning('最多选择10张图片！');
            if (img.length + _this.formValidate.sliderImages.length > 10)
              return this.$message.warning('最多选择10张图片！');
            img.map((item) => {
              _this.formValidate.sliderImages.push(item.sattDir);
            });
          }
          if (!tit && num === 'dan') {
            _this.OneattrValue[0].image = img[0].sattDir;
          }
          if (!tit && num === 'duo') {
            _this.ManyAttrValue[i].image = img[0].sattDir;
          }
          if (!tit && num === 'pi') {
            _this.oneFormBatch[0].image = img[0].sattDir;
          }
        },
        tit,
        'content',
      );
    },
    // 移动
    handleDragStart(e, item) {
      if (!this.isDisabled) this.dragging = item;
    },
    handleDragEnd(e, item) {
      if (!this.isDisabled) this.dragging = null;
    },
    handleDragOver(e) {
      if (!this.isDisabled) e.dataTransfer.dropEffect = 'move';
    },
    // 移动关键代码，包含轮播图、规格
    handleDragEnter(e, item, index) {
      if (!this.isDisabled) {
        e.dataTransfer.effectAllowed = 'move';
        if (item === this.dragging) {
          return;
        }
        let newItems = [];
        newItems = [...this.formValidate.sliderImages];
        const src = newItems.indexOf(this.dragging);
        const dst = newItems.indexOf(item);
        newItems.splice(dst, 0, ...newItems.splice(src, 1));
        this.formValidate.sliderImages = newItems;
      }
    },
  },
};
