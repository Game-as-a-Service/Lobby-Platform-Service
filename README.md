# Lobby-Platform-Service


<p align="center">
  <a href="https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/"><img src="https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg" alt="Maven Central"></a>
  <a href="https://gitee.com/troyzhxu/bean-searcher/blob/master/LICENSE"><img src="https://img.shields.io/hexpm/l/plug.svg" alt="License"></a>
</p>


## Description / Overview
The Backend of the Game as a Lobby platform.

Lobby-Platform-Service 是一個遊戲大廳平台的後端，提供API和服務，可以讓遊戲開發者輕鬆地將自己的遊戲添加到平台中供玩家使用。該項目主要目的是提供一個統一的遊戲大廳平台，讓玩家可以方便地找到各種類型的遊戲。

## Live Environment

### 前端網頁
- [前端網頁：https://lobby.gaas.waterballsa.tw](https://lobby.gaas.waterballsa.tw)
↗ 上查看我們的示例，該示例演示瞭如何在平台上查找遊戲和創建遊戲房間。

### API文檔
- [後端API：swagger](https://api.gaas.waterballsa.tw/swagger-ui/index.html#/)
  API文檔使用swagger，在API文檔中，你可以找到關於如何使用平台API的詳細信息，包括API調用的參數、請求和響應格式等。

##  Tech Stack

- 後端框架：Spring Boot 
- 後端語言：Kotlin 
- 數據庫：Mongodb 
- API文件：Swagger 
- build tool：Maven 
- CI / CD工具： GitHub Actions
- 部屬環境：AWS EC2 
- ATDD -- 驗收驅動開發，以使用者功能使用的角度寫e2e測試。
- Clean Architecture -- 乾淨架構

## Installation
如果你想在本地運行該項目，你需要做以下幾件事：
```
### 克隆項目到本地
git clone https://github.com/Game-as-a-Service/Lobby-Platform-Service.git

### 進入到項目目錄：
cd Lobby-Platform-Service

### 安裝 
mvn clean install 

### 啟動，找到spring目錄裡的
LobbyPlatformApplication.kt

### 在瀏覽器打開：
http://localhost:8087/swagger-ui/index.html 
```

## Code of Conduct
- 請在提交前確保代碼符合項目的編碼規範。
- 請測試您的代碼，確保沒有錯誤和異常。 
- 請確保您的代碼沒有破壞項目的現有功能，也沒有引入新的問題。 
- 請提交有意義的提交消息，描述你的簡介。


## File Structure
這個項目採用按功能分模塊的開發方式，包含以下三個模塊：

- application：


- domain：包含應用程序的核心業務邏輯和實體類，例如領域模型、服務實現類等。


- spring：包含應用程序的業務邏輯和應用程序的入口點，例如Web應用程序的控制器、服務和客戶端等。 包含應用程序的Spring配置和依賴項，例如數據訪問、事務管理和Web MVC等。



## reference
https://github.com/Game-as-a-Service/Lobby-Platform-Service/discussions/97


# Contributor

<a href="https://github.com/Game-as-a-Service/Lobby-Platform/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Game-as-a-Service/Lobby-Platform" />
</a>

Made with [contrib.rocks](https://contrib.rocks).
