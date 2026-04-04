<template>
  <!--  腾讯地图地址：https://lbs.qq.com/webDemoCenter/glAPI/glEditor/toolDraw-->
  <div>
    <div class="map-controls">
      <!--      <div class="search-box">-->
      <!--        <el-input -->
      <!--          v-model="address" -->
      <!--          @keyup.enter="searchAddress" -->
      <!--          placeholder="输入地址搜索"-->
      <!--          clearable-->
      <!--        >-->
      <!--          <template #append>-->
      <!--            <el-button @click="searchAddress" icon="el-icon-search"></el-button>-->
      <!--          </template>-->
      <!--        </el-input>-->
      <!--      </div>-->
      <div>
        是否开启 ：<el-switch
          :disabled="checkPermi(['admin:merchant:reservation:to:home:edit']) ? false : true"
          v-model="form.serviceAreaSwitch"
          :active-value="true"
          :inactive-value="false"
          active-text="开启"
          inactive-text="关闭"
          @change="handleSave"
      />
      </div>
      <div class="control-buttons">
        <el-button-group v-if="checkPermi(['admin:merchant:reservation:to:home:edit'])">
          <el-button v-show="editorMode === 'edit'" @click="deleteSelectedPolygon" icon="el-icon-delete">
            删除
          </el-button>
          <el-button
              @click="setEditorMode('edit')"
              icon="el-icon-edit-outline"
          >
            编辑
          </el-button>
          <el-button
            @click="setEditorMode('draw')"
            icon="el-icon-edit"
          >
            新增
          </el-button>
          <el-button @click="handleSave" type="primary" icon="el-icon-check">保存</el-button>
        </el-button-group>
      </div>
    </div>
    <div id="container"></div>
    <div class="font14 mt20 from-tips line-height-15">
      <div>
        <p>【绘制模式】鼠标左键点击及移动即可绘制图形，双击结束绘制。可以绘制多个区域</p>
        <p>【编辑模式】可以拖拽顶点调整多边形形状，点击边线添加新顶点，地图会自动定位到绘制区域中心</p>
        <p>【删除功能】编辑模式下选中多边形后点击"删除选中"删除指定区域</p>
        <p>【多区域支持】支持绘制和编辑多个独立的服务区域，每个区域可以单独编辑和删除</p>
        <!--        <p><strong>地址搜索：</strong>输入地址后按回车或点击搜索按钮定位到指定位置</p>-->
        <p>【其他操作】ESC键中断绘制，Ctrl+Z撤销操作，右键撤销绘制点</p>
      </div>
    </div>
  </div>
</template>

<script>
import {checkPermi} from "@/utils/permission";

