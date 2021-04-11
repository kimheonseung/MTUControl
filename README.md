# MTUControl




## Jsoup 사용 및 HTTP 연결시 특정 URL 연결 실패인 경우 MTU 조절하여 connect 하기 위한 유틸
- 사용 
```java
/* 빌드 전, 생성자에 네트워크 인터페이스명 기입 필요 */
public MTUChanger() {
    this.ip = NetworkInterfaceUtils.getInstance().getIPv4HostAddressByInterfaceName("eth1");
    this.MTU_CHANGE_COMMAND = getMTUChangeCommand(OSHelper.getInstance().isWindows());
}


/* 576 (Windows), 220 (Linux) ~ 1500 사이에서 호출마다 10씩 증가하여 MTU값 조절 */
/* 1500인 경우 다시 최소값으로 조절됨 */
MTUChanger.getInstance().changeMTU();
```
