<template>
  <div>
    <el-row :gutter="24">
      <!-- 次卡商品参数 -->
      <el-col :span="24" v-if="formValidate.type === OrderSecondTypeEnum.PunchCard">
        <el-form-item label="核销次数：" required>
          <el-input-number
            v-model="formValidate.verifyQuantity"
            controls-position="right"
            :step="1"
            step-strictly
            :min="1"
            :disabled="isDisabled"
          ></el-input-number>
          <div class="from-tips">用户可以核销的次数，一次性购买多个商品则取总和次数</div>
        </el-form-item>
        <el-form-item label="核销时效：" required>
          <el-radio-group
            v-model="formValidate.verifyTimeLimitType"
            :disabled="isDisabled"
            @input="changeVerifyTimeType"
          >
            <el-radio :label="1">永久有效</el-radio>
            <el-radio :label="2">购买几天后有效</el-radio>
            <el-radio :label="3">固定有效期</el-radio>
          </el-radio-group>
          <div v-if="formValidate.verifyTimeLimitType === 2">
            <el-input-number
              v-model="formValidate.verifyTimeLimitDay"
              controls-position="right"
              :step="1"
              step-strictly
              :min="1"
              :disabled="isDisabled"
            ></el-input-number>
          </div>
          <div v-if="formValidate.verifyTimeLimitType === 3">
            <el-date-picker
              v-model="punchCardTimeVal"
              type="daterange"
              size="small"
              style="width: 360px"
              class="mr20"
              :clearable="false"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              :disabled="isDisabled"
              :picker-options="pickerOptions"
            >
            </el-date-picker>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="佣金设置：" props="isSub" required>
          <el-radio-group v-model="formValidate.isSub" :disabled="isDisabled" @change="changeAttrVal">
            <el-radio :label="true" class="radio">单独设置</el-radio>
            <el-radio :label="false">默认设置</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- <el-form-item label="会员商品：" required>
          <el-radio-group v-model="formValidate.isPaidMember" :disabled="isDisabled" @change="changeAttrVal">
            <el-radio :label="true" class="radio">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="会员价类型：" required>
          <el-radio-group v-model="formValidate.vipPriceType" :disabled="isDisabled" @change="changeAttrVal">
            <el-radio :label="0" class="radio">无</el-radio>
            <el-radio :label="1" class="radio">svip价</el-radio>
            <el-radio :label="2" class="radio">商户会员价</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="规格类型：" props="specType" v-if="formValidate.type !== OrderSecondTypeEnum.PunchCard">
          <el-radio-group
            v-model="formValidate.specType"
            @change="onChangeSpec(formValidate.specType)"
            :disabled="isDisabled"
          >
            <el-radio :label="false" class="radio">单规格</el-radio>
            <el-radio :label="true">多规格</el-radio>
          </el-radio-group>
          <el-dropdown
            :disabled="isDisabled"
            v-if="formValidate.specType && ruleList.length && formValidate.type !== OrderSecondTypeEnum.Reservation"
            class="ml20 dropdown-menu-box"
            @command="confirm"
            trigger="hover"
          >
            <span class="el-dropdown-link"> 选择规格模板<i class="el-icon-arrow-down el-icon--right"></i> </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="(item, index) in ruleList" :key="index" :command="item.ruleName">{{
                item.ruleName
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-form-item>
      </el-col>
      <!-- 多规格添加-->
      <el-col v-if="formValidate.specType && !isDisabled" :span="24" class="noForm">
        <el-form-item label="商品规格：">
          <div v-show="formValidate.attrs.length" class="from-tips mb20" style="margin-top: 9px">
            勾选添加规格图，当前规格图片必须上传
          </div>
          <div class="specifications">
            <draggable
              group="specifications"
              :disabled="formValidate.attrs.length < 2"
              :list="formValidate.attrs"
              handle=".move-icon"
              @end="onMoveSpec"
              animation="300"
            >
              <div
                class="specifications-item active"
                v-for="(item, index) in formValidate.attrs"
                :key="index"
                @click="changeCurrentIndex(index)"
              >
                <div class="move-icon">
                  <span class="iconfont icon-drag2"></span>
                </div>
                <i class="del el-icon-error" @click="handleRemoveRole(index, item.value)"></i>
                <div class="specifications-item-box">
                  <div class="lineBox"></div>
                  <div class="specifications-item-name mb18">
                    <el-input
                      size="small"
                      v-model="item.value"
                      placeholder="规格名称"
                      @input="attrChangeValue(index, item.value)"
                      @focus="handleFocus(item.value)"
                      class="specifications-item-name-input"
                      maxlength="30"
                      show-word-limit
                    ></el-input>
                    <el-checkbox
                      class="ml20"
                      v-model="item.add_pic"
                      :disabled="!item.add_pic && !canSel"
                      :true-label="1"
                      :false-label="0"
                      @change="(e) => addPic(e, index)"
                      >添加规格图</el-checkbox
                    >
                    <el-tooltip
                      class="item"
                      effect="dark"
                      content="添加规格图片, 仅支持打开一个(建议尺寸:800*800),勾选后,当前规格图片必须上传"
                      placement="right"
                    >
                      <i class="el-icon-info"></i>
                    </el-tooltip>
                  </div>
                  <div class="rulesBox ml-30">
                    <draggable
                      class="item"
                      :list="item.detail"
                      :disabled="item.detail.length < 2"
                      handle=".drag"
                      @end="onMoveSpec"
                    >
                      <div v-for="(det, indexn) in item.detail" :key="indexn" class="mr10 spec drag">
                        <i class="el-icon-error" @click="handleRemove2(item.detail, indexn, det.value)"></i>
                        <el-input
                          style="width: 430px"
                          size="small"
                          v-model="det.value"
                          placeholder="规格值"
                          @input="attrDetailChangeValue(det.value, index)"
                          @focus="handleFocus(det.value)"
                          maxlength="30"
                          show-word-limit
                          @blur="handleBlur()"
                        >
                          <template slot="prefix">
                            <span class="iconfont icon-drag2"></span>
                          </template>
                        </el-input>
                        <div class="img-popover" v-if="item.add_pic">
                          <div class="popper-arrow"></div>
                          <div class="popper" @click="handleSelImg(det, indexn)">
                            <el-image class="image" v-if="det.image" :src="det.image" fit="cover"></el-image>
                            <i v-else class="el-icon-plus"></i>
                          </div>
                          <i v-if="det.image" class="img-del el-icon-error" @click="handleRemoveImg(det)"></i>
                        </div>
                      </div>
                      <div>
                        <el-popover
                          :ref="'popoverRef_' + index"
                          placement=""
                          width="420"
                          trigger="click"
                          @after-enter="handleShowPop(index)"
                        >
                          <el-input
                            :ref="'inputRef_' + index"
                            size="small"
                            placeholder="请输入规格值"
                            v-model="formDynamic.attrsVal"
                            @keyup.enter.native="createAttr(formDynamic.attrsVal, index)"
                            @blur="createAttr(formDynamic.attrsVal, index)"
                            maxlength="30"
                            show-word-limit
                          >
                          </el-input>
                          <div class="addfont" slot="reference">添加规格值</div>
                        </el-popover>
                      </div>
                    </draggable>
                  </div>
                </div>
              </div>
            </draggable>
          </div>
          <div class="flex">
            <el-button
              v-if="
                (formValidate.type === OrderSecondTypeEnum.Reservation && formValidate.attrs.length === 0) ||
                formValidate.type !== OrderSecondTypeEnum.Reservation
              "
              @click="handleAddRole"
              >添加新规格</el-button
            >
            <el-button v-if="formValidate.attrs.length >= 1" @click="handleSaveAsTemplate()">另存为模板</el-button>
          </div>
        </el-form-item>
      </el-col>
      <!-- 预约商品的时段划分 -->
      <el-col :span="24" v-if="formValidate.type === OrderSecondTypeEnum.Reservation">
        <el-form-item label="时段划分：">
          <el-radio-group :disabled="isDisabled" v-model="formValidate.supplementReservation.timeSlotClassifyType">
            <el-radio :label="1">自动划分</el-radio>
            <el-radio :label="2">自定义划分</el-radio>
          </el-radio-group>
          <!----------- 自动划分 ---------->
          <div class="reservation-times-box font12" v-if="formValidate.supplementReservation.timeSlotClassifyType == 1">
            起止时间：
            <el-time-picker
              is-range
              value-format="HH:mm"
              format="HH:mm"
              placeholder="选择时间"
              v-model="formValidate.temporaryData.reservationTime"
              style="width: 200px"
              size="mini"
              class="mr10"
              :clearable="false"
              :disabled="isDisabled"
              @change="reservationTimesChange"
            ></el-time-picker>

            间隔时间：
            <el-input-number
              v-model="formValidate.supplementReservation.timeSlotSpan"
              :min="10"
              :max="1440"
              :precision="0"
              size="small"
              controls-position="right"
              class="mr10"
              :disabled="isDisabled"
            />
            <span class="from-tips mr10">支持设置10-1440分钟</span>
            <el-button
              size="small"
              :disabled="isDisabled"
              type="primary"
              v-debounceClick="generateTimeSlots"
              :class="{ flash: isFlashing }"
              >确认时间段</el-button
            >
            <div class="flex mt5" v-if="formValidate.timeSlotAutomatic.length">
              <el-checkbox v-model="timeCheckAll" @change="handleCheckAll" :indeterminate="isIndeterminate"
                >全选</el-checkbox
              >
              <el-checkbox-group v-model="timeCheckGroup" @change="checkAllGroupChange" class="flex-1">
                <el-checkbox
                  :label="item.timeSlot"
                  :key="item.timeSlot"
                  class="ml20"
                  v-for="(item, index) in formValidate.timeSlotAutomatic"
                  >{{ item.timeSlot }}</el-checkbox
                >
              </el-checkbox-group>
            </div>
          </div>
          <!----------------- 自定义划分 -------------------->
          <div class="reservation-times-box" v-if="formValidate.supplementReservation.timeSlotClassifyType == 2">
            <div class="from-tips">请依照时间的先后顺序添加时段，并且时段的开始时间不得早于上一个时段的结束时间。</div>
            <div class="acea-row mt10 customize-time">
              <div
                class="flex relative"
                v-for="(item, index) in formValidate.temporaryData.customizeTimeClone"
                :key="index"
              >
                <el-time-picker
                  is-range
                  value-format="HH:mm"
                  format="HH:mm"
                  placeholder="选择时间"
                  :key="index"
                  v-model="formValidate.temporaryData.customizeTimeClone[index]"
                  placement="bottom-end"
                  :clearable="false"
                  @change="changeCustomizeTime"
                  style="width: 200px"
                  size="mini"
                  class="mr10 mb10"
                ></el-time-picker>
                <div
                  @click.stop="closeTime(index)"
                  v-if="
                    formValidate.temporaryData.customizeTimeClone &&
                    formValidate.temporaryData.customizeTimeClone.length > 1
                  "
                  class="el-icon-error"
                ></div>
              </div>
              <span
                :disabled="isDisabled"
                class="add-time font12"
                @click="addTime"
                v-if="
                  formValidate.temporaryData.customizeTimeClone &&
                  formValidate.temporaryData.customizeTimeClone.length < 24
                "
                >添加时段（{{ formValidate.temporaryData.customizeTimeClone.length }}/24）</span
              >
              <el-button
                :disabled="isDisabled"
                :class="{ flash: isFlashing }"
                class="ml10 mb20"
                v-debounceClick="setCustomizeTime"
                size="mini"
                type="primary"
                >确认时间段</el-button
              >
            </div>
          </div>
        </el-form-item>
      </el-col>
      <el-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
        <!-- 单规格表格-->
        <el-form-item v-if="formValidate.specType === false">
          <el-alert title="价格设置范围 0.01~999999.99" type="info"> </el-alert>
          <el-table :data="OneattrValue" border class="tabNumWidth" size="small">
            <el-table-column label="图片" width="60" align="center">
              <template slot-scope="scope">
                <div class="upLoadPicBox" @click="modalPicTap(false, 'dan', 'pi')">
                  <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image" /></div>
                  <div v-else class="upLoad tabPic">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </template>
            </el-table-column>
            <template v-if="formValidate.isSub">
              <el-table-column label="一级返佣(%)" min-width="120" align="center">
                <template slot-scope="scope">
                  <el-input
                    maxlength="6"
                    :disabled="isDisabled"
                    v-model.trim="scope.row.brokerage"
                    min="0"
                    class="priceBox"
                    @keyup.native="keyupEventBrokerage('brokerage', scope.$index, 2)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="二级返佣(%)" min-width="120" align="center">
                <template slot-scope="scope">
                  <el-input
                    maxlength="6"
                    :disabled="isDisabled"
                    v-model.trim="scope.row.brokerageTwo"
                    min="0"
                    class="priceBox"
                    @keyup.native="keyupEventBrokerage('brokerageTwo', scope.$index, 2)"
                  />
                </template>
              </el-table-column>
            </template>
            <el-table-column
              v-for="(item, iii) in tableAttrValue"
              :key="iii"
              :label="oneAttrFormThead(iii)"
              min-width="160"
              align="center"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="iii === 'barCode' || iii === 'itemNumber'"
                  :disabled="isDisabled"
                  v-model="scope.row[iii]"
                  maxlength="40"
                  type="test"
                  class="priceBox"
                  :readonly="false"
                />
                <el-input-number
                  v-else
                  v-model.trim="scope.row[iii]"
                  :disabled="isDisabled || (iii === 'stock' && formValidate.type == 6 ? true : false)"
                  :min="iii === 'stock' || iii === 'weight' || iii === 'volume' ? 0 : 0.01"
                  :max="formValidate.vipPriceType && iii === 'vipPrice' ? Number(scope.row.price) - 0.01 : 999999.99"
                  :step="iii === 'stock' ? 1 : 0.01"
                  controls-position="right"
                  step-strictly
                ></el-input-number>
              </template>
            </el-table-column>
            <template v-if="formValidate.type == 5">
              <el-table-column label="云盘设置" min-width="120" align="center">
                <template slot-scope="scope">
                  <el-button v-if="!scope.row.expand" size="small" @click="handleAddVirtually(0, 'OneattrValue', 1)"
                    >添加链接</el-button
                  >
                  <el-button
                    v-else
                    class="seeCatMy pointer"
                    type="text"
                    size="small"
                    @click="seeVirtually(OneattrValue[0], 'OneattrValue', 0, 1)"
                    >已设置</el-button
                  >
                </template>
              </el-table-column>
            </template>
            <template v-if="formValidate.type == 6">
              <el-table-column label="卡密设置" min-width="120" align="center">
                <template slot-scope="scope">
                  <el-button v-if="!scope.row.cdkeyId" size="small" @click="handleAddVirtually(0, 'OneattrValue', 2)"
                    >添加卡密</el-button
                  >
                  <el-button
                    v-else
                    type="text"
                    class="seeCatMy pointer"
                    @click="seeVirtually(OneattrValue[0], 'OneattrValue', 0, 2)"
                    >{{ scope.row.cdkeyLibraryName }}</el-button
                  >
                </template>
              </el-table-column>
            </template>
            <template v-if="formValidate.type == 7">
              <el-table-column label="预约设置" min-width="180" align="center">
                <template slot-scope="scope">
                  <el-button
                    size="small"
                    @click="handleSetReservationNum(0, OneattrValue)"
                    :class="{ flash: isFlashing }"
                    >设置可约数量</el-button
                  >
                </template>
              </el-table-column>
            </template>
          </el-table>
        </el-form-item>
        <!-- 多规格表格-->
        <el-form-item
          v-if="formValidate.attrs.length > 0 && formValidate.specType"
          label="商品属性："
          class="labeltop"
          :class="isDisabled ? 'disLabel' : 'disLabelmoren'"
        >
          <el-alert title="价格设置范围 0.01~999999.99" type="info"> </el-alert>
          <el-table
            :data="ManyAttrValue"
            border
            class="tabNumWidth"
            size="small"
            :span-method="objectSpanMethod"
            :cell-class-name="tableCellClassName"
            :key="tableKey"
          >
            <el-table-column
              v-for="(item, index) in formValidate.header"
              :key="index"
              :label="item.title"
              :min-width="item.minWidth || '200'"
            >
              <template slot-scope="scope">
                <!-- 批量设置 -->
                <template v-if="scope.$index == 0">
                  <template v-if="item.key">
                    <div
                      v-if="formValidate.attrs.length && formValidate.attrs[scope.column.index] && ManyAttrValue.length"
                    >
                      <el-select v-model="oneFormBatch[0][item.title]" :placeholder="`请选择${item.title}`" clearable>
                        <el-option
                          v-for="val in formValidate.attrs[scope.column.index].detail"
                          :key="val.value"
                          :label="val.value"
                          :value="val.value"
                        >
                        </el-option>
                      </el-select>
                    </div>
                  </template>
                  <template v-else-if="item.slot === 'image'">
                    <div class="upLoadPicBox specPictrue" @click="modalPicTap(false, 'pi', scope.$index)">
                      <div class="upLoad tabPic" v-if="oneFormBatch[0].image">
                        <img v-lazy="oneFormBatch[0].image" />
                      </div>
                      <div class="upLoad tabPic" v-else>
                        <i class="el-icon-camera cameraIconfont"></i>
                      </div>
                    </div>
                  </template>
                  <template v-else-if="item.slot === 'brokerage' && formValidate.isSub">
                    <el-input v-model="oneFormBatch[0].brokerage"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'brokerageTwo' && formValidate.isSub">
                    <el-input v-model="oneFormBatch[0].brokerageTwo"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'price'">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].price"
                      :min="0.01"
                      :max="999999.99"
                      :step="0.01"
                      controls-position="right"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'vipPrice' && formValidate.vipPriceType">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].vipPrice"
                      :min="0.01"
                      :max="999999.99"
                      :step="0.01"
                      controls-position="right"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'cost'">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].cost"
                      :min="0.01"
                      :max="999999.99"
                      :step="0.01"
                      controls-position="right"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'otPrice'">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].otPrice"
                      :min="0.01"
                      :max="999999.99"
                      :step="0.01"
                      controls-position="right"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'stock' && formValidate.type !== OrderSecondTypeEnum.Reservation">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].stock"
                      :disabled="formValidate.type === OrderSecondTypeEnum.CardPassword ? true : false"
                      :min="0"
                      :step="1"
                      controls-position="right"
                      :max="999999"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'fictitious'"> -- </template>
                  <template v-else-if="item.slot === 'barCode'">
                    <el-input v-model="oneFormBatch[0].barCode" maxlength="40" class="priceBox"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'itemNumber' && formValidate.type === OrderSecondTypeEnum.Normal">
                    <el-input v-model="oneFormBatch[0].itemNumber" maxlength="40" class="priceBox"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'weight' && formValidate.type !== OrderSecondTypeEnum.Reservation">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].weight"
                      :step="0.1"
                      :min="0"
                      :max="999999"
                      controls-position="right"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'volume' && formValidate.type !== OrderSecondTypeEnum.Reservation">
                    <el-input-number
                      :controls="false"
                      v-model="oneFormBatch[0].volume"
                      :step="0.1"
                      :min="0"
                      :max="999999"
                      controls-position="right"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template
                    v-else-if="
                      item.slot === 'attrValueReservationList' && formValidate.type === OrderSecondTypeEnum.Reservation
                    "
                  >
                    <el-button
                      size="small"
                      :disabled="isDisabled"
                      @click="handleSetReservationNum(scope.$index, ManyAttrValue, 'batch')"
                      :class="{ flash: isFlashing }"
                      >批量可约数量</el-button
                    >
                  </template>
                  <template v-else-if="item.slot === 'isDefault'"> -- </template>
                  <template v-else-if="item.slot === 'action'" :disabled="isDisabled">
                    <a type="text" size="mini" @click="batchAdd">批量修改</a>
                    <a type="text" size="mini" @click="batchDel" class="ml10">清空</a>
                  </template>
                </template>
                <template v-else>
                  <template v-if="item.key">
                    <div class="text-center">
                      <span>{{ scope.row.attrValueShow[item.key] }}</span>
                    </div>
                  </template>
                  <template v-if="item.slot === 'image'">
                    <div class="upLoadPicBox specPictrue" @click="modalPicTap(false, 'duo', scope.$index)">
                      <div class="upLoad tabPic" v-if="scope.row.image">
                        <el-image
                          :src="scope.row.image"
                          :preview-src-list="isDisabled ? [scope.row.image] : []"
                          fit="cover"
                        ></el-image>
                      </div>
                      <div class="upLoad tabPic" v-else>
                        <i class="el-icon-camera cameraIconfont" style="font-size: 24px"></i>
                      </div>
                    </div>
                  </template>
                  <template v-if="item.slot === 'price'">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].price"
                      :min="0.01"
                      :max="999999.99"
                      class="priceBox"
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'vipPrice' && formValidate.vipPriceType">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].vipPrice"
                      :min="0.01"
                      :max="999999.99"
                      class="priceBox"
                      clearable
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'cost'">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].cost"
                      :min="0.01"
                      :max="999999.99"
                      class="priceBox"
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'otPrice'">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].otPrice"
                      :min="0.01"
                      :max="999999.99"
                      class="priceBox"
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'stock' && formValidate.type !== OrderSecondTypeEnum.Reservation">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].stock"
                      :disabled="formValidate.type === OrderSecondTypeEnum.CardPassword ? true : false"
                      :min="0"
                      :max="999999"
                      :precision="0"
                      class="priceBox"
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'barCode'">
                    <el-input v-model="ManyAttrValue[scope.$index].barCode" maxlength="40" class="priceBox"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'itemNumber' && formValidate.type === OrderSecondTypeEnum.Normal">
                    <el-input
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].itemNumber"
                      maxlength="40"
                      class="priceBox"
                    ></el-input>
                  </template>
                  <template v-else-if="item.slot === 'brokerage' && formValidate.isSub">
                    <el-input v-model="ManyAttrValue[scope.$index].brokerage"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'brokerageTwo' && formValidate.isSub">
                    <el-input v-model="ManyAttrValue[scope.$index].brokerageTwo"></el-input>
                  </template>
                  <template v-else-if="item.slot === 'weight' && formValidate.type !== OrderSecondTypeEnum.Reservation">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].weight"
                      :step="0.1"
                      :max="999999"
                      class="priceBox"
                    ></el-input-number>
                  </template>
                  <template v-else-if="item.slot === 'volume' && formValidate.type !== OrderSecondTypeEnum.Reservation">
                    <el-input-number
                      :controls="false"
                      v-model="ManyAttrValue[scope.$index].volume"
                      :step="0.1"
                      :min="0"
                      :max="999999"
                      class="priceBox"
                    ></el-input-number>
                  </template>
                  <template
                    v-else-if="item.slot === 'fictitious' && formValidate.type == OrderSecondTypeEnum.CloudDrive"
                  >
                    <div>
                      <el-button
                        v-if="!scope.row.expand"
                        size="small"
                        :disabled="isDisabled"
                        @click="handleAddVirtually(scope.$index, 'ManyAttrValue', 1)"
                        >添加链接</el-button
                      >
                      <el-button
                        v-else
                        class="seeCatMy pointer"
                        type="text"
                        size="small"
                        @click="seeVirtually(ManyAttrValue[scope.$index], 'ManyAttrValue', scope.$index, 1)"
                        >已设置</el-button
                      >
                    </div>
                  </template>
                  <template
                    v-else-if="item.slot === 'fictitious' && formValidate.type == OrderSecondTypeEnum.CardPassword"
                    slot-scope="scope"
                  >
                    <div>
                      <el-button
                        v-if="!scope.row.cdkeyId"
                        size="small"
                        :disabled="isDisabled"
                        @click="handleAddVirtually(scope.$index, 'ManyAttrValue', 2)"
                        >添加卡密</el-button
                      >
                      <el-button
                        v-else
                        type="text"
                        class="seeCatMy pointer"
                        :disabled="isDisabled"
                        @click="seeVirtually(ManyAttrValue[scope.$index], 'ManyAttrValue', scope.$index, 2)"
                        >{{ scope.row.cdkeyLibraryName }}</el-button
                      >
                    </div>
                  </template>
                  <template
                    v-else-if="
                      item.slot === 'attrValueReservationList' && formValidate.type === OrderSecondTypeEnum.Reservation
                    "
                  >
                    <el-button
                      size="small"
                      :disabled="isDisabled"
                      @click="handleSetReservationNum(scope.$index, ManyAttrValue)"
                      :class="{ flash: isFlashing }"
                      >设置可约数量</el-button
                    >
                  </template>
                  <template v-else-if="item.slot === 'isDefault'">
                    <el-switch
                      v-model="ManyAttrValue[scope.$index].isDefault"
                      :active-value="true"
                      :inactive-value="false"
                      active-text="默认"
                      :disabled="isDisabled"
                      @change="(e) => changeDefaultSelect(e, scope.$index)"
                    />
                  </template>
                  <template v-else-if="item.slot === 'action'">
                    <el-switch
                      class="defineSwitch"
                      v-model="ManyAttrValue[scope.$index].isShow"
                      active-text="显示"
                      inactive-text="隐藏"
                      :active-value="true"
                      :inactive-value="false"
                      :disabled="isDisabled"
                      @change="changeDefaultShow(scope.$index)"
                    />
                  </template>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-col>
    </el-row>
    <!-- 添加云盘-->
    <add-cloud-disk
      ref="cloudDiskRef"
      :cloudDisk="cloudDisk"
      @handlerSubSuccess="handlerSubSuccessCloudDisk"
      @closeCloudDisk="closeCloudDisk"
    ></add-cloud-disk>

    <!-- 选择卡密库-->
    <cdkey-library
      ref="cdkeyLibraryRef"
      :cdkeyLibraryInfo="cdkeyLibraryInfo"
      @handlerSubSuccess="handlerChangeCdkeyIdSubSuccess"
    ></cdkey-library>

    <!-- 可预约设置弹窗 -->
    <reservationStockDialog
      ref="reservationStock"
      :dialogVisible="dialogVisible"
      :reservationData="reservationData"
      :specType="formValidate.specType"
      @submitOk="handleSubmitOk"
      @close="handleClose"
    />
  </div>