export default {
  name: 'DrawGeometryMap',
  props: {
    value: {
      type: Object,
      default: function () {
        return {};
      },
    },
  },
  data() {
    return {
      form: this.value,
      address: '',
      map: null,
      editor: null,
      polygon: null,
      searchService: null,
      serviceAreaRange: [], // 存储多个多边形的坐标数组，每个元素是一个多边形的路径数组
      editorMode: 'draw', // 编辑器模式：draw-绘制，edit-编辑
      searchResults: [], // 搜索结果
    };
  },
  mounted() {
    if (this.form.serviceAreaRange) this.serviceAreaRange = JSON.parse(this.form.serviceAreaRange);
    this.initMap();
  },
  methods: {
    checkPermi,
    // 地址搜索功能
    searchAddress() {
      if (!this.address.trim()) {
        this.$message.warning('请输入搜索地址');
        return;
      }

      if (!this.searchService) {
        // 使用备用搜索方案
        this.searchAddressByHTTP();
        return;
      }

      // 显示加载状态
      const loading = this.$loading({
        lock: true,
        text: '搜索中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });

      this.searchService
        .search({
          keyword: this.address,
          page_size: 10,
          page_index: 1,
        })
        .then((result) => {
          loading.close();
          if (result.data && result.data.length > 0) {
            this.searchResults = result.data;
            const firstResult = result.data[0];
            const location = firstResult.location;

            // 移动地图中心到搜索结果
            this.map.setCenter(new window.TMap.LatLng(location.lat, location.lng));
            this.map.setZoom(15);

            // 添加标记点
            this.addSearchMarker(location);

            this.$message.success(`找到 ${result.data.length} 个结果`);
          } else {
            this.$message.warning('未找到相关地址');
          }
        })
        .catch((error) => {
          loading.close();
          console.error('搜索失败：', error);
          this.$message.error('搜索失败，请重试');
        });
    },

    // 添加搜索标记
    addSearchMarker(location) {
      // 清除之前的搜索标记
      if (this.searchMarker) {
        this.searchMarker.setMap(null);
      }

      // 创建新的标记
      this.searchMarker = new window.TMap.MultiMarker({
        map: this.map,
        styles: {
          searchMarker: new window.TMap.MarkerStyle({
            width: 25,
            height: 35,
            anchor: { x: 12, y: 35 },
            src: 'https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/marker.png',
          }),
        },
      });

      this.searchMarker.add({
        id: 'search-result',
        styleId: 'searchMarker',
        position: new window.TMap.LatLng(location.lat, location.lng),
      });
    },

    // 备用HTTP搜索方案
    searchAddressByHTTP() {
      // 显示加载状态
      const loading = this.$loading({
        lock: true,
        text: '搜索中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });

      // 使用腾讯地图WebService API进行搜索
      const url = `https://apis.map.qq.com/ws/place/v1/search`;
      const params = {
        keyword: this.address,
        boundary: 'region(北京,0)',
        page_size: 10,
        page_index: 1,
        key: '3RZBZ-Q6DKF-WLEJ2-N5KCS-KLJX3-5BFQG', // 这里需要替换为实际的API Key
      };

      // 构建查询字符串
      const queryString = Object.keys(params)
        .map((key) => `${key}=${encodeURIComponent(params[key])}`)
        .join('&');

      fetch(`${url}?${queryString}`)
        .then((response) => response.json())
        .then((data) => {
          loading.close();
          if (data.status === 0 && data.data && data.data.length > 0) {
            this.searchResults = data.data;
            const firstResult = data.data[0];
            const location = firstResult.location;

            // 移动地图中心到搜索结果
            this.map.setCenter(new window.TMap.LatLng(location.lat, location.lng));
            this.map.setZoom(15);

            // 添加标记点
            this.addSearchMarker(location);

            this.$message.success(`找到 ${data.data.length} 个结果`);
          } else {
            this.$message.warning('未找到相关地址');
          }
        })
        .catch((error) => {
          loading.close();
          console.error('HTTP搜索失败：', error);
          this.$message.warning('搜索服务暂不可用，请手动在地图上定位');
        });
    },

    // 初始化搜索服务
    initSearchService() {
      try {
        // 尝试初始化搜索服务
        if (window.TMap && window.TMap.service && window.TMap.service.SearchService) {
          this.searchService = new window.TMap.service.SearchService({
            pageSize: 10,
            pageIndex: 1,
          });
          console.log('搜索服务初始化成功');
        } else if (window.TMap && window.TMap.SearchService) {
          this.searchService = new window.TMap.SearchService({
            pageSize: 10,
            pageIndex: 1,
          });
          console.log('搜索服务初始化成功（备用方式）');
        } else {
          console.warn('腾讯地图搜索服务API不可用，将使用HTTP搜索');
          this.searchService = null;
        }
      } catch (error) {
        console.warn('搜索服务初始化失败，将使用HTTP搜索：', error);
        this.searchService = null;
      }
    },

    // 设置编辑器模式
    setEditorMode(mode) {
      this.editorMode = mode;
      if (this.editor) {
        if (mode === 'draw') {
          this.editor.setActionMode(window.TMap.tools.constants.EDITOR_ACTION.DRAW);
        } else if (mode === 'edit') {
          this.editor.setActionMode(window.TMap.tools.constants.EDITOR_ACTION.EDIT);
        }
        // 编辑模式下自动定位到多边形区域
        this.centerToExistingPolygons(mode);
      }
    },
    // 根据已有多边形在地图上定位
    centerToExistingPolygons(mode) {
      try {
        if (!this.polygon || !this.polygon.geometries || this.polygon.geometries.length === 0) return;

        // 优先使用第一个多边形进行定位
        const first = this.polygon.geometries[0];
        if (!first || !first.paths || first.paths.length === 0) return;

        // 编辑模式使用更合适的视野，非编辑模式只居中不改变缩放
        if (mode === 'edit') {
          this.fitMapToPolygon(first.paths);
        } else {
          this.centerMapToPolygon(first.paths);
        }
      } catch (e) {
        console.warn('centerToExistingPolygons error:', e);
      }
    },

    // 删除选中的多边形
    deleteSelectedPolygon() {
      if (this.editor) {
        try {
          this.editor.delete();
          // 更新坐标数据
          this.updateServiceAreaRange();
          this.$message.success('已删除选中的多边形');
        } catch (error) {
          console.error('删除失败：', error);
          this.$message.error('删除失败，请重试');
        }
      }
    },

    // 更新服务区域坐标数据
    updateServiceAreaRange() {
      if (this.polygon && this.polygon.geometries && this.polygon.geometries.length > 0) {
        // 获取所有多边形的坐标，每个多边形作为一个独立的路径数组
        this.serviceAreaRange = [];
        this.polygon.geometries.forEach((geometry) => {
          if (geometry.paths && geometry.paths.length > 0) {
            this.serviceAreaRange.push(geometry.paths);
          }
        });
      } else {
        this.serviceAreaRange = [];
      }
      console.log('更新后的坐标数据：', this.serviceAreaRange);
    },

    // 确保样式一致性
    ensureConsistentStyle() {
      if (this.polygon && this.polygon.geometries) {
        this.polygon.geometries.forEach((geometry) => {
          // 强制设置样式为透明蓝色
          try {
            geometry.setStyleId('polygonStyle');
            // 如果setStyleId不可用，尝试直接设置样式
            if (geometry.setStyle) {
              geometry.setStyle(
                new window.TMap.PolygonStyle({
                  color: '#3777FF',
                  opacity: 0.3,
                  borderColor: '#3777FF',
                  borderWidth: 2,
                }),
              );
            }
          } catch (error) {
            console.warn('设置样式失败：', error);
          }
        });
      }
    },

    // 保存
    handleSave() {
      // 将多个多边形数据转换为JSON字符串存储
      this.form.serviceAreaRange = JSON.stringify(this.serviceAreaRange);

      this.$emit('input', this.form);
      this.$emit('getServiceAreaRange', this.serviceAreaRange);
      this.editorMode = 'draw'

      // this.$message.success(`已保存 ${this.serviceAreaRange.length} 个多边形区域`);
    },
    // 绘制已有的多边形
    drawExistingPolygon(polygon) {
      try {
        // 检查数据结构，支持两种格式：
        // 1. 旧格式：直接是坐标数组 [[lat, lng], [lat, lng], ...]
        // 2. 新格式：多个多边形的数组 [[[lat, lng], [lat, lng], ...], [[lat, lng], [lat, lng], ...]]
        let polygonData = this.serviceAreaRange;

        // 如果是旧格式（直接是坐标数组），转换为新格式
        if (polygonData.length > 0 && polygonData[0].length === 2 && typeof polygonData[0][0] === 'number') {
          polygonData = [polygonData];
        }

        // 绘制每个多边形
        polygonData.forEach((polygonPaths, index) => {
          if (polygonPaths && polygonPaths.length > 0) {
            // 将坐标数组转换为TMap格式的路径
            const paths = polygonPaths.map((coord) => {
              return new window.TMap.LatLng(coord.lat || coord[1], coord.lng || coord[0]);
            });

            // 添加多边形到地图
            polygon.add({
              id: `existing-polygon-${index}`,
              styleId: 'polygonStyle', // 使用默认样式
              paths: paths,
            });
          }
        });

        // 编辑时自动定位到第一个多边形区域，但不放大
        if (polygonData.length > 0 && polygonData[0].length > 0) {
          const firstPolygonPaths = polygonData[0].map((coord) => {
            return new window.TMap.LatLng(coord.lat || coord[1], coord.lng || coord[0]);
          });
          this.centerMapToPolygon(firstPolygonPaths);
        }
      } catch (error) {
        console.error('绘制现有多边形失败：', error);
      }
    },

    // 将地图中心定位到多边形，但不改变缩放级别
    centerMapToPolygon(paths) {
      if (!paths || paths.length === 0) return;

      try {
        // 计算多边形的中心点
        let totalLat = 0;
        let totalLng = 0;

        paths.forEach((point) => {
          totalLat += point.lat;
          totalLng += point.lng;
        });

        const centerLat = totalLat / paths.length;
        const centerLng = totalLng / paths.length;

        // 只移动地图中心，不改变缩放级别
        this.map.setCenter(new window.TMap.LatLng(centerLat, centerLng));

        console.log('编辑模式：地图已定位到多边形中心', {
          centerLat: centerLat.toFixed(6),
          centerLng: centerLng.toFixed(6),
          currentZoom: this.map.getZoom(),
        });
      } catch (error) {
        console.error('定位地图中心失败：', error);
      }
    },

    // 调整地图视图以适应多边形
    fitMapToPolygon(paths) {
      if (!paths || paths.length === 0) return;

      try {
        // 计算多边形的边界
        let minLat = paths[0].lat;
        let maxLat = paths[0].lat;
        let minLng = paths[0].lng;
        let maxLng = paths[0].lng;

        paths.forEach((point) => {
          minLat = Math.min(minLat, point.lat);
          maxLat = Math.max(maxLat, point.lat);
          minLng = Math.min(minLng, point.lng);
          maxLng = Math.max(maxLng, point.lng);
        });

        // 添加适当的边距缓冲区，让多边形周围有足够的空间
        const padding = Math.max(0.005, ((maxLat - minLat + maxLng - minLng) / 4) * 0.3); // 动态边距
        const paddedMinLat = minLat - padding;
        const paddedMaxLat = maxLat + padding;
        const paddedMinLng = minLng - padding;
        const paddedMaxLng = maxLng + padding;

        // 计算中心点
        const centerLat = (paddedMinLat + paddedMaxLat) / 2;
        const centerLng = (paddedMinLng + paddedMaxLng) / 2;

        // 计算合适的缩放级别 - 针对编辑模式优化
        const latDiff = paddedMaxLat - paddedMinLat;
        const lngDiff = paddedMaxLng - paddedMinLng;
        const maxDiff = Math.max(latDiff, lngDiff);

        let zoom = 12; // 编辑模式默认缩放级别
        if (maxDiff > 0.3) zoom = 9; // 很大范围
        else if (maxDiff > 0.15) zoom = 10; // 大范围
        else if (maxDiff > 0.08) zoom = 11; // 中等范围
        else if (maxDiff > 0.04) zoom = 12; // 小范围
        else if (maxDiff > 0.02) zoom = 13; // 较小范围
        else if (maxDiff > 0.01) zoom = 14; // 很小范围
        else zoom = 15; // 极小范围

        // 设置地图中心点和缩放级别
        this.map.setCenter(new window.TMap.LatLng(centerLat, centerLng));
        //  this.map.setZoom(zoom);

        console.log('编辑模式：地图已调整到多边形位置', {
          centerLat: centerLat.toFixed(6),
          centerLng: centerLng.toFixed(6),
          zoom,
          polygonSize: maxDiff.toFixed(6),
        });
      } catch (error) {
        console.error('调整地图视图失败：', error);
      }
    },
    initMap() {
      // 初始化地图
      this.map = new window.TMap.Map('container', {
        zoom: 7, // 设置地图缩放级别，降低默认缩放级别
        center: new window.TMap.LatLng(39.984104, 116.307503), // 设置地图中心点坐标
      });

      // 初始化搜索服务
      this.initSearchService();

      // 初始化几何图形及编辑器
      this.polygon = new window.TMap.MultiPolygon({
        map: this.map,
      });

      this.editor = new window.TMap.tools.GeometryEditor({
        // TMap.tools.GeometryEditor 文档地址：https://lbs.qq.com/webApi/javascriptGL/glDoc/glDocEditor
        map: this.map, // 编辑器绑定的地图对象
        overlayList: [
          // 可编辑图层 文档地址：https://lbs.qq.com/webApi/javascriptGL/glDoc/glDocEditor#4
          {
            overlay: this.polygon,
            id: 'polygon',
          },
        ],
        actionMode: window.TMap.tools.constants.EDITOR_ACTION.DRAW, // 编辑器的工作模式
        activeOverlayId: 'polygon', // 激活图层
        selectable: true, // 开启点选功能
        snappable: true, // 开启吸附
        // 禁用默认的选中样式，使用自定义样式
        selectedStyle: null,
        // 设置编辑时的样式
        editStyle: null,
      });

      // 如果有已有的坐标数据，先绘制到地图上
      if (this.serviceAreaRange && this.serviceAreaRange.length > 0) {
        this.drawExistingPolygon(this.polygon);
        // 初次加载时根据当前模式定位
        this.centerToExistingPolygons(this.editorMode);
      }

      // 监听绘制结束事件，获取绘制几何图形
      this.editor.on('draw_complete', (geometry) => {
        var id = geometry.id;

        if (this.editor.getActiveOverlay().id === 'polygon') {
          // 获取多边形顶点坐标
          var geo = this.polygon.geometries.filter(function (item) {
            return item.id === id;
          });

          if (geo.length > 0) {
            // 将新绘制的多边形添加到数组中,绘制的多边形坐标:geo[0].paths
            this.serviceAreaRange.push(geo[0].paths);
            console.log('当前所有多边形：', this.serviceAreaRange);
          }
        }
      });

      // 监听编辑事件
      this.editor.on('geometry_changed', (geometry) => {
        if (this.editor.getActiveOverlay().id === 'polygon') {
          var geo = this.polygon.geometries.filter(function (item) {
            return item.id === geometry.id;
          });
          if (geo.length > 0) {
            // 找到对应的多边形在数组中的索引并更新
            const geometryIndex = this.polygon.geometries.findIndex((item) => item.id === geometry.id);
            if (geometryIndex !== -1) {
              // 编辑后的多边形坐标： geo[0].paths, 当前所有多边形： this.serviceAreaRange
              this.serviceAreaRange[geometryIndex] = geo[0].paths;
              // 编辑过程中也动态保持视图合适
              this.centerToExistingPolygons('edit');
            }
          }
        }
      });

      // 监听删除事件 多边形已删除:geometry
      this.editor.on('geometry_removed', (geometry) => {
        this.updateServiceAreaRange();
      });
    },
  },
};
</script>
<style>
html,
body {
  height: 100%;
  margin: 0px;
  padding: 0px;
}

#container {
  width: 100%;
  height: 80%;
}

.map-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 10px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  margin-right: 20px;
}

.control-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

#toolControl {
  position: absolute;
  top: 10px;
  left: 0px;
  right: 0px;
  margin: auto;
  width: 252px;
  z-index: 1001;
}

.toolItem {
  width: 30px;
  height: 30px;
  float: left;
  margin: 1px;
  padding: 4px;
  border-radius: 3px;
  background-size: 30px 30px;
  background-position: 4px 4px;
  background-repeat: no-repeat;
  box-shadow: 0 1px 2px 0 #e4e7ef;
  background-color: #ffffff;
  border: 1px solid #ffffff;
}

.toolItem:hover {
  border-color: #789cff;
}

.active {
  border-color: #d5dff2;
  background-color: #d5dff2;
}

.marker {
  background-image: url('https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/marker_editor.png');
}

.polyline {
  background-image: url('https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/polyline.png');
}

.polygon {
  background-image: url('https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/polygon.png');
}

.circle {
  background-image: url('https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/circle.png');
}

.rectangle {
  background-image: url('https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/rectangle.png');
}

.ellipse {
  background-image: url('https://mapapi.qq.com/web/lbs/javascriptGL/demo/img/ellipse.png');
}
</style>
