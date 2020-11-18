# 服务状态发生变更

{#instance.getRegistration().getName()}的状态变更为{#event.getStatusInfo().getStatus()}

 - 发生时间：{#event.getTimestamp().plusMillis(28800000).toString()}
 - 服务地址：{#instance.getRegistration().getServiceUrl()}

```json
{#objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(#event.getStatusInfo().getDetails())}
```