</template>

<script>
import addCdkey from '@/views/product/components/addCdkey.vue';
import addCloudDisk from '@/views/product/components/addCloudDisk.vue';
import vuedraggable from 'vuedraggable';
import { attrCreatApi, templateListApi } from '@/api/product';
import { arraysEqual } from '@/utils';
import product from '@/mixins/product';
import { defaultObj } from '../creatProduct/default';
import CdkeyLibrary from '@/views/product/components/cdkeyLibrary';
import reservationStockDialog from './reservationStockDialog.vue';
import { Debounce } from '@/utils/validate';
import {
  commissionTableHead,
  FictitiousTableHead,
  GoodsTableHead,
  imageTableHead,
  reservationTableHeard,
  vipPriceTableHead,
  VirtualTableHead,
  VirtualTableHead2,
} from '../creatProduct/TableHeadList.js';
import { OrderSecondTypeEnum } from '@/enums/productEnums';
export default {
  name: 'creatAttr',
  mixins: [product], //此js存放商品的部分函数方法
  components: {
    draggable: vuedraggable,
    addCdkey,
    addCloudDisk,
    CdkeyLibrary,
    reservationStockDialog,
  },
  computed: {
    tableAttrValue() {
      const obj = Object.assign({}, defaultObj.attrValueList[0]);
      delete obj.isShow;
      delete obj.image;
      delete obj.brokerage;
      delete obj.brokerageTwo;
      delete obj.cdkeyId;
      delete obj.expand;
      delete obj.cdkeyLibraryName;
      delete obj.attrValueReservationList;
      if (!this.formValidate.vipPriceType) {
        delete obj.vipPrice;
      } else {
        obj.vipPrice = Object.assign({}, defaultObj.attrValueList[0]).vipPrice;
      }
      if (this.formValidate.type === 2) {
        delete obj.itemNumber;
      }
      if (this.formValidate.type === 5) {
        delete obj.volume;
        delete obj.weight;
        delete obj.itemNumber;
      }
      if (this.formValidate.type === 6) {
        delete obj.volume;
        delete obj.weight;
        delete obj.itemNumber;
      }
      if (this.formValidate.type === 7) {
        delete obj.volume;
        delete obj.weight;
        delete obj.stock;
        delete obj.itemNumber;
      }
      return obj;
    },
    // 单规格表头
    oneAttrFormThead() {
      return (index) => {
        if (this.formThead[index].title == '会员价（元）' && this.formValidate.vipPriceType == 1) {
          return 'svip价'
        } else if (this.formThead[index].title == '会员价（元）' && this.formValidate.vipPriceType == 2) {
          return '商户会员价'
        } else {
          return this.formThead[index].title
        }
      }
    }
  },
  data() {
    return {
      OrderSecondTypeEnum: OrderSecondTypeEnum,
      carMytabIndex: 0,
      carMytabName: '',
      cdkeyId: null, //卡密库id
      cdkeyLibraryInfo: null, //卡密库对象
      cloudDisk: '', //云盘地址
      formValidate: this.value,
      ruleList: [],
      // // 规格数据
      formDynamic: {
        attrsName: '',
        attrsVal: '',
      },
      formDynamics: {
        ruleName: '',
        ruleValue: [],
      },
      tableFrom: {
        page: 1,
        limit: 9999,
        keywords: '',
      },
      columnsInstalM: [],
      changeAttrValue: '', //修改的规格值
      currentIndex: 0,
      canSel: true, // 规格图片添加判断
      tableKey: 0,

      // timeCheckGroup: [], //自动划分当前选中的元素
      timeCheckAllClone: [], //自动划分克隆全部选中的元素
      timeDataClone: [], //自动划分时的库存（为了切换时段划分时，可以复原之前选中的数据）
      // customizeTimeClone: [['', '']], // 自定义划分库存
      // reservationTime: ['', ''], // 自动划分起止时间
      timeCheckAll: false, //是否全选
      // timeSlotList: [], // 生成的时间段
      isIndeterminate: false,
      dialogVisible: false, // 预约弹窗
      reservationData: [], // 设置预约库存
      customizeTimeData: [], //自定义时间段
      isCustomize: false, //自定义时间段开关，true自定义开关开启
      isFlashing: false,
      punchCardTimeVal: [],
      // 定义日期选择器的限制选项
      pickerOptions: {
        disabledDate: (time) => {
          // 方式2：禁用今天之前，但允许选择今天（推荐）
          return time.getTime() < Date.now() - 8.64e7; // 8.64e7毫秒 = 1天
        },
      },
    };
  },
  props: {
    value: {
      type: Object,
      default: function () {
        return {};
      },
    },
    // 生成规格表格中的头部标题
    manyTabDate: {
      type: Object,
      default: function () {
        return {};
      },
    },
    //生成规格的表格中填写的值
    formThead: {
      type: Object,
      default: function () {
        return {};
      },
    },
    //生成表格的头部标题
    manyTabTit: {
      type: Object,
      default: function () {
        return {};
      },
    },
    isDisabled: {
      type: Boolean,
      default: false,
    },
    // 单规格
    OneattrValue: {
      type: Array,
      default: function () {
        return [];
      },
    },
    // 多规格
    ManyAttrValue: {
      type: Array,
      default: function () {
        return [];
      },
    },
    //批量添加规格
    oneFormBatch: {
      type: Array,
      default: function () {
        return [];
      },
    },
  },
  watch: {
    formValidate: {
      handler(newVal) {
        this.formValidate = newVal;
        // this.$emit('input', newVal);
      },
      // deep: true,
    },
    punchCardTimeVal(nval) {
      this.formValidate.verifyTimeLimitStartDate = nval[0];
      this.formValidate.verifyTimeLimitEndDate = nval[1];
    },
  },
  mounted() {
    this.getTimeCheckGroup();
    //预约商品
    if (this.$route.params.id && this.$route.params.id != 0 && this.formValidate.type === 7) {
      this.getReservationInfosss();
    }
    //次卡商品固定有效期
    if (this.formValidate.type === 8) {
      this.punchCardTimeVal = [this.formValidate.verifyTimeLimitStartDate, this.formValidate.verifyTimeLimitEndDate];
    }
    if (this.formValidate.specType) this.productGetRule(); //加载商品规格选项

    if (
      this.formValidate.specType &&
      ((this.$route.params.id && this.$route.params.id != 0) || this.$route.params.isCopy === '1')
    ) {
      this.createBnt = true;
      // 采集商品只有规格，没有规格列表，这里判断使用 方法，attrValueList.length为0，走生成规格方法
      if (this.formValidate.attrValueList.length) {
        this.generateHeader(this.formValidate.attrs);
      } else {
        this.generateAttr(this.formValidate.attrs);
      }
    }
  },
  methods: {
    /***************预约的业务*****************/
    // 编辑获取预约商品详情数据
    getReservationInfosss() {
      //预约商品时段划分类型：1-自动，2-自定义 , 编辑中赋值设置的时间段
      let timeSlotLists = this.formValidate.timeSlotList.map((item) => item.timeSlot);
      if (this.formValidate.supplementReservation.timeSlotClassifyType === 1) {
        //  this.formValidate.timeSlotAutomatic = this.formValidate.timeSlotList;
        //   this.isCustomize = false;
        // this.timeCheckGroup = timeSlotLists;
      } else {
        this.isCustomize = true;
        const formattedTimes = timeSlotLists.map((time) => time.split('-'));
        //    this.formValidate.temporaryData.customizeTimeClone = formattedTimes;
        this.customizeTimeData = timeSlotLists;
      }
    },
    // 自定义添加时段；
    addTime() {
      if (this.isDisabled) return;
      let customizeTime = this.formValidate.temporaryData.customizeTimeClone;
      for (let i = 0; i < customizeTime.length; i++) {
        if (!customizeTime[i][0]) {
          return this.$message.warning('请选择时段');
        }
      }
      if (this.intersection(customizeTime)) {
        return this.$message.warning('时段必须递增无交集');
      }
      this.formValidate.temporaryData.customizeTimeClone.push(['', '']);
    },
    // 判断交集和递增；
    intersection(customizeTime) {
      let intersection = this.$selfUtil.hasIntersection(customizeTime); //是否有交集
      return intersection;
    },
    // 自定义划分 时间选择
    changeCustomizeTime(e) {
      let customizeTime = this.formValidate.temporaryData.customizeTimeClone;
      customizeTime[customizeTime.length - 1] = e;
    },
    closeTime(index) {
      this.formValidate.temporaryData.customizeTimeClone.splice(index, 1);
    },
    //预约设置库存回调
    handleSubmitOk(data) {
      if (this.formValidate.specType) {
        this.ManyAttrValue[this.carMytabIndex] = data[0];
        this.$nextTick(() => {
          this.$emit('changeOneattrValue', [...this.ManyAttrValue]);
        });
      } else {
        this.$nextTick(() => {
          this.$emit('changeManyAttrValue', [...data]);
        });
      }
      this.dialogVisible = false;
    },
    // 关闭弹窗
    handleClose() {
      this.dialogVisible = false;
    },
    // 获取可预约数量的数据
    gettReservationNumData(index, attrValue, name) {
      let timeList = this.isCustomize ? this.customizeTimeData : this.timeCheckGroup;
      // 设置数量跟时间，用一个updateList函数复用批量和单个逻辑。
      const updateList = (item) => {
        const oldList = item.attrValueReservationList || [];
        item.attrValueReservationList = timeList.map((time) => {
          // 查找有没有时间，有的话就是设置过数量的，没有的话就默认为0
          const old = oldList.find((i) => i.timeSlot === time);
          return { stock: old ? old.stock : 0, timeSlot: time };
        });
      };
      let attrValueNewBatch = this.formValidate.specType ? [...attrValue.slice(1)] : [...attrValue]; // 批量使用的数组
      let attrValueNew = [...attrValue]; // 单条使用的数组

      // batch为批量，批量与单独设置的区别，批量设置的是全部的sku，单独设置的是当前这条的属性
      if (name === 'batch') {
        attrValueNewBatch.forEach(updateList);
        this.reservationData = attrValueNewBatch;
      } else {
        updateList(attrValueNew[index]);
        this.reservationData = [attrValueNew[index]];
      }
    },
    // 设置可预约的数量
    handleSetReservationNum: Debounce(function (index, attrValue, name) {
      // return;
      // this.isCustomize为true是自定义划分，false是自动划分
      let timeList = this.isCustomize ? this.customizeTimeData : this.timeCheckGroup;
      if (timeList.length === 0) {
        this.isFlashing = true;
        this.$message.warning('请先设置时段划分');
        setTimeout(() => {
          this.isFlashing = false; // 可选：自动停止闪烁
        }, 5000); // 闪烁时间，这里设置为2秒
        return;
      }
      this.gettReservationNumData(index, attrValue, name);
      this.carMytabIndex = index;
      this.carMytabName = name;
      this.dialogVisible = true;
    }),
    // 设置预约库存弹窗生成时间、库存，只在新增中调用， 设置数量跟时间，用一个updateList函数复用批量和单个逻辑。
    updateList(item) {
      const oldList = item.attrValueReservationList || [];
      item.attrValueReservationList = timeList.map((time) => {
        // 查找有没有时间，有的话就是设置过数量的，没有的话就默认为0
        const old = oldList.find((i) => i.timeSlot === time);
        return { stock: old ? old.stock : 0, timeSlot: time };
      });
    },
    //全选按钮
    handleCheckAll(val) {
      this.formValidate.timeSlotAutomatic.forEach((item) => {
        item.isCheck = val ? 1 : 0;
      });
      this.isIndeterminate = false;
      this.getTimeCheckGroup();
    },
    // 单独勾选按钮
    checkAllGroupChange(value) {
      const checkedCount = value.length;
      this.timeCheckAll = checkedCount === this.formValidate.timeSlotAutomatic.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.formValidate.timeSlotAutomatic.length;
      this.setTimeSlotAutomatic(value);
    },
    // 获取选中的对象
    setTimeSlotAutomatic(value) {
      this.formValidate.timeSlotAutomatic.forEach((item) => {
        item.isCheck = value.includes(item.timeSlot) ? 1 : 0;
      });
    },
    // 设置时间段展示
    /**
     * 生成时间段区间数组
     * @param {string} start - 起始时间 "HH:mm"
     * @param {string} end - 结束时间 "HH:mm"
     * @param {number} interval - 间隔分钟数
     * @returns {string[]} 时间段区间数组
     */
    generateTimeRanges(start, end, interval) {
      const result = [];
      let [sh, sm] = start.split(':').map(Number);
      let [eh, em] = end.split(':').map(Number);
      let startMinutes = sh * 60 + sm;
      const endMinutes = eh * 60 + em;
      while (startMinutes + interval <= endMinutes) {
        const h1 = String(Math.floor(startMinutes / 60)).padStart(2, '0');
        const m1 = String(startMinutes % 60).padStart(2, '0');
        const h2 = String(Math.floor((startMinutes + interval) / 60)).padStart(2, '0');
        const m2 = String((startMinutes + interval) % 60).padStart(2, '0');
        result.push({
          timeSlot: `${h1}:${m1}-${h2}:${m2}`,
          isCheck: 1,
        });
        startMinutes += interval;
      }
      return result;
    },
    // 保存生成时间段
    generateTimeSlots() {
      this.isCustomize = false;
      if (!this.formValidate.temporaryData.reservationTime[0]) return this.$message.warning('起止时间段不能为空');
      let start_time = this.formValidate.temporaryData.reservationTime[0];
      let end_time = this.formValidate.temporaryData.reservationTime[1];
      let slots = this.generateTimeRanges(start_time, end_time, this.formValidate.supplementReservation.timeSlotSpan);
      // 按时间正序排序
      slots.sort((a, b) => {
        const getStart = (str) => {
          const [h, m] = str.timeSlot.split('-')[0].split(':').map(Number);
          return h * 60 + m;
        };
        return getStart(a) - getStart(b);
      });
      this.formValidate.timeSlotAutomatic = slots;
      const lal = slots.filter((item) => item.isCheck === 1).map((item) => item.timeSlot);
      this.timeCheckGroup = lal;
      this.isIndeterminate = false;
      this.timeCheckAll = true;
      const attrValue = this.formValidate.specType ? this.ManyAttrValue : this.OneattrValue;
      this.gettReservationNumData(0, attrValue, 'batch');
      this.$message.success('设置成功');
    },
    // 选择起止时间
    reservationTimesChange(value) {
      // 判断选择的时间间隔不能小于10分钟
      if (value && value.length === 2) {
        const startTime = new Date(`1970-01-01T${value[0]}`);
        const endTime = new Date(`1970-01-01T${value[1]}`);
        const diff = (endTime - startTime) / 1000 / 60; // 转换为分钟
        if (diff < 10) {
          this.$message.error('时间间隔不能小于10分钟');
          // 可以选择重置时间范围或者调整其中一个时间值以符合要求
          this.formValidate.temporaryData.reservationTime = ['', '']; // 清空时间范围，让用户重新选择
        } else {
          this.formValidate.supplementReservation.timeSlotStartEndTime =
            this.formValidate.temporaryData.reservationTime.join(',');
        }
      }
    },
    // 自定义划分设置按钮
    setCustomizeTime() {
      for (let i = 0; i < this.formValidate.temporaryData.customizeTimeClone.length; i++) {
        if (!this.formValidate.temporaryData.customizeTimeClone[i][0]) {
          return this.$message.error('请选择时段');
        }
      }
      if (this.intersection(this.formValidate.temporaryData.customizeTimeClone)) {
        return this.$message.error('时段必须无重叠');
      }
      this.customizeTimeData = [];
      this.formValidate.timeSlotCustomize = [];
      this.formValidate.temporaryData.customizeTimeClone.forEach((item) => {
        this.customizeTimeData.push(`${item[0]}-${item[1]}`);
        this.formValidate.timeSlotCustomize.push({
          timeSlot: `${item[0]}-${item[1]}`,
          isCheck: 1,
        });
      });
      // 按时间正序排序
      this.customizeTimeData.sort((a, b) => {
        const getStart = (str) => {
          const [h, m] = str.split('-')[0].split(':').map(Number);
          return h * 60 + m;
        };
        return getStart(a) - getStart(b);
      });
      this.isCustomize = true; //自定义划分开启
      const attrValue = this.formValidate.specType ? this.ManyAttrValue : this.OneattrValue;
      this.gettReservationNumData(0, attrValue, 'batch');
      this.$message.success('设置成功');
      this.$emit('input', this.formValidate);
    },
    /***************预约的业务*****************/

    handleShowPop(index) {
      this.$refs['inputRef_' + index][0].focus();
    },
    // 删除属性
    handleRemove2(item, index, val) {
      item.splice(index, 1);
      this.delAttrTable(val);
      this.$emit('input', this.formValidate);
    },
    // 新增规格
    handleAddRole() {
      let data = {
        value: this.formDynamic.attrsName,
        add_pic: 0,
        detail: [],
      };

      this.$nextTick(() => {
        this.formValidate.attrs.push(data);
        this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
        this.$emit('input', this.formValidate);
      });
    },
    // 规格名称改变
    attrChangeValue(i, val) {
      if (val.trim().length && this.formValidate.attrs[i].detail.length) {
        this.generateHeader(this.formValidate.attrs);
        if (this.ManyAttrValue.length) {
          this.ManyAttrValue.map((item, i) => {
            if (i > 0) {
              if (Object.keys(item.attrValueShow).includes(this.changeAttrValue)) {
                item.attrValueShow[val] = item.attrValueShow[this.changeAttrValue];
                item[val] = item[this.changeAttrValue];
                delete item.attrValueShow[this.changeAttrValue];
                delete item[this.changeAttrValue];
              }
            }
          });
          this.changeAttrValue = val;
        }
      } else {
        this.generateAttr(this.formValidate.attrs);
      }
      // 触发父组件数据同步
      this.$emit('input', this.formValidate);
    },
    // 规格值改变
    attrDetailChangeValue(val, i) {
      if (this.ManyAttrValue.length) {
        let key = this.formValidate.attrs[i].value;
        this.ManyAttrValue.map((item, i) => {
          if (i > 0) {
            if (Object.keys(item.attrValueShow).includes(key) && item.attrValueShow[key] === this.changeAttrValue) {
              item.attrValueShow[key] = val;
              let index = item.attr_arr.findIndex((item) => item === this.changeAttrValue);
              item.attr_arr[index] = val;
            }
          }
        });
        this.changeAttrValue = val;
      } else {
        this.generateAttr(this.formValidate.attrs, 1);
      }
      this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
      this.$emit('input', this.formValidate);
      // this.$nextTick(() => {
      //   this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
      //   this.$emit('input', this.formValidate);
      // });
    },
    changeCurrentIndex(i) {
      this.currentIndex = i;
    },
    // 新增一条属性
    addOneAttr(val, val2) {
      this.generateAttr(this.formValidate.attrs, val2);
    },
    handleFocus(val) {
      this.changeAttrValue = val;
    },
    handleBlur() {
      this.changeAttrValue = '';
    },
    // 规格拖拽排序后
    onMoveSpec() {
      this.generateAttr(this.formValidate.attrs);
    },
    handleRemoveImg(item) {
      item.image = '';
      this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
    },
    // 合并单元格
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0 && rowIndex > 0) {
        let lable = column.label;
        //这里判断第几列需要合并
        const tagFamily = this.ManyAttrValue[rowIndex].attrValueShow[lable];
        const index = this.ManyAttrValue.findIndex((item, index) => {
          if (index > 0) return item.attrValueShow[lable] == tagFamily;
        });
        if (rowIndex == index) {
          let len = 1;
          for (let i = index + 1; i < this.ManyAttrValue.length; i++) {
            if (this.ManyAttrValue[i].attrValueShow[lable] !== tagFamily) {
              break;
            }
            len++;
          }
          return {
            rowspan: len,
            colspan: 1,
          };
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          };
        }
      }
    },
    /*
     * 生成属性
     * @param {Array} data 规格数据
     * */
    generateAttr(data, val) {
      // 判断该段Js执行时间
      this.generateHeader(data);
      const combinations = this.generateCombinations(data);
      let rows = combinations.map((combination) => {
        const row = {
          attr_arr: combination,
          attrValueShow: {},
          image: '',
          price: 0.01,
          vipPrice: 0.01,
          cost: 0.01,
          otPrice: 0.01,
          barCode: '',
          itemNumber: '',
          stock: 0,
          weight: 0,
          volume: 0,
          brokerage: 0,
          brokerageTwo: 0,
          cdkeyId: null,
          cdkeyLibraryName: '',
          expand: '',
          isShow: true,
          attrValueReservationList: [],
        };
        for (let i = 0; i < combination.length; i++) {
          const value = combination[i];
          this.$set(row, data[i].value, value);
          this.$set(row, 'title', data[i].value);
          this.$set(row, 'key', data[i].value);
          this.$set(row.attrValueShow, data[i].value, value);
          // 如果manyFormValidate中存在该属性值，则赋值
          for (let k = 0; k < this.ManyAttrValue.length; k++) {
            const manyItem = this.ManyAttrValue[k];
            // 对比两个数组是否完全相等
            if (k > 0 && manyItem.attr_arr && arraysEqual(manyItem.attr_arr, combination)) {
              Object.assign(row, {
                price: manyItem.price,
                cost: manyItem.cost,
                otPrice: manyItem.otPrice,
                stock: manyItem.stock,
                image: manyItem.image,
                sku: manyItem.sku || '',
                weight: manyItem.weight || 0,
                isDefault: manyItem.isDefault || 0,
                volume: manyItem.volume || 0,
                barCode: manyItem.barCode || '',
                itemNumber: manyItem.itemNumber || '',
                brokerage: manyItem.brokerage,
                brokerageTwo: manyItem.brokerageTwo,
                vipPrice: manyItem.vipPrice,
                attrValueReservationList: manyItem.attrValueReservationList || [],
                cdkeyId: manyItem.cdkeyId || '',
                cdkey: manyItem.cdkey || {},
                expand: manyItem.expand || '',
                isShow: manyItem.isShow,
                cdkeyLibraryName: manyItem.cdkeyLibraryName || '',
              });
            } else if (k > 0 && manyItem.attr_arr.length && data[i].add_pic && combination.includes(val)) {
              // data[i].detail中的value是规格值 存在与 manyItem.attr_arr 中的某一项
              data[i].detail.map((e, ii) => {
                combination.includes(e.value) && this.$set(row, 'image', e.image);
              });
            }
          }
        }
        return row;
      });
      this.$nextTick(() => {
        // rows数组第一项 新增默认数据 oneFormBatch
        this.$emit('changeManyAttrValue', [...this.oneFormBatch, ...rows]);
      });
    },
    //分佣设置 会员商品操作
    changeAttrVal() {
      this.generateHeader(this.formValidate.attrs);
    },
    // 生成商品规格表头
    generateHeader(data) {
      let specificationsColumns = data.map((item) => ({
        title: item.value,
        key: item.value,
        minWidth: 200,
        fixed: 'left',
      }));
      let arr;
      //云盘商品
      if (this.formValidate.type == this.OrderSecondTypeEnum.CloudDrive) {
        // 开启分佣设置
        if (this.formValidate.isSub && this.formValidate.vipPriceType) {
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...commissionTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...VirtualTableHead,
          ];
        } else if (this.formValidate.isSub) {
          arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...VirtualTableHead];
        } else if (this.formValidate.vipPriceType) {
          //开启会员
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...VirtualTableHead,
          ];
        } else {
          arr = [...specificationsColumns, ...imageTableHead, ...VirtualTableHead];
        }
        // 找到slot 等于 fictitious 将title改为规格名称
        this.formValidate.header.map((item) => {
          if (item.slot === 'fictitious') {
            item.title = '云盘设置';
          }
        });
      } else if (this.formValidate.type == this.OrderSecondTypeEnum.CardPassword) {
        //卡密商品
        // 开启分佣设置
        if (this.formValidate.isSub && this.formValidate.vipPriceType) {
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...commissionTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...VirtualTableHead2,
          ];
        } else if (this.formValidate.isSub) {
          arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...VirtualTableHead2];
        } else if (this.formValidate.vipPriceType) {
          //开启会员
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...VirtualTableHead2,
          ];
        } else {
          arr = [...specificationsColumns, ...imageTableHead, ...VirtualTableHead2];
        }
      } else if (this.formValidate.type == this.OrderSecondTypeEnum.Fictitious) {
        //虚拟商品
        // 开启分佣设置
        if (this.formValidate.isSub && this.formValidate.vipPriceType) {
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...commissionTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...FictitiousTableHead,
          ];
        } else if (this.formValidate.isSub) {
          arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...FictitiousTableHead];
        } else if (this.formValidate.vipPriceType) {
          //开启会员
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...FictitiousTableHead,
          ];
        } else {
          arr = [...specificationsColumns, ...imageTableHead, ...FictitiousTableHead];
        }
      } else if (this.formValidate.type == this.OrderSecondTypeEnum.Reservation) {
        // 预约
        if (this.formValidate.isSub && this.formValidate.vipPriceType) {
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...commissionTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...reservationTableHeard,
          ];
        } else if (this.formValidate.vipPriceType) {
          //开启会员
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...reservationTableHeard,
          ];
        } else if (this.formValidate.isSub) {
          arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...FictitiousTableHead];
        } else {
          arr = [...specificationsColumns, ...imageTableHead, ...reservationTableHeard];
        }
      } else {
        // 开启分佣设置
        if (this.formValidate.isSub && this.formValidate.vipPriceType) {
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...commissionTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...GoodsTableHead,
          ];
        } else if (this.formValidate.isSub) {
          arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...GoodsTableHead];
        } else if (this.formValidate.vipPriceType) {
          //开启会员
          arr = [
            ...specificationsColumns,
            ...imageTableHead,
            ...vipPriceTableHead(this.formValidate.vipPriceType),
            ...GoodsTableHead,
          ];
        } else {
          arr = [...specificationsColumns, ...imageTableHead, ...GoodsTableHead];
        }
      }
      this.$set(this.formValidate, 'header', arr);
      this.tableKey += 1;
      this.columnsInstalM = arr;
    },
    // 生成规格组合
    generateCombinations(arr, prefix = []) {
      if (arr.length === 0) {
        return [prefix];
      }
      const [first, ...rest] = arr;
      return first.detail.flatMap((detail) => this.generateCombinations(rest, [...prefix, detail.value]));
    },
    // 另存为模板
    handleSaveAsTemplate() {
      this.$prompt('', '请输入模板名称', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: (value) => {
          if (value === null) {
            return '输入不能为空';
          }
          if (value.length > 30) return '输入限制30字以内';
        },
      })
        .then(({ value }) => {
          let spec = this.formValidate.attrs.map((item) => {
            return {
              value: item.value,
              detail: item.detail.map((e) => e.value),
            };
          });
          const data = {
            id: 0,
            ruleName: value,
            ruleValue: JSON.stringify(spec),
          };
          attrCreatApi(data)
            .then((res) => {
              this.$message.success('提交成功');
              this.productGetRule();
              this.clear();
              this.loading = false;
              this.loadingBtn = false;
            })
            .catch(() => {
              this.loading = false;
              this.loadingBtn = false;
            });
        })
        .catch(() => {});
    },
    // 在规格中选择规格图片
    handleSelImg(item) {
      const _this = this;
      this.$modalUpload(
        function (img) {
          if (!img) return;
          item.image = img[0].sattDir;
          _this.changeSpecImg([item.value], img[0].sattDir);
        },
        false,
        'content',
      );
    },
    changeSpecImg(arr, img) {
      // 判断是否存在规格图
      let isHas = false;
      for (let i = 1; i < this.ManyAttrValue.length; i++) {
        let item = this.ManyAttrValue[i];
        if (item.image && this.isSubset(item.attr_arr, arr)) {
          isHas = true;
          break;
        }
      }
      if (isHas) {
        this.$confirm('可以同步修改下方该规格图片，确定要替换吗？', '提示', {
          confirmButtonText: '替换',
          cancelButtonText: '暂不',
          type: 'warning',
        })
          .then(() => {
            for (let val of this.ManyAttrValue) {
              if (this.isSubset(val.attr_arr, arr)) {
                this.$set(val, 'image', img);
              }
            }
          })
          .catch(() => {});
      } else {
        for (let val of this.ManyAttrValue) {
          if (this.isSubset(val.attr_arr, arr)) {
            this.$set(val, 'image', img);
          }
        }
      }
      this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
    },
    // 规格图片添加开关
    addPic(e, i) {
      if (e) {
        this.formValidate.attrs.map((item, ii) => {
          if (ii !== i) {
            this.$set(item, 'add_pic', 0);
          }
        });
        this.canSel = false;
      } else {
        this.canSel = true;
      }
    },
    // 生成列表 行 列 数据
    tableCellClassName({ row, column, rowIndex, columnIndex }) {
      //注意这里是解构
      //利用单元格的 className 的回调方法，给行列索引赋值
      row.index = rowIndex || '';
      column.index = columnIndex;
    },
    //返佣输入
    keyupEventBrokerage(val, index, num) {
      switch (num) {
        case 1:
          this.oneFormBatch[index][val] =
            this.oneFormBatch[index][val] > 0 ? parseInt(this.oneFormBatch[index][val]) : 0;
          break;
        case 2:
          this.OneattrValue[index][val] =
            this.OneattrValue[index][val] > 0 ? parseInt(this.OneattrValue[index][val]) : 0;
          break;
        default:
          this.ManyAttrValue[index][val] =
            this.ManyAttrValue[index][val] > 0 ? parseInt(this.ManyAttrValue[index][val]) : 0;
      }
    },
    // 添加规则；
    addRule() {
      const _this = this;
      this.$modalAttr(this.formDynamics, function () {
        _this.productGetRule();
      });
    },
    // 选择规格
    onChangeSpec(num) {
      if (num) {
        this.productGetRule();
        if (this.$route.params.id != 0) this.formValidate.attrs = [];
      } else {
        this.formValidate.attrs = [];
        this.formValidate.attrList = [];
      }
    },
    // 选择属性确认
    confirm(name) {
      this.createBnt = true;
      this.canSel = true;
      this.formValidate.selectRule = name;
      if (!this.formValidate.selectRule) {
        return this.$message.warning('请选择属性');
      }
      const dataAttrList = [];
      const dataAttrs = [];
      this.ruleList.forEach((item) => {
        if (item.ruleName === this.formValidate.selectRule) {
          item.ruleValue.forEach((i) => {
            dataAttrList.push({
              attributeName: i.value,
              optionList: i.detail.map((val) => ({ optionName: val, image: '', sort: 0 })),
              id: 0,
              isShowImage: false,
              sort: i + 1,
            });
            dataAttrs.push({
              value: i.value,
              detail: i.detail.map((val) => ({ value: val, image: '' })),
              id: 0,
              add_pic: 0,
              sort: i + 1,
            });
          });
        }
        this.formValidate.attrList = dataAttrList;
        this.formValidate.attrs = dataAttrs;
      });
      // this.attrs.map((item, ii) => {
      //   this.$set(item, 'isShowImage', false);
      // });
      // this.canSel = true;
      this.generateAttr(this.formValidate.attrs);
    },
    // 获取商品属性模板；
    productGetRule() {
      templateListApi(this.tableFrom).then((res) => {
        const list = res.list;
        for (var i = 0; i < list.length; i++) {
          list[i].ruleValue = JSON.parse(list[i].ruleValue);
        }
        this.ruleList = list;
      });
    },
    // 删除表格中的属性
    delAttrTable(val) {
      for (let i = 0; i < this.ManyAttrValue.length; i++) {
        let item = this.ManyAttrValue[i];
        if (item.attr_arr && item.attr_arr.includes(val)) {
          this.ManyAttrValue.splice(i, 1);
          i--;
        }
      }
    },
    isSubset(arr1, arr2) {
      // 将数组转换为 Set，以便进行高效的包含检查
      const set1 = new Set(arr1);
      const set2 = new Set(arr2);

      // 检查 set2 中的每个元素是否都在 set1 中
      for (let elem of set2) {
        if (!set1.has(elem)) {
          return false;
        }
      }
      return true;
    },
    // 切换默认选中规格
    changeDefaultSelect(e, index) {
      const ManyAttrValues = [...this.ManyAttrValue];
      ManyAttrValues.map((item, i) => {
        if (i !== index) {
          item.isDefault = false;
        }
      });
      if (e) ManyAttrValues[index].isShow = true;
      this.$emit('changeManyAttrValue', [...ManyAttrValues]);
    },
    // 改变是否显示
    changeDefaultShow(index) {
      // 如果默认选中开启 则不可隐藏
      if (this.ManyAttrValue[index].isDefault === true) {
        this.ManyAttrValue[index].isShow = true;
        return this.$message.error('默认规格不可隐藏');
      }
      // 至少显示一个规格
      if (!this.ManyAttrValue.some((item) => item.isShow)) {
        this.ManyAttrValue[index].isShow = true;
        this.$message.error('至少显示一个规格');
      }
    },
    // 清空批量规格信息
    batchDel() {
      this.$emit('handleBatchDel');
    },
    // 批量添加
    batchAdd() {
      let arr = [];
      for (let val of this.formValidate.attrs) {
        if (this.oneFormBatch[0][val.value]) {
          arr.push(this.oneFormBatch[0][val.value]);
        }
      }
      this.ManyAttrValue.forEach((val) => {
        if (val.attrValueShow) {
          if (arr.length) {
            let attrVal = val.attrValueShow;
            //   let data = Object.values(val.attrValueShow).map((item) => item.val);
            if (this.isSubset(Object.values(attrVal), arr)) {
              this.batchData(val);
            }
          } else {
            this.batchData(val);
          }
        }
      });
    },
    // 批量数据
    batchData(val) {
      if (this.oneFormBatch[0].image) this.$set(val, 'image', this.oneFormBatch[0].image);
      if (this.oneFormBatch[0].price > 0) this.$set(val, 'price', this.oneFormBatch[0].price);
      if (this.oneFormBatch[0].cost > 0) this.$set(val, 'cost', this.oneFormBatch[0].cost);
      if (this.oneFormBatch[0].otPrice > 0) this.$set(val, 'otPrice', this.oneFormBatch[0].otPrice);
      if (this.oneFormBatch[0].barCode) this.$set(val, 'barCode', this.oneFormBatch[0].barCode);
      if (this.oneFormBatch[0].stock >= 0) this.$set(val, 'stock', this.oneFormBatch[0].stock);
      if (this.oneFormBatch[0].weight >= 0) this.$set(val, 'weight', this.oneFormBatch[0].weight);
      if (this.oneFormBatch[0].volume >= 0) this.$set(val, 'volume', this.oneFormBatch[0].volume);
      if (this.oneFormBatch[0].brokerage > 0) this.$set(val, 'brokerage', this.oneFormBatch[0].brokerage);
      if (this.oneFormBatch[0].brokerageTwo > 0) this.$set(val, 'brokerageTwo', this.oneFormBatch[0].brokerageTwo);
      if (this.oneFormBatch[0].vipPrice > 0) this.$set(val, 'vipPrice', this.oneFormBatch[0].vipPrice);
      if (this.oneFormBatch[0].itemNumber) this.$set(val, 'itemNumber', this.oneFormBatch[0].itemNumber);
    },
    // 添加按钮
    addBtn() {
      this.clearAttr();
      this.isBtn = true;
    },
    clearAttr() {
      this.formDynamic.attrsName = '';
      this.formDynamic.attrsVal = '';
    },
    // 删除规格
    handleRemoveRole(index) {
      this.formValidate.attrs.splice(index, 1);
      this.ManyAttrValue.splice(index, 1);
      if (!this.formValidate.attrs.length) {
        this.formValidate.header = [];
        this.ManyAttrValue = [];
      } else {
        this.generateAttr(this.formValidate.attrs);
      }
      this.canSel = true;
    },

    // 添加属性
    createAttr(num, idx) {
      if (num) {
        // 判断是否存在同样数据
        var isExist = this.formValidate.attrs[idx].detail.some((item) => item.value === num);
        if (isExist) {
          this.$message.error('规格值已存在');
          return;
        }

        this.formValidate.attrs[idx].detail.push({ value: num, image: '' });
        if (this.ManyAttrValue.length) {
          this.addOneAttr(this.formValidate.attrs[idx].value, num);
        } else {
          this.generateAttr(this.formValidate.attrs);
        }
        this.$refs['popoverRef_' + idx][0].doClose(); //关闭的
        this.clearAttr();
        setTimeout(() => {
          if (this.$refs['popoverRef_' + idx]) {
            //重点是以下两句
            this.$refs['popoverRef_' + idx][0].doShow(); //打开的
            //重点是以上两句
          }
        }, 20);
      } else {
        this.$refs['popoverRef_' + idx][0].doClose(); //关闭的
      }
    },

    //清空卡密
    virtualListClear() {
      // this.$set(this[this.carMytabName][this.carMytabIndex], 'cdkey', []);
      this[this.carMytabName][this.carMytabIndex].cdkeyId = [];
    },
    //添加倒入卡密的值
    changeVirtual(e) {
      this.virtualList = this.virtualList.concat(e);
    },
    closeCloudDisk() {
      this.$refs.cloudDiskRef.cloudDiskShow = false;
    },
    //确认保存云盘链接
    handlerSubSuccessCloudDisk(e) {
      this.cloudDisk = e;
      this[this.carMytabName][this.carMytabIndex].expand = JSON.parse(JSON.stringify(e));
      this.$refs.cloudDiskRef.cloudDiskShow = false;
    },
    //选择卡密库回调
    handlerChangeCdkeyIdSubSuccess(row) {
      this[this.carMytabName][this.carMytabIndex].cdkeyLibraryName = row.name;
      this[this.carMytabName][this.carMytabIndex].cdkeyId = row.id;
      this[this.carMytabName][this.carMytabIndex].stock = row.stock;
    },
    //查看已设置的虚拟商品
    seeVirtually(data, name, index, type) {
      this.carMytabName = name;
      this.carMytabIndex = index;
      if (type === 1) {
        this.cloudDisk = data.expand;
        this[this.carMytabName][this.carMytabIndex].expand = data.expand;
        this.$refs.cloudDiskRef.cloudDiskShow = true;
      } else {
        this.cdkeyLibraryInfo = {
          id: data.cdkeyId,
          name: data.cdkeyLibraryName,
        };
        this.$refs.cdkeyLibraryRef.cdkeyShow = true;
      }
    },
    // 添加虚拟链接
    handleAddVirtually(index, name, type) {
      this.carMytabIndex = index;
      this.carMytabName = name;
      //this.virtualListClear();
      if (type === 1) {
        this.cloudDisk = '';
        this.$refs.cloudDiskRef.cloudDiskShow = true;
      } else {
        this.cdkeyLibraryInfo = {};
        this.$refs.cdkeyLibraryRef.cdkeyShow = true;
      }
    },
    // 修改次卡商品核销时效类型
    changeVerifyTimeType() {
      this.punchCardTimeVal = [];
      this.formValidate.verifyTimeLimitDay = 1;
    },
  },
};
</script>

