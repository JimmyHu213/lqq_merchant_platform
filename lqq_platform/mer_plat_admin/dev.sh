#!/bin/bash
# 使用项目特定的 Node.js v16.16.0 运行开发服务器
# 这样做可以避免更换系统的默认 Node.js 版本

echo "使用 Node.js v16.16.0 启动开发服务器..."
PATH=$(dirname $(n which 16.16.0)):$PATH npm run dev