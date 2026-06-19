# Demo Shop
## 前提条件

開発・実行環境として以下が必要です。

- **Docker Desktop** (または Docker Engine)
- **Visual Studio Code (VSCode)**
  - 拡張機能「Dev Containers」

本リポジトリは VSCode の Dev Containers に対応しています。Dev Containerを使用することで、JavaやBunなどの必要なツールがすべて揃った環境を簡単に構築できます。

## 環境構築

1. このリポジトリをVSCodeで開きます。
2. 画面右下に表示されるポップアップから「Reopen in Container」をクリックするか、コマンドパレット（`Ctrl+Shift+P` または `Cmd+Shift+P`）から `Dev Containers: Reopen in Container` を選択します。
3. Dockerコンテナのビルドが完了するまで待ちます（初回は少し時間がかかります）。

## 実行

1. demoディレクトリに移動します。
2. カレントで以下のコマンド実行して起動できます。
```bash
./mvnw spring-boot:run
```
3. デフォルトだと http://localhost:8080 でアクセスできます。

## ビルド (Spring)

Springのビルド(デプロイ)をします。

1. VSCodeのターミナルを開き、以下のコマンドを実行してください。
```bash
cd demo
./mvnw clean package -DskipTests
```
2. demo/target/xxx.jar が生成されるので、以下のコマンドで実行してください。
```bash
java -jar target/xxx.jar
```

## ビルド（Vue.js）

Vue.jsのビルドをします。

1. VSCodeのターミナルを開き、以下のコマンドを実行してください。
```bash
cd demo/frontend
bun install
bun run build
```
2. 自動的に/demo/src/main/resources/staticにindex.html等が配置されます。