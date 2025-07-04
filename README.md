# 微信公众号后台服务：火烧云预测

## 公众号：Echuan

### 使用方法：输入地名，即可查询该地区的火烧云预测

### 项目结构说明：

- `src/main/java/com/tencent/wxcloudrun`：核心代码目录
    - `client`：外部服务调用客户端（如Qwen、Sun等）
    - `controller`：API接口控制器
    - `dao`：数据库访问层
    - `domain`：领域模型和业务逻辑
    - `service`：微信请求处理服务
- `src/main/resources`：资源配置文件
    - `application.yml`：主配置文件
    - `mapper`：MyBatis映射文件

