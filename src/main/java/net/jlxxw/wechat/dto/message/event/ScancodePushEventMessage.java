package net.jlxxw.wechat.dto.message.event;

import net.jlxxw.wechat.dto.message.AbstractWeiXinMessage;

/**
 * 扫码推事件的事件推送
 * @author chunyang.leng
 * @date 2021-12-17 6:58 下午
 */
public class ScancodePushEventMessage extends AbstractWeiXinMessage {

    /**
     * 事件KEY值，由开发者在创建菜单时设定
     */
    private String eventKey;

    /**
     * 扫描信息
     */
    private ScanCodeInfo scanCodeInfo;

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public ScanCodeInfo getScanCodeInfo() {
        return scanCodeInfo;
    }

    public void setScanCodeInfo(ScanCodeInfo scanCodeInfo) {
        this.scanCodeInfo = scanCodeInfo;
    }
}
