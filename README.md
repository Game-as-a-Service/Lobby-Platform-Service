# Lobby-Platform-Service

<p style="text-align:center;">
    <a href="">
        <img src="https://github.com/Game-as-a-Service/Lobby-Platform-Service/actions/workflows/build-and-deploy.yml/badge.svg" alt="badge">
    </a>
    <a href="https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/">
        <img src="https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg" alt="Maven Central">
    </a>
    <a href="https://lobby.gaas.waterballsa.tw">
        <img src="https://img.shields.io/website?url=https%3A%2F%2Fapi.gaas.waterballsa.tw%2Fhealth&label=service%20status" alt="Website">
    </a>
    <a href="https://github.com/Game-as-a-Service/Lobby-Platform-Service/releases">
        <img src="https://img.shields.io/github/release/Game-as-a-Service/Lobby-Platform-Service.svg" alt="GitHub Release">
    </a>
    <a href="https://github.com/Game-as-a-Service/Lobby-Platform-Service/blob/main/LICENSE">
        <img src="https://img.shields.io/hexpm/l/plug.svg" alt="License">
    </a>
    <a href="https://github.com/Game-as-a-Service/Lobby-Platform-Service/blob/main/CODE_OF_CONDUCT.md">
        <img src="https://img.shields.io/badge/code%20of-conduct-ff69b4.svg?style=flat" alt="Code of Conduct ">
    </a>
    <a href="https://api.gaas.waterballsa.tw/swagger-ui/index.html">
        <img src="https://img.shields.io/swagger/valid/3.0?specUrl=https%3A%2F%2Fapi.gaas.waterballsa.tw%2Fswagger-ui%2Fapi-docs" alt="Swagger Validator">
    </a>
    <a href="https://discord.gg/waterballsa">
        <img src="https://img.shields.io/discord/937992003415838761?label=Discord" alt="Discord">
    </a>
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

## Environment

- JDK 1.17 +
- Maven 3.5 +
- IntelliJ IDEA  2023.2 +
- Mongodb  4.0.2 +
- 部屬環境：AWS EC2
- ATDD -- 驗收驅動開發，以使用者功能使用的角度寫e2e測試。
- Clean Architecture -- 乾淨架構

## Tech Stack

| Tech Stack                       | Version | Description                                                                       |
|----------------------------------|:-------:|-----------------------------------------------------------------------------------|
| Spring Boot                      | 2.7.10  | Spring Boot 是一個個基於Java 的開源框架。 主要是為了簡化Spring框架，並做到自動配置。 |
| Spring-boot-starter-data-mongodb |    -    | Spring Boot連結Mongodb的服務的套件                                                |
| Springdoc-openapi                |    -    | Spring 整合 swagger的服務套件                                                     |
| Docker                           |    -    | 一種Container實現的技術                                                           |

## Installation

如果你想在本地運行該項目，你需要做以下幾件事：

```cmd
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


## File Structure

這個項目採用DDD分模塊的開發方式，遵循根據領域模型來創建軟體系統的開發理念，將業務邏輯和實體類與應用程序的其他部分分離。
在DDD中，通常會將應用程序分為三個層次：應用層、領域層和基礎設施層。以實現分離關注點的目的。

- Application Layer（應用層）：負責協調應用程序的各個部分，將用戶的輸入轉換為對領域層的調用，並將領域層返回的結果轉換為用戶可以理解的格式。在Spring中，應用層通常由控制器和服務實現類組成。

- Domain Layer（領域層）：包含應用程序的核心業務邏輯和實體類。在領域層中，將根據領域模型來創建實體類、值對象和聚合根等，實現業務邏輯。在Spring中，領域層通常由領域模型和服務實現類組成。

- spring-Infrastructure Layer（基礎設施層）：包含應用程序的基礎設施代碼，及應用程序的業務邏輯和應用程序的入口點。 包含應用程序的Spring配置和依賴項，例如數據訪問、事務管理和Web MVC等基礎建設。在Spring中，基礎設施層通常由數據訪問、事務管理和Web MVC等組件組成。

## reference

<https://github.com/Game-as-a-Service/Lobby-Platform-Service/discussions/97>

## Contributor

<a href="https://github.com/Game-as-a-Service/Lobby-Platform/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Game-as-a-Service/Lobby-Platform"  alt="Contributor"/>
</a>

Made with [contrib.rocks](https://contrib.rocks).