<style scoped lang="scss">
@keyframes blink {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

.flash {
  border-color: #fbcac3;
  background-color: #feedeb;
  color: var(--prev-color-prompt-color);
  animation: blink 1s infinite;
}
.customize-time {
  &:hover .el-icon-error {
    visibility: visible;
  }
  .el-icon-error {
    visibility: hidden;
    cursor: pointer;
    font-size: 15px;
    color: #999999;
    position: absolute;
    top: -5px;
    right: 6px;
  }
}

.add-time {
  color: var(--prev-color-primary);
  cursor: pointer;
}
.reservation-times-box {
  margin-top: 10px;
  padding: 10px 20px;
  min-width: 700px;
  background-color: #fafafa;
  border-radius: 10px;

  ::v-deep(.el-checkbox__label) {
    font-size: 12px;
  }
}
.el-dropdown-menu {
  border-color: #ebeef5;
  max-height: 650px !important;
  overflow-y: scroll !important;
}

.priceBox {
  width: 100%;
  ::v-deep.el-input__inner {
    text-align: center;
  }
}
// 多规格设置
.addfont {
  display: inline-block;
  font-size: 12px;
  font-weight: 400;
  color: var(--prev-color-primary);
  margin-left: 14px;
  cursor: pointer;
}
.specifications {
  .specifications-item:hover {
    background-color: var(--prev-color-primary-light-9);
  }
  .specifications-item:hover .del {
    display: block;
  }
  .specifications-item:last-child {
    margin-bottom: 14px;
  }
  .specifications-item {
    position: relative;
    display: flex;
    align-items: center;
    padding: 20px 15px;
    transition: all 0.1s;
    background-color: #fafafa;
    margin-bottom: 10px;
    border-radius: 4px;

    .del {
      display: none;
      position: absolute;
      right: 15px;
      top: 15px;
      font-size: 22px;
      color: var(--prev-color-primary);
      cursor: pointer;
      z-index: 9;
    }
    .specifications-item-box {
      position: relative;
      .lineBox {
        position: absolute;
        left: 13px;
        top: 30px;
        width: 30px;
        height: 45px;
        border-radius: 6px;
        border-left: 1px solid #dcdfe6;
        border-bottom: 1px solid #dcdfe6;
      }
      .specifications-item-name {
        .el-icon-info {
          color: var(--prev-color-primary);
          font-size: 12px;
          margin-left: 5px;
        }
      }
      .specifications-item-name-input {
        width: 300px;
      }
    }
  }
}

.rulesBox {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  .item {
    display: flex;
    flex-wrap: wrap;
  }
  .addfont {
    margin-top: 5px;
    margin-left: 0px;
  }
  ::v-deep .el-popover {
    border: none;
    box-shadow: none;
    padding: 0;
    margin-top: 5px;
    line-height: 1.5;
  }
}
.spec {
  display: block;
  margin: 5px 0;
  position: relative;

  .el-icon-error {
    position: absolute;
    display: none;
    right: -3px;
    top: -3px;
    z-index: 9;
    color: var(--prev-color-primary);
  }
}
.img-popover {
  cursor: pointer;
  width: 76px;
  height: 76px;
  padding: 6px;
  margin-top: 12px;
  background-color: #fff;
  position: relative;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  &:hover .img-del {
    display: block;
  }
  .img-del {
    display: none;
    position: absolute;
    right: -4px;
    top: -5px;
    font-size: 16px;
    color: var(--prev-color-primary);
    cursor: pointer;
    z-index: 9;
  }
  .popper {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
  }
  .popper-arrow,
  .popper-arrow:after {
    position: absolute;
    display: block;
    width: 0;
    height: 0;
    border-color: transparent;
    border-style: solid;
  }
  .popper-arrow {
    top: -13px;
    border-top-width: 0;
    border-bottom-color: #dcdfe6;
    border-width: 6px;
    filter: drop-shadow(0 2px 12px rgba(0, 0, 0, 0.03));
    &::after {
      top: -5px;
      margin-left: -6px;
      border-top-width: 0;
      border-bottom-color: #fff;
      content: ' ';
      border-width: 6px;
    }
  }
}
.spec:hover {
  .el-icon-error {
    display: block;
    z-index: 999;
    cursor: pointer;
  }
}
// 多规格设置
.specifications {
  .specifications-item:hover {
    background: var(--prev-color-primary-light-9) !important;
  }
  .specifications-item:hover .del {
    display: block;
  }
  .specifications-item:last-child {
    margin-bottom: 14px;
  }
}
.ml-30 {
  margin-left: 30px;
}
</style>
