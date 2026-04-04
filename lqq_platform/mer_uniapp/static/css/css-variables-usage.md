# 全局CSS变量使用说明

## 已定义的全局变量

### 屏幕尺寸相关
```css
--screen-height: 100vh;        /* 屏幕高度 */
--screen-width: 100vw;         /* 屏幕宽度 */
--status-bar-height: 0px;      /* 状态栏高度 */
--nav-bar-height: 44px;        /* 导航栏高度 */
```

### 安全区域
```css
--safe-area-inset-top: env(safe-area-inset-top);      /* 顶部安全区域 */
--safe-area-inset-bottom: env(safe-area-inset-bottom); /* 底部安全区域 */
--safe-area-inset-left: env(safe-area-inset-left);    /* 左侧安全区域 */
--safe-area-inset-right: env(safe-area-inset-right);  /* 右侧安全区域 */
```

### 常用间距
```css
--spacing-xs: 8rpx;    /* 超小间距 */
--spacing-sm: 16rpx;   /* 小间距 */
--spacing-md: 24rpx;   /* 中等间距 */
--spacing-lg: 32rpx;   /* 大间距 */
--spacing-xl: 48rpx;   /* 超大间距 */
```

### 常用圆角
```css
--border-radius-sm: 8rpx;   /* 小圆角 */
--border-radius-md: 16rpx;  /* 中等圆角 */
--border-radius-lg: 24rpx;  /* 大圆角 */
--border-radius-xl: 32rpx;  /* 超大圆角 */
```

### 常用阴影
```css
--shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);      /* 小阴影 */
--shadow-md: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);    /* 中等阴影 */
--shadow-lg: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);     /* 大阴影 */
```

## 使用示例

### 1. 设置全屏高度
```css
.full-screen {
  height: var(--screen-height);
  width: var(--screen-width);
}
```

### 2. 适配状态栏
```css
.status-bar-padding {
  padding-top: var(--status-bar-height);
}
```

### 3. 底部安全区域适配
```css
.bottom-safe-area {
  padding-bottom: var(--safe-area-inset-bottom);
}
```

### 4. 使用预定义间距
```css
.card {
  margin: var(--spacing-md);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
}
```

### 5. 响应式布局
```css
.container {
  min-height: calc(var(--screen-height) - var(--status-bar-height) - var(--nav-bar-height));
}
```

## 注意事项

1. 这些变量在 `base.css` 中全局定义
2. H5环境下会动态设置实际值
3. 小程序环境下使用默认值
4. 所有变量都支持在组件中直接使用
5. 可以通过 `var()` 函数在任何CSS属性中使用

## 扩展建议

如需添加新的全局变量，请在 `base.css` 的 `:root` 选择器中添加，并在 `App.vue` 的 `setCSSVariables` 方法中设置动态值。 