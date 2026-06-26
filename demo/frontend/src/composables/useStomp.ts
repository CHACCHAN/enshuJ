import { onUnmounted } from 'vue'
import { Client, type StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

// モジュールスコープでクライアントを生成 (null にならない)
// activate() を呼ぶ前に subscribe() しても @stomp/stompjs が内部でキューイングする
const client = new Client({
  webSocketFactory: () => new SockJS('/ws') as WebSocket,
  reconnectDelay: 5000,
})

// 接続完了後に呼ぶコールバック群
const connectCallbacks: Array<() => void> = []

client.onConnect = () => {
  // splice で取り出して全部呼ぶ (以降の再接続では空なので再実行しない)
  connectCallbacks.splice(0).forEach(cb => cb())
}

let refCount = 0

export function useStomp() {
  const subscriptions = new Map<string, StompSubscription>()

  function connect(onConnect?: () => void) {
    refCount++
    if (client.connected) {
      // 既に接続済みなら即コールバック
      onConnect?.()
    } else {
      if (onConnect) connectCallbacks.push(onConnect)
      // まだ activate していなければ開始
      if (!client.active) client.activate()
    }
  }

  function disconnect() {
    refCount = Math.max(0, refCount - 1)
    if (refCount === 0) client.deactivate()
  }

  function subscribe(destination: string, callback: (body: unknown) => void) {
    // client が常に存在するので null チェック不要
    // 未接続でも @stomp/stompjs が内部 _subscribeQueue に積み、接続後に自動送信する
    const sub = client.subscribe(destination, frame => {
      try { callback(JSON.parse(frame.body)) } catch { callback(frame.body) }
    })
    subscriptions.set(destination, sub)
  }

  function unsubscribe(destination: string) {
    subscriptions.get(destination)?.unsubscribe()
    subscriptions.delete(destination)
  }

  function send(destination: string, body: unknown) {
    if (client.connected) {
      client.publish({ destination, body: JSON.stringify(body) })
    } else {
      // 未接続時: 接続後に送信
      connectCallbacks.push(() => {
        client.publish({ destination, body: JSON.stringify(body) })
      })
    }
  }

  onUnmounted(() => {
    subscriptions.forEach(sub => sub.unsubscribe())
    subscriptions.clear()
    disconnect()
  })

  return { connect, disconnect, subscribe, unsubscribe, send }
}
