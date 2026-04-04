// 在用户列表组件的data中定义
const tableColumns = [
    {
        // ID列
        prop: 'id',
        label: 'ID',
        width: 70,
        align: 'right'
    },
    {
        // 头像列
        label: '头像',
        width: 60,
        type: 'image',
        prop: 'avatar' // 使用插槽自定义显示
    },
    {
        // 昵称列
        label: '昵称',
        minWidth: 180,
        slotName: 'nickname' // 使用插槽自定义显示（包含注销状态）
    },
    {
        // 手机号列
        label: '手机号',
        minWidth: 180,
        prop: 'phone',
        formatter: (row) => {
            // 可以添加手机号格式化逻辑
            return row.phone || '--';
        }
    },
    {
        // 注册类型列
        prop: 'registerType',
        label: '注册类型',
        minWidth: 180,
        formatter: (row) => {
            const registerTypeMap = {
                'wechat': '公众号',
                'routine': '小程序',
                'h5': 'H5',
                'iosWx': '微信ios',
                'androidWx': '微信安卓',
                'ios': 'ios'
            };
            return registerTypeMap[row.registerType] || '--';
        }
    },
    {
        // 操作列
        label: '操作',
        width: 70,
        fixed: 'right',
        slotName: 'operate' // 使用插槽自定义操作按钮
    }
];

export default tableColumns;