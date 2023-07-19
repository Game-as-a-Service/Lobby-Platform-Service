package tw.waterballsa.gaas.application.repositories

interface SocketIORepository {

    fun start()

    /**
     * 停止服务
     */
    fun stop()

    /**
     * 推送信息给指定客户端
     *
     * @param userId:     客户端唯一标识
     * @param msgContent: 消息内容
     */
    fun pushMessageToUser(userId: String, msgContent: String)
